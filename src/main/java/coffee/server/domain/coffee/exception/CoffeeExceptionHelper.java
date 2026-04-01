package coffee.server.domain.coffee.exception;

import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.UserFacingServiceException;
import org.springframework.http.HttpStatus;

public final class CoffeeExceptionHelper {
    private CoffeeExceptionHelper() {}

    public static UserFacingServiceException createCoffeeNotFound(Long coffeeId) {
        throw new UserFacingServiceException(
                ErrorCode.COFFEE_NOT_FOUND,
                HttpStatus.NOT_FOUND,
                coffeeId,
                "could not find coffee id of (%s)".formatted(coffeeId));
    }
}
