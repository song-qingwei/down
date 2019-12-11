package org.down.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>{@link ChunkInfo}</p>
 * 块文件信息
 * @author 白菜
 * @since Created in 2019/12/3 16:03
 */
@Data
@Accessors(chain = true)
public class ChunkInfo implements Serializable {

    private static final long serialVersionUID = 1787808094895965418L;
    private int index;
    /** 当前快下载大小 */
    private long downSize;
    /** 当前块文件总大小 */
    private long totalSize;
    /** 最后一次已存在的大小 */
    private long lastCountSize;
    /** 最后一次暂停时间 */
    private long lastPauseTime;
    /** 总共暂停的时间 */
    private long pauseTime;
    /** 下载状态 */
    private int status;
    /** 下载速度 */
    private long speed;
}
