package coffee.server.domain.idempotencycache.exception;

import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.ServiceException;
import java.util.UUID;

public class DuplicateCacheKeyException extends ServiceException {
    public DuplicateCacheKeyException(UUID idempotencyCacheKey) {
        super(
                ErrorCode.IDEMPOTENCY_CACHE_DUPLICATE_CACHE_KEY,
                "There already is a row with idempotency_cache_key (%s)".formatted(idempotencyCacheKey));
    }
}
