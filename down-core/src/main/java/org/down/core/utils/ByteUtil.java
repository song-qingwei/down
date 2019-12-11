package org.down.core.utils;

/**
 * <p>{@link ByteUtil}</p>
 *
 * @author 白菜
 * @since Created in 2019/12/3 16:16
 */
public class ByteUtil {

    public static String byteFormat(long size) {
        String[] unit = {"B", "KB", "MB", "GB", "TB", "PB"};
        int pow = 0;
        long temp = size;
        double unitSize = 1000D;
        while (temp / unitSize > 1) {
            pow++;
            temp /= unitSize;
        }
        String format = unit[pow];
        return String.format("%.2f", size / Math.pow(1024, pow)) + format;
    }
}
