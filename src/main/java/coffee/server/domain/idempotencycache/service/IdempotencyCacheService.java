package coffee.server.domain.idempotencycache.service;

import coffee.server.domain.idempotencycache.entity.IdempotencyCache;
import coffee.server.domain.idempotencycache.exception.DuplicateCacheKeyException;
import coffee.server.domain.idempotencycache.repository.IdempotencyCacheRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class IdempotencyCacheService {
    private final ObjectMapper objectMapper;
    private final IdempotencyCacheRepository idempotencyCacheRepository;

    @Transactional
    public void putCache(@NonNull UUID idempotencyCacheKey, @NonNull Object idempotencyCacheValue) {

        String cacheString = objectMapper.writeValueAsString(idempotencyCacheValue);

        if (!idempotencyCacheRepository.putCache(idempotencyCacheKey, cacheString, Instant.now())) {
            throw new DuplicateCacheKeyException(idempotencyCacheKey);
        }
    }

    @Nullable
    @Transactional(readOnly = true)
    public <T> T getCache(@NonNull UUID idempotencyCacheKey, TypeReference<T> valueType) {
        Optional<IdempotencyCache> cacheOpt = idempotencyCacheRepository.findByIdempotencyCacheKey(idempotencyCacheKey);

        if (cacheOpt.isEmpty()) {
            return null;
        }

        IdempotencyCache cache = cacheOpt.get();

        String cacheString = cache.getIdempotencyCacheValue();

        return objectMapper.readValue(cacheString, valueType);
    }
}
