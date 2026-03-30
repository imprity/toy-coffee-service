package coffee.server.domain.idempotencycache.repository;

import coffee.server.domain.idempotencycache.entity.IdempotencyCache;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyCacheRepository extends JpaRepository<IdempotencyCache, Long> {
    public Optional<IdempotencyCache> findByIdempotencyCacheKey(UUID idempotencyCacheKey);
}
