package org.down.core.utils;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.down.core.boot.HttpDownBootstrap;
import org.down.core.entity.HttpHeadersInfo;
import org.down.core.entity.HttpRequestInfo;
import org.down.core.entity.HttpResponseInfo;
import org.down.core.exception.BootstrapResolveException;
import org.down.core.proxy.ProxyConfig;
import org.down.core.proxy.ProxyHandleFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.resolver.NoopAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.SSLException;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * <p>{@link HttpDownUtil}</p>
 *
 * @author 白菜
 * @since Created in 2019/12/3 16:45
 */
@Slf4j
public class HttpDownUtil {

    private static SslContext context;

    public static SslContext getSslContext() throws SSLException {
        if (Objects.isNull(context)) {
            synchronized (HttpDownUtil.class) {
                if (Objects.isNull(context)) {
                    context = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                }
            }
        }
        return context;
    }

    public static HttpResponseInfo getHttpResponseInfo(HttpRequest httpRequest, HttpHeaders heads, ProxyConfig proxyConfig,
                                                       NioEventLoopGroup loopGroup) throws Exception {
        HttpResponse response = null;
        if (Objects.isNull(heads)) {
            response = getResponse(httpRequest, proxyConfig, loopGroup);
            // 处理重定向
            if ((response.status().code() + "").indexOf(30) == 0) {
                String redirectUrl = response.headers().get(HttpHeaderNames.LOCATION);
                HttpRequestInfo request = (HttpRequestInfo) httpRequest;
                // 重定向cookie设置
                List<String> cookies = response.headers().getAll(HttpHeaderNames.SET_COOKIE);
                if (Objects.nonNull(cookies) && !cookies.isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    String oldCookie = request.headers().get(HttpHeaderNames.COOKIE);
                    if (StringUtils.isNotBlank(oldCookie)) {
                        builder.append(oldCookie);
                    }
                    String split = (char) HttpConstants.SEMICOLON + String.valueOf(HttpConstants.SP_CHAR);
                    for (String cookie : cookies) {
                        String c = cookie.split(split)[0];
                        if (builder.length() > 0) {
                            builder.append(split);
                        }
                        builder.append(c);
                    }
                    request.headers().set(HttpHeaderNames.COOKIE, builder.toString());
                }
                request.headers().remove(HttpHeaderNames.HOST);
                request.setUri(redirectUrl);
                ProtoUtil.RequestProto requestProtocol = ProtoUtil.getRequestProto(request);
                assert requestProtocol != null;
                request.headers().set(HttpHeaderNames.HOST, requestProtocol.getHost());
                request.setRequestProto(requestProtocol);
                return getHttpResponseInfo(httpRequest, null, proxyConfig, loopGroup);
            }
        }
        if (Objects.isNull(response)) {
            response = getResponse(httpRequest, proxyConfig, loopGroup);
        }
        if (HttpResponseStatus.OK.code() != response.status().code()
                && HttpResponseStatus.PARTIAL_CONTENT.code() != response.status().code()) {
            throw new BootstrapResolveException("Status code exception:" + response.status().code());
        }
        return parseResponse(httpRequest, response);
    }

