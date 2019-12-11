package org.down.core.entity;

import org.down.core.utils.ProtoUtil.RequestProto;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * <p>{@link HttpRequestInfo}</p>
 * http 请求信息
 * @author 白菜
 * @since Created in 2019/12/3 15:17
 */
@Data
@Accessors(chain = true)
public class HttpRequestInfo implements HttpRequest, Serializable {

    private static final long serialVersionUID = -748095229561299497L;

    private RequestProto requestProto;
    private HttpHeadersInfo headers;
    private HttpVer version;
    private String uri;
    private String method;
    private byte[] content;

    public HttpRequestInfo(HttpHeadersInfo headers, HttpVer version, String uri, HttpMethod method, byte[] content) {
        this.headers = headers;
        this.version = version;
        this.uri = uri;
        this.method = method.toString();
        this.content = content;
    }

    @Override
    public HttpMethod getMethod() {
        return method();
    }

    @Override
    public HttpMethod method() {
        return new HttpMethod(method);
    }

    @Override
    public HttpRequest setMethod(HttpMethod httpMethod) {
        this.method = httpMethod.toString();
        return this;
    }

    @Override
    @Deprecated
    public String getUri() {
        return uri();
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public HttpRequest setUri(String uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public HttpVersion getProtocolVersion() {
        return getProtocolVersion();
    }

    @Override
    public HttpVersion protocolVersion() {
        if (HttpVer.HTTP_1_0 == version) {
            return HttpVersion.HTTP_1_0;
        }
        return HttpVersion.HTTP_1_1;
    }

    @Override
    public HttpRequest setProtocolVersion(HttpVersion httpVersion) {
        if (httpVersion.minorVersion() == 0) {
            this.version =HttpVer.HTTP_1_0;
        } else {
            this.version = HttpVer.HTTP_1_1;
        }
        return this;
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public DecoderResult getDecoderResult() {
        return null;
    }

    @Override
    public DecoderResult decoderResult() {
        return null;
    }

    @Override
    public void setDecoderResult(DecoderResult decoderResult) {

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(method());
        builder.append(' ');
        builder.append(uri());
        builder.append(' ');
        builder.append(protocolVersion());
        builder.append('\n');
        if (Objects.nonNull(headers)) {
            for (Map.Entry<String, String> header : headers) {
                builder.append(header.getKey());
                builder.append(": ");
                builder.append(header.getValue());
                builder.append('\n');
            }
            builder.setLength(builder.length() - 1);
        }
        if (Objects.nonNull(content)) {
            builder.append(new String(content, StandardCharsets.UTF_8));
        }
        return builder.toString();
    }

    public enum HttpVer {
        /* HTTP 版本 */
        HTTP_1_0, HTTP_1_1
    }
}
