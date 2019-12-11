package org.down.core.utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * <p>{@link ProtoUtil}</p>
 * 请求地址
 * @author 白菜
 * @since Created in 2019/12/3 16:53
 */
public class ProtoUtil {

    @Data
    @Accessors(chain = true)
    public static class RequestProto implements Serializable {

        private static final long serialVersionUID = 5701753879525046360L;
        private String host;
        private int port;
        private boolean ssl;
    }

    public static RequestProto getRequestProto(HttpRequest httpRequest) {
        RequestProto requestProto = new RequestProto();
        int port = -1;
        String host = httpRequest.headers().get(HttpHeaderNames.HOST);
        if (StringUtils.isBlank(host)) {
            Pattern pattern = compile("^(?:https?://)?(?<host>[^/]*)/?.*$");
            Matcher matcher = pattern.matcher(httpRequest.uri());
            if (matcher.find()) {
                host = matcher.group("host");
            } else {
                return null;
            }
        }
        String uri = httpRequest.uri();
        Pattern pattern = compile("^(?:https?://)?(?<host>[^:]*)(?::(?<port>\\d+))?(/.*)?$");
        Matcher matcher = pattern.matcher(host);
        // 先从host上取端口号没取到再从uri上取端口号
        String portTemp = null;
        if (matcher.find()) {
            requestProto.setHost(matcher.group("host"));
            portTemp = matcher.group("port");
            if (StringUtils.isBlank(portTemp)) {
                matcher = pattern.matcher(uri);
                if (matcher.find()) {
                    portTemp = matcher.group("port");
                }
            }
        }
        if (StringUtils.isNotBlank(portTemp)) {
            port = Integer.parseInt(portTemp);
        }
        boolean isSsl = uri.indexOf("https") == 0 || host.indexOf("https") == 0;
        if (port == -1) {
            if (isSsl) {
                port = 443;
            } else {
                port = 80;
            }
        }
        requestProto.setPort(port);
        requestProto.setSsl(isSsl);
        return requestProto;
    }
}
