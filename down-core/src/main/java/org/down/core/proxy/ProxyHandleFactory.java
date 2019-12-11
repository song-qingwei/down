package org.down.core.proxy;

import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * <p>{@link ProxyHandleFactory}</p>
 *
 * @author 白菜
 * @since Created in 2019/12/3 16:59
 */
public class ProxyHandleFactory {

    public static ProxyHandler build(ProxyConfig config) {
        ProxyHandler handler = null;
        if (Objects.nonNull(config)) {
            boolean isAuth = StringUtils.isNotBlank(config.getUser()) && StringUtils.isNotBlank(config.getPwd());
            InetSocketAddress socketAddress = new InetSocketAddress(config.getHost(), config.getPort());
            switch (config.getProxyType()) {
                case HTTP:
                    if (isAuth) {
                        handler = new HttpProxyHandler(socketAddress, config.getUser(), config.getPwd());
                    } else {
                        handler = new HttpProxyHandler(socketAddress);
                    }
                    break;
                case SOCKS4:
                    handler = new Socks4ProxyHandler(socketAddress);
                    break;
                case SOCKS5:
                    if (isAuth) {
                        handler = new Socks5ProxyHandler(socketAddress, config.getUser(), config.getPwd());
                    } else {
                        handler = new Socks5ProxyHandler(socketAddress);
                    }
                    break;
                default:
                    break;
            }
        }
        return handler;
    }
}
