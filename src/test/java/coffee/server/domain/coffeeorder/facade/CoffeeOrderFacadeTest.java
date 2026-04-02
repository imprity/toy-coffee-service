package coffee.server.domain.coffeeorder.facade;

import static org.assertj.core.api.Assertions.assertThat;

import coffee.server.domain.coffee.entity.Coffee;
import coffee.server.domain.coffee.enums.CoffeeStatus;
import coffee.server.domain.coffee.repository.CoffeeRepository;
import coffee.server.domain.coffeeorder.dto.CoffeeOrderRequest;
import coffee.server.domain.coffeeorder.entity.CoffeeOrder;
import coffee.server.domain.idempotencycache.entity.IdempotencyCache;
import coffee.server.domain.point.service.PointService;
import coffee.server.domain.pointaudit.entity.PointAudit;
import coffee.server.testutil.DatabaseCleaner;
import coffee.server.testutil.IdempotencyTestHelper;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("coffee-order-facade-test")
class CoffeeOrderFacadeTest {
    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private PointService pointService;

    @Autowired
    private CoffeeOrderFacade coffeeOrderFacade;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @BeforeEach
    void clearDatabase() {
        databaseCleaner.deleteTables(Coffee.class, CoffeeOrder.class, IdempotencyCache.class, PointAudit.class);
    }

    @Test
    @DisplayName("커피_주문_멱등성_보장")
    void idempotencyTest() throws Throwable {
        // GIVEN
        pointService.setPoint(BigDecimal.valueOf(100000));
        Coffee coffee =
                coffeeRepository.save(Coffee.create("coffee", BigDecimal.valueOf(1000), 10L, CoffeeStatus.SELLING));

        CoffeeOrderRequest orderRequest = new CoffeeOrderRequest(UUID.randomUUID(), coffee.getCoffeeId(), 1L, "momo");

        // WHEN
        IdempotencyTestHelper.doIdempotencyTest(() -> {
            coffeeOrderFacade.orderCoffee(orderRequest);
        });

        // THEN

        // 값이 맞는지
        assertThat(pointService.getPoint().pointAmount()).isEqualByComparingTo(BigDecimal.valueOf(99000));
        assertThat(coffeeRepository.findById(coffee.getCoffeeId()).get().getCoffeeStock())
                .isEqualTo(9L);
        assertThat(coffeeRepository.findById(coffee.getCoffeeId()).get().getCoffeeOrderCount())
                .isEqualTo(1L);
    }
}
