package org.down.core.dispatch;

import org.down.core.boot.HttpDownBootstrap;
import org.down.core.entity.ChunkInfo;

/**
 * <p>{@link HttpDownCallback}</p>
 * 下载回调
 * @author 白菜
 * @since Created in 2019/12/3 16:07
 */
public class HttpDownCallback {

    public void onStart(HttpDownBootstrap httpDownBootstrap) {
    }

    public void onProgress(HttpDownBootstrap httpDownBootstrap) {
    }

    public void onPause(HttpDownBootstrap httpDownBootstrap) {
    }

    public void onResume(HttpDownBootstrap httpDownBootstrap) {
    }

    public void onChunkError(HttpDownBootstrap httpDownBootstrap, ChunkInfo chunkInfo) {
    }

    public void onError(HttpDownBootstrap httpDownBootstrap) {
    }

    public void onChunkDone(HttpDownBootstrap httpDownBootstrap, ChunkInfo chunkInfo) {
    }

    public void onDone(HttpDownBootstrap httpDownBootstrap) {
    }
}
