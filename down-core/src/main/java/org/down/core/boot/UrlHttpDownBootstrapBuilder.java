package org.down.core.boot;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.down.core.entity.HttpRequestInfo;
import org.down.core.entity.HttpResponseInfo;
import org.down.core.exception.BootstrapBuildException;
import org.down.core.utils.HttpDownUtil;

import java.util.Map;
import java.util.Objects;

/**
 * <p>{@link UrlHttpDownBootstrapBuilder}</p>
 *
 * @author 白菜
 * @since Created in 2019/12/4 11:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UrlHttpDownBootstrapBuilder extends HttpDownBootstrapBuilder {

    private String method;
    private String url;
    private Map<String, String> heads;
    private String body;

    @Override
    public HttpDownBootstrap build() {
        try {
            if (Objects.isNull(getRequest())) {
                HttpRequestInfo request = HttpDownUtil.buildRequest(method, url, heads, body);
                setRequest(request);
            }
            if (Objects.isNull(getLoopGroup())) {
                setLoopGroup(new NioEventLoopGroup(1));
            }
            if (Objects.isNull(getResponse())) {
                HttpResponseInfo response = HttpDownUtil.getHttpResponseInfo(getRequest(), null, getProxyConfig(), getLoopGroup());
                setResponse(response);
            }
        } catch (Exception e) {
            if (Objects.nonNull(getLoopGroup())) {
                getLoopGroup().shutdownGracefully();
            }
            throw new BootstrapBuildException("build URLHttpDownBootstrap error", e);
        }
        return super.build();
    }
}
