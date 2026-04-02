package coffee.server.domain.coffee.batchjobs;

import coffee.server.domain.coffee.entity.Coffee;
import coffee.server.domain.coffee.repository.CoffeeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResetCoffeeOrderCountBatchJob {
    private final CoffeeRepository coffeeRepository;

    @Scheduled(cron = "${app.batchjobs.reset-coffee-order-count.cron}")
    @Transactional
    public void calculateMembershipAndReadyPoints() {
        log.info("Resetting coffee order counts of coffees.");

        List<Coffee> coffees = coffeeRepository.findAll();

        for (Coffee coffee : coffees) {
            coffee.updateCoffeeOrderCount(0L);
        }

        coffeeRepository.saveAll(coffees);
    }
}
