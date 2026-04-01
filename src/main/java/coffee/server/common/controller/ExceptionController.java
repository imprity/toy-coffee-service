package coffee.server.common.controller;

import coffee.server.common.dto.BaseResponse;
import coffee.server.common.dto.ValidationErrorResponse;
import coffee.server.common.exception.ServiceException;
import coffee.server.common.exception.UserFacingServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
    /**
     * UserFacingServiceException 핸들링
     */
    @ExceptionHandler(UserFacingServiceException.class)
    public ResponseEntity<BaseResponse<?>> handleUserFacingServiceException(UserFacingServiceException e) {
        BaseResponse res = BaseResponse.fail(e.getErrorCode().name(), e.getMessage(), e.getData());

        return ResponseEntity.status(e.getHttpStatus().value()).body(res);
    }

    /**
     * ServiceException 핸들링
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<BaseResponse<?>> handleServiceException(ServiceException e) {
        log.error("Uncaught ServiceException", e);
        BaseResponse res =
                BaseResponse.fail(e.getErrorCode().name(), e.getErrorCode().name());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    /**
     * request validation 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<ValidationErrorResponse>> handleValidationExceptions(
            MethodArgumentNotValidException e) {
        ValidationErrorResponse data = new ValidationErrorResponse();

        for (var fieldError : e.getBindingResult().getFieldErrors()) {
            data.putFieldError(fieldError);
        }

        for (var globalError : e.getBindingResult().getGlobalErrors()) {
            data.putGlobalError(globalError);
        }

        BaseResponse<ValidationErrorResponse> res =
                BaseResponse.fail(HttpStatus.BAD_REQUEST.name(), HttpStatus.BAD_REQUEST.getReasonPhrase(), data);

        return ResponseEntity.badRequest().body(res);
    }
}
