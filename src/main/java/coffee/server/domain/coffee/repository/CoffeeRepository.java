package coffee.server.domain.coffee.repository;

import coffee.server.domain.coffee.entity.Coffee;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT c FROM Coffee c WHERE c.coffeeId = :coffeeId")
    Optional<Coffee> findByIdWithLock(@Param("coffeeId") Long coffeeId);
}
