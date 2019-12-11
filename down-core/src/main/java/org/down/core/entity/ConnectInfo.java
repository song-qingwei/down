package org.down.core.entity;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.nio.channels.SeekableByteChannel;

/**
 * <p>{@link ConnectInfo}</p>
 * 每块下载大小信息
 * @author 白菜
 * @since Created in 2019/12/3 16:04
 */
@Data
@Accessors(chain = true)
public class ConnectInfo implements Serializable {

    private static final long serialVersionUID = 7907338947994221495L;
    /** 下载开始位置 */
    private long startPosition;
    /** 下载结束位置 */
    private long endPosition;
    /** 当前块文件已下载完成大小 */
    private long downSize;
    /** 下载失败次数 */
    private int errorCount;
    /** 当前块数 */
    private int chunkIndex;
    /** 下载状态 */
    private int status;

    private transient Channel connectChannel;
    private transient SeekableByteChannel fileChannel;

    public long getTotalSize() {
        return endPosition - startPosition + 1;
    }
}
