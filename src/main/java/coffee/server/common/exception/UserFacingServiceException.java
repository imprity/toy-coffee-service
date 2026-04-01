package coffee.server.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserFacingServiceException extends ServiceException {
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;
    private final Object data;

    public UserFacingServiceException(ErrorCode errorCode, HttpStatus httpStatus, Object data, String message) {
        super(errorCode, message);

        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.data = data;
    }

    public UserFacingServiceException(
            ErrorCode errorCode, HttpStatus httpStatus, Object data, String message, Throwable cause) {
        super(errorCode, message, cause);

        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.data = data;
    }
}
