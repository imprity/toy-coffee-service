package coffee.server.domain.point.exception;

import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.ServiceException;
import coffee.server.common.exception.UserFacingServiceException;
import org.springframework.http.HttpStatus;

public final class PointExceptionHelper {
    private PointExceptionHelper() {}

    public static ServiceException createPointNotFound(Long pointId) {
        throw new ServiceException(ErrorCode.POINT_NOT_FOUND, "Could not find point id of (%s).".formatted(pointId));
    }

    public static ServiceException createPointNotFoundByCustomerId(String customerId) {
        throw new UserFacingServiceException(
                ErrorCode.POINT_NOT_FOUND,
                HttpStatus.NOT_FOUND,
                null,
                "Could not find point of customerId (%s).".formatted(customerId));
    }
}
