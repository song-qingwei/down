package org.down.core.boot;

import org.down.core.dispatch.HttpDownCallback;
import org.down.core.entity.HttpDownConfigInfo;
import org.down.core.entity.HttpRequestInfo;
import org.down.core.entity.HttpResponseInfo;
import org.down.core.entity.TaskInfo;
import org.down.core.exception.BootstrapBuildException;
import org.down.core.proxy.ProxyConfig;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;
import java.util.Objects;

/**
 * <p>{@link HttpDownBootstrapBuilder}</p>
 * 创建一个HTTP下载器
 * @author 白菜
 * @since Created in 2019/12/4 11:49
 */
@Data
@Accessors(chain = true)
public class HttpDownBootstrapBuilder {

    /** 任务http请求信息 默认值：null */
    private HttpRequestInfo request;
    /** 任务http响应信息 默认值：null */
    private HttpResponseInfo response;
    /** 任务配置信息 默认:new HttpDownConfigInfo() */
    private HttpDownConfigInfo downConfig;
    /** 任务二级代理信息 默认:null */
    private ProxyConfig proxyConfig;
    /** 任务下载记录 默认值：null */
    private TaskInfo taskInfo;
    /** 线程池设置 默认值：new NioEventLoopGroup(1) */
    private NioEventLoopGroup loopGroup;
    /** 下载回调类 */
    private HttpDownCallback callback;

    public HttpDownBootstrap build() {
        if (Objects.isNull(request)) {
            throw new BootstrapBuildException("request Can not be empty.");
        }
        if (Objects.isNull(response)) {
            throw new BootstrapBuildException("response Can not be empty.");
        }
        if (Objects.isNull(loopGroup)) {
            loopGroup = new NioEventLoopGroup(1);
        }
        if (Objects.isNull(downConfig)) {
            downConfig = new HttpDownConfigInfo();
        }
        if (StringUtils.isBlank(downConfig.getFilePath())) {
            downConfig.setFilePath(System.getProperty("user.dir"));
        } else {
            downConfig.setFilePath(Paths.get(downConfig.getFilePath()).toFile().getPath());
        }
        if (!response.isSupportRange()) {
            downConfig.setConnections(1);
        }
        HttpDownBootstrap downBootstrap = new HttpDownBootstrap();
        downBootstrap.setRequest(request)
                .setResponse(response)
                .setDownConfig(downConfig)
                .setProxyConfig(proxyConfig)
                .setTaskInfo(taskInfo)
                .setCallback(callback)
                .setLoopGroup(loopGroup);
        return downBootstrap;
    }
}
