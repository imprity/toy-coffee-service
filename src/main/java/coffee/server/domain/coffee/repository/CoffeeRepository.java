package coffee.server.domain.coffee.repository;

import coffee.server.domain.coffee.entity.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeRepository extends JpaRepository<Coffee, Long> {}
