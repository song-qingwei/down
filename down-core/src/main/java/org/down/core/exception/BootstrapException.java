package org.down.core.exception;

/**
 * <p>{@link BootstrapException}</p>
 *
 * @author 白菜
 * @since Created in 2019/12/3 18:31
 */
public class BootstrapException extends RuntimeException {

    public BootstrapException(String message) {
        super(message);
    }

    public BootstrapException(String message, Throwable cause) {
        super(message, cause);
    }
}
