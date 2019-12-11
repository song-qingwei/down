package org.down.core.dispatch;

import org.down.core.boot.HttpDownBootstrap;
import org.down.core.utils.ByteUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>{@link ConsoleHttpDownCallback}</p>
 * 控制台打印
 * @author 白菜
 * @since Created in 2019/12/3 16:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ConsoleHttpDownCallback extends HttpDownCallback {

    private int progressWidth = 20;

    public ConsoleHttpDownCallback() {
    }

    @Override
    public void onStart(HttpDownBootstrap httpDownBootstrap) {
        System.out.println(httpDownBootstrap.getRequest().toString());
        System.out.println();
    }

    @Override
    public void onProgress(HttpDownBootstrap httpDownBootstrap) {
        double rate = 0;
        if (httpDownBootstrap.getResponse().getTotalSize() > 0) {
            rate = httpDownBootstrap.getTaskInfo().getDownSize() / (double) httpDownBootstrap.getResponse().getTotalSize();
        }
        printProgress(rate, httpDownBootstrap.getTaskInfo().getSpeed());
    }

    @Override
    public void onError(HttpDownBootstrap httpDownBootstrap) {
        System.out.println("\n\nDownload error");
    }

    @Override
    public void onDone(HttpDownBootstrap httpDownBootstrap) {
        System.out.println("\n\nDownload completed");
    }

    private void printProgress(double rate, long speed) {
        int downWidth = (int) (progressWidth * rate);
        StringBuilder builder = new StringBuilder();
        builder.append("\r[");
        for (int i = 0; i < progressWidth; i++) {
            if (i < downWidth) {
                builder.append("■");
            } else {
                builder.append("□");
            }
        }
        builder.append("]")
                .append(String.format("%.2f", rate * 100))
                .append("%    ")
                .append(String.format("%8s", ByteUtil.byteFormat(speed)))
                .append("/S");
        System.out.print(builder.toString());
    }
}
