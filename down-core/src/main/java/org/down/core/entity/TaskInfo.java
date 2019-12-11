package org.down.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>{@link TaskInfo}</p>
 * 下载任务信息
 * @author 白菜
 * @since Created in 2019/12/3 16:02
 */
@Data
@Accessors(chain = true)
public class TaskInfo implements Serializable {

    private static final long serialVersionUID = -2718958649569697242L;
    /** 下载完成大小 */
    private long downSize;
    /** 开始时间 */
    private long startTime;
    /** 最后一次的开始时间 */
    private transient long lastStartTime;
    /** 最后一次下载的大小 */
    private transient long lastDownSize;
    /** 最后一次暂停时间 */
    private long lastPauseTime;
    /** 下载状态 */
    private int status;
    /** 瞬时速度 */
    private long speed;
    /** 分块信息 */
    private List<ChunkInfo> chunkInfoList;
    /** 每块下载的大小及channel */
    private List<ConnectInfo> connectInfoList;
}
