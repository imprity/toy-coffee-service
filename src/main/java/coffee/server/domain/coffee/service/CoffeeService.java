package coffee.server.domain.coffee.service;

import coffee.server.domain.coffee.dto.GetCoffeeResponse;
import coffee.server.domain.coffee.entity.Coffee;
import coffee.server.domain.coffee.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeService {
    private final CoffeeRepository coffeeRepository;

    @Transactional(readOnly = true)
    public GetCoffeeResponse getCoffe(Long coffeeId) {
        Coffee coffee = coffeeRepository
                .findById(coffeeId)
                .orElseThrow(() -> new RuntimeException("coffee %s not found".formatted(coffeeId)));

        return GetCoffeeResponse.of(coffee);
    }
}
