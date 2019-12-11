package org.down.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * <p>{@link HttpDownConfigInfo}</p>
 * 下载配置
 * @author 白菜
 * @since Created in 2019/12/3 15:59
 */
@Data
@Accessors(chain = true)
public class HttpDownConfigInfo implements Serializable {

    private static final long serialVersionUID = 5796266002215159725L;
    /** 文件保存路径 */
    private String filePath;
    /** 分块数 */
    private int connections;
    /** 超时时间 */
    private int timeout;
    /** 允许重试次数 */
    private int retryCount;
    /** 是否自动重命名 */
    private boolean autoRename;
    /** 下载限制网速 */
    private long speedLimit;

    public String getFilePath() {
        if (StringUtils.isBlank(filePath)) {
            filePath = System.getProperty("user.dir");
        }
        return filePath;
    }

    public int getTimeout() {
        if (timeout <= 0) {
            timeout = 30;
        }
        return timeout;
    }

    public int getRetryCount() {
        if (retryCount <= 0) {
            retryCount = 5;
        }
        return retryCount;
    }

    public int getConnections() {
        if (connections <= 0) {
            connections = 128;
        }
        return connections;
    }
}
