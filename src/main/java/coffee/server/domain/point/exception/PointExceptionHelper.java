package coffee.server.domain.point.exception;

import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.ServiceException;

public final class PointExceptionHelper {
    private PointExceptionHelper() {}

    public static ServiceException createPointNotFound(Long pointId) {
        throw new ServiceException(ErrorCode.POINT_NOT_FOUND, "Could not find point id of (%s).".formatted(pointId));
    }
}