    private static HttpResponse getResponse(HttpRequest httpRequest,
                                               ProxyConfig proxyConfig, NioEventLoopGroup loopGroup) throws Exception {
        final HttpResponse[] responses = new HttpResponse[1];
        CountDownLatch latch = new CountDownLatch(1);
        HttpRequestInfo request = (HttpRequestInfo) httpRequest;
        ProtoUtil.RequestProto requestProto = request.getRequestProto();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        if (Objects.nonNull(proxyConfig)) {
                            pipeline.addLast(ProxyHandleFactory.build(proxyConfig));
                        }
                        if (requestProto.isSsl()) {
                            pipeline.addLast(getSslContext().newHandler(channel.alloc(), requestProto.getHost(), requestProto.getPort()));
                        }
                        pipeline.addLast("httpCodec", new HttpClientCodec());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                if (msg instanceof HttpResponse) {
                                    HttpResponse httpResponse = (HttpResponse) msg;
                                    responses[0] = httpResponse;
                                    ctx.channel().close();
                                    latch.countDown();
                                }
                            }
                        });
                    }
                });
        if (Objects.nonNull(proxyConfig)) {
            // 代理服务器解析DNS和连接
            bootstrap.resolver(NoopAddressResolverGroup.INSTANCE);
        }
        ChannelFuture channelFuture = bootstrap.connect(requestProto.getHost(), requestProto.getPort());
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // 请求下载一个字节测试是否支持断点下载
                httpRequest.headers().set(HttpHeaderNames.RANGE, "bytes=0-0");
                channelFuture.channel().writeAndFlush(httpRequest);
                if (Objects.nonNull(request.getContent())) {
                    // 请求体写入
                    HttpContent content = new DefaultLastHttpContent();
                    content.content().writeBytes(request.getContent());
                    channelFuture.channel().writeAndFlush(content);
                }
            } else {
                latch.countDown();
            }
        });
        latch.await(60, TimeUnit.SECONDS);
        if (Objects.isNull(responses[0])) {
            throw new TimeoutException("getResponse timeout");
        }
        httpRequest.headers().remove(HttpHeaderNames.RANGE);
        return responses[0];
    }

    private static HttpResponseInfo parseResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpResponseInfo response = new HttpResponseInfo();
        response.setFileName(getDownFileName(httpRequest, httpResponse.headers()));
        response.setTotalSize(getDownFileSize(httpResponse.headers()));
        // chunked编码不支持断点下载
        if (httpResponse.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
            // 206表示支持断点下载
            if (HttpResponseStatus.PARTIAL_CONTENT.equals(httpResponse.status())) {
                response.setSupportRange(true);
            }
        }
        return response;
    }

    private static String getDownFileName(HttpRequest httpRequest, HttpHeaders headers) {
        String fileName = null;
        String disposition = headers.get(HttpHeaderNames.CONTENT_DISPOSITION);
        if (StringUtils.isNotBlank(disposition)) {
            Pattern pattern = compile("^.*filename\\*?=\"?(?:.*'')?([^\"]*)\"?$");
            Matcher matcher = pattern.matcher(disposition);
            if (matcher.find()) {
                char[] chars = matcher.group(1).toCharArray();
                byte[] bytes = new byte[chars.length];
                // netty将byte转成了char，导致中文乱码 HttpObjectDecoder(:803)
                for (int i = 0; i < chars.length; i++) {
                    bytes[i] = ( byte) chars[i];
                }
                fileName = new String(bytes, StandardCharsets.UTF_8);
                try {
                    URLDecoder.decode(fileName, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        if (StringUtils.isBlank(fileName)) {
            Pattern pattern = compile("^.*/([^/?]*\\.[^./?]+)(\\?[^?]*)?$");
            Matcher matcher = pattern.matcher(httpRequest.uri());
            if (matcher.find()) {
                fileName = matcher.group(1);
            } else {
                fileName = httpRequest.uri().substring(httpRequest.uri().lastIndexOf("/") + 1);
            }
        }
        return fileName;
    }

    private static long getDownFileSize(HttpHeaders headers) {
        String contentRange = headers.get(HttpHeaderNames.CONTENT_RANGE);
        if (StringUtils.isNotBlank(contentRange)) {
            Pattern pattern = compile("^.*/(\\d+).*$");
            Matcher matcher = pattern.matcher(contentRange);
            if (matcher.find()) {
                return Long.parseLong(matcher.group(1));
            }
        } else {
            String contentLength = headers.get(HttpHeaderNames.CONTENT_LENGTH);
            if (StringUtils.isNotBlank(contentLength)) {
                return Long.parseLong(contentLength);
            }
        }
        return 0;
    }

    public static void safeClose(Channel channel, Closeable... fileChannels) throws IOException {
        if (Objects.nonNull(channel) && channel.isOpen()) {
            // 关闭旧的下载连接
            channel.close();
        }
        if (Objects.nonNull(fileChannels) && fileChannels.length > 0) {
            for (Closeable closeable : fileChannels) {
                if (Objects.nonNull(closeable)) {
                    closeable.close();
                }
            }
        }
    }

    public static ProtoUtil.RequestProto parseRequestProto(URL url) {
        int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
        ProtoUtil.RequestProto requestProto = new ProtoUtil.RequestProto();
        requestProto.setHost(url.getHost()).setPort(port).setSsl("https".equalsIgnoreCase(url.getProtocol()));
        return requestProto;
    }

    public static HttpRequestInfo buildRequest(String method, String url, Map<String, String> heads,
                                               String body) throws MalformedURLException {
        URL u = new URL(url);
        HttpHeadersInfo headersInfo = new HttpHeadersInfo();
        headersInfo.add("Host", u.getHost());
        headersInfo.add("Connection", "keep-alive");
        headersInfo.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36");
        headersInfo.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headersInfo.add("Referer", u.getHost());
        if (Objects.nonNull(heads)) {
            for (Map.Entry<String, String> entry : headersInfo) {
                headersInfo.set(entry.getKey(), StringUtils.isBlank(entry.getValue()) ? "" : entry.getValue());
            }
        }
        byte[] content = null;
        if (StringUtils.isNotBlank(body)) {
            content = body.getBytes(StandardCharsets.UTF_8);
            headersInfo.add("Content-Length", content.length);
        }
        HttpMethod httpMethod = StringUtils.isNotBlank(method) ? HttpMethod.valueOf(method.toUpperCase()) : HttpMethod.GET;
        HttpRequestInfo requestInfo = new HttpRequestInfo(headersInfo, HttpRequestInfo.HttpVer.HTTP_1_1, u.getFile(), httpMethod, content);
        requestInfo.setRequestProto(parseRequestProto(u));
        return requestInfo;
    }

    public static HttpRequestInfo buildRequest(String url, Map<String, String> heads, String body)
            throws MalformedURLException {
        return buildRequest(null, url, heads, body);
    }

    public static HttpRequestInfo buildRequest(String url, Map<String, String> heads)
            throws MalformedURLException {
        return buildRequest(url, heads, null);
    }

    public static HttpRequestInfo buildRequest(String url)
            throws MalformedURLException {
        return buildRequest(url, null);
    }

    public static String getUrl(HttpRequest request) {
        String host = request.headers().get(HttpHeaderNames.HOST);
        String url;
        if (request.uri().indexOf("/") == 0) {
            if (request.uri().length() > 1) {
                url = host + request.uri();
            } else {
                url = host;
            }
        } else {
            url = request.uri();
        }
        return url;
    }

    public static String getTaskFilePath(HttpDownBootstrap httpDownBootstrap) {
        return httpDownBootstrap.getDownConfig().getFilePath() + File.separator + httpDownBootstrap.getResponse().getFileName();
    }

    public static void main(String[] args) throws Exception {
        String url = "http://file.360adtrade.com/dcp/folder/a47bf730-7c67-4083-9425-dc3e0c46d7ec/bj_zhengbing_F_3D/Video_bj_zhengbing_F_3D.mxf";
        HttpRequestInfo requestInfo = buildRequest(url);
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(1);
        HttpResponseInfo response = getHttpResponseInfo(requestInfo, null, null, loopGroup);
        System.out.println(response);
        loopGroup.shutdownGracefully();
    }
}
