package coffee.server.domain.coffeeorder.repository;

import coffee.server.domain.coffeeorder.entity.CoffeeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeOrderRepository extends JpaRepository<CoffeeOrder, Long> {}
