package org.down.core.exception;

/**
 * <p>{@link BootstrapBuildException}</p>
 *
 * @author 白菜
 * @since Created in 2019/12/4 13:21
 */
public class BootstrapBuildException extends RuntimeException {

    public BootstrapBuildException(String message) {
        super(message);
    }

    public BootstrapBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}
