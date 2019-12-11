package org.down.core.proxy;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>{@link ProxyConfig}</p>
 * 代理配置
 * @author 白菜
 * @since Created in 2019/12/3 16:01
 */
@Data
@Accessors(chain = true)
public class ProxyConfig implements Serializable {

    private static final long serialVersionUID = 5758936753399883211L;
    /** 代理类型 */
    private ProxyType proxyType;
    /** 代理host */
    private String host;
    /** 代理端口 */
    private int port;
    /** 用户名 */
    private String user;
    /** 密码 */
    private String pwd;

    public ProxyConfig(ProxyType proxyType, String host, int port) {
        this.proxyType = proxyType;
        this.host = host;
        this.port = port;
    }
}
