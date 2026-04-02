package coffee.server.domain.idempotencycache.repository;

import coffee.server.domain.idempotencycache.entity.IdempotencyCache;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

public interface IdempotencyCacheRepository extends JpaRepository<IdempotencyCache, Long> {
    Optional<IdempotencyCache> findByIdempotencyCacheKey(UUID idempotencyCacheKey);

    @Modifying
    @NativeQuery(
            """
        INSERT INTO idempotency_caches (
            idempotency_cache_key,
            idempotency_cache_value,
            created_at,
            modified_at
        ) VALUES (
            :idempotencyCacheKey,
            :idempotencyCacheValue,
            :createdAt,
            :createdAt
        ) ON DUPLICATE KEY UPDATE modified_at=:createdAt;
    """)
    Long putCacheImpl(
            @Param("idempotencyCacheKey") UUID idempotencyCacheKey,
            @Param("idempotencyCacheValue") String idempotencyCacheValue,
            @Param("createdAt") Instant createdAt);

    default boolean putCache(UUID idempotencyCacheKey, String idempotencyCacheValue, Instant createdAt) {
        Long res = putCacheImpl(idempotencyCacheKey, idempotencyCacheValue, createdAt);

        if (res == 1) {
            return true;
        } else {
            return false;
        }
    }
}
