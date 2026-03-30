package coffee.server.domain.idempotencycache.entity;

import coffee.server.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Entity
@Table(name = "idempotency_caches")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdempotencyCache extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idempotencyCacheId;

    private UUID idempotencyCacheKey;

    private String idempotencyCacheValue;

    public static IdempotencyCache create(@NonNull UUID idempotencyCacheKey, @NonNull String idempotencyCacheValue) {
        IdempotencyCache cache = new IdempotencyCache();

        cache.idempotencyCacheKey = idempotencyCacheKey;
        cache.idempotencyCacheValue = idempotencyCacheValue;

        return cache;
    }
}
