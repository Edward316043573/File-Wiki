package top.cxscoder.common.exception;

/**
 * @author Edward
 * @date 2023-11-30 15:32
 * @copyright Copyright (c) 2023 Edward
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {}

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }
}
