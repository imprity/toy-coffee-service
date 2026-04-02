package coffee.server.domain.idempotencycache.service;

import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.ServiceException;
import coffee.server.domain.idempotencycache.exception.DuplicateCacheKeyException;
import java.util.UUID;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

@Service
@RequiredArgsConstructor
public class IdempotentExecutorService {
    private final IdempotencyCacheService idempotencyCacheService;

    public <T> T executeWithIdempotency(Callable<T> task, TypeReference<T> valueType, UUID idempotencyKey) {
        T cachedRes = idempotencyCacheService.getCache(idempotencyKey, valueType);

        if (cachedRes != null) {
            return cachedRes;
        }

        try {
            return task.call();
        } catch (DuplicateCacheKeyException e) {
            cachedRes = idempotencyCacheService.getCache(idempotencyKey, valueType);
            if (cachedRes != null) {
                return cachedRes;
            } else {
                throw new ServiceException(
                        ErrorCode.ERROR,
                        "Could not find idempotencyCacheKey (%s) even though it was supposed to be in idempotency_caches table."
                                .formatted(idempotencyKey));
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(
                    ErrorCode.ERROR,
                    "Uncaught exception during executing task with %s cache key".formatted(idempotencyKey),
                    e);
        }
    }
}
