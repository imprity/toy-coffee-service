package coffee.server.common.initializer;

import coffee.server.domain.coffee.entity.Coffee;
import coffee.server.domain.coffee.enums.CoffeeStatus;
import coffee.server.domain.coffee.repository.CoffeeRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CoffeeInitializer implements ApplicationRunner {
    private final CoffeeRepository coffeeRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        List<Coffee> coffees = coffeeRepository.findAll();

        if (coffees.isEmpty()) {
            coffeeRepository.save(Coffee.create("카라멜 마끼아또", BigDecimal.valueOf(5000), 10L, CoffeeStatus.SELLING));
            coffeeRepository.save(Coffee.create("핫초코", BigDecimal.valueOf(3000), 15L, CoffeeStatus.DISCONTINUED));
            coffeeRepository.save(Coffee.create("에스프레소", BigDecimal.valueOf(3500), 0L, CoffeeStatus.DISCONTINUED));
            coffeeRepository.save(Coffee.create("모카", BigDecimal.valueOf(4000), 5L, CoffeeStatus.SELLING));
        }
    }
}
