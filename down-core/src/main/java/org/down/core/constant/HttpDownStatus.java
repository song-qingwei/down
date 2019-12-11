package org.down.core.constant;

/**
 * <p>{@link HttpDownStatus}</p>
 *
 * @author 白菜
 * @since Created in 2019/12/3 16:27
 */
public final class HttpDownStatus {

    /**
     * 等待下载
     */
    public final static int WAIT = 0;
    /**
     * 下载中
     */
    public final static int RUNNING = 1;
    /**
     * 暂停下载
     */
    public final static int PAUSE = 2;
    /**
     * 下载失败
     */
    public final static int ERROR = 3;
    /**
     * 下载完成
     */
    public final static int DONE = 4;
}
