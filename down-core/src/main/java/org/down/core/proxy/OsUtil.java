package org.down.core.proxy;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * <p>{@link OsUtil}</p>
 *
 * @author 白菜
 * @since Created in 2019/12/3 18:23
 */
public class OsUtil {

    /**
     * 查看指定的端口号是否空闲，若空闲则返回否则返回一个随机的空闲端口号
     */
    public static int getFreePort(int defaultPort) throws IOException {
        try (
                ServerSocket serverSocket = new ServerSocket(defaultPort)
        ) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            return getFreePort();
        }
    }

    /**
     * 获取空闲端口号
     */
    public static int getFreePort() throws IOException {
        try (
                ServerSocket serverSocket = new ServerSocket(0)
        ) {
            return serverSocket.getLocalPort();
        }
    }

    /**
     * 检查端口号是否被占用
     */
    public static boolean isBusyPort(int port) {
        boolean ret = true;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            ret = false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isWindowsXp() {
        return OS.contains("win") && OS.contains("xp");
    }

    public static boolean isMac() {
        return OS.contains("mac");
    }

    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    public static boolean isSolaris() {
        return (OS.contains("sunos"));
    }

    private static final String ARCH = System.getProperty("sun.arch.data.model");

    public static boolean is64() {
        return "64".equals(ARCH);
    }

    public static boolean is32() {
        return "32".equals(ARCH);
    }
}
