package coffee.server.domain.coffeeorder.service;

import coffee.server.domain.coffee.dto.CoffeeDto;
import coffee.server.domain.coffeeorder.dto.CoffeeOrderDto;
import coffee.server.domain.coffeeorder.entity.CoffeeOrder;
import coffee.server.domain.coffeeorder.repository.CoffeeOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeOrderService {
    private final CoffeeOrderRepository coffeeOrderRepository;

    @Transactional
    public CoffeeOrderDto createCoffeeOrder(CoffeeDto coffeeDto, Long coffeeOrderAmount, String customerId) {
        CoffeeOrder coffeeOrder = CoffeeOrder.create(coffeeDto, coffeeOrderAmount, customerId);

        coffeeOrder = coffeeOrderRepository.saveAndFlush(coffeeOrder);

        return CoffeeOrderDto.of(coffeeOrder);
    }
}
