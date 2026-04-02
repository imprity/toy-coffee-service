package coffee.server.domain.point.facade;

import static org.assertj.core.api.Assertions.assertThat;

import coffee.server.domain.coffee.entity.Coffee;
import coffee.server.domain.coffeeorder.entity.CoffeeOrder;
import coffee.server.domain.idempotencycache.entity.IdempotencyCache;
import coffee.server.domain.point.dto.AddPointRequest;
import coffee.server.domain.point.dto.SetPointRequest;
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
@ActiveProfiles("point-facade-test")
class PointFacadeTest {
    @Autowired
    private PointService pointService;

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @BeforeEach
    void clearDatabase() {
        databaseCleaner.deleteTables(Coffee.class, CoffeeOrder.class, IdempotencyCache.class, PointAudit.class);
    }

    @Test
    @DisplayName("포인트_값_set_멱등성_보장")
    void pointSetIdempotencyTest() throws Throwable {
        // GIVEN
        pointService.setPoint(BigDecimal.valueOf(0));

        SetPointRequest req = new SetPointRequest(BigDecimal.valueOf(1000), UUID.randomUUID());

        // WHEN
        IdempotencyTestHelper.doIdempotencyTest(() -> {
            pointFacade.setPoint(req);
        });

        // THEN
        assertThat(pointService.getPoint().pointAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("포인트_값_add_멱등성_보장")
    void pointAddIdempotencyTest() throws Throwable {
        // GIVEN
        pointService.setPoint(BigDecimal.valueOf(0));

        AddPointRequest req = new AddPointRequest(BigDecimal.valueOf(1000), UUID.randomUUID());

        // WHEN
        IdempotencyTestHelper.doIdempotencyTest(() -> {
            pointFacade.addPoint(req);
        });

        // THEN
        assertThat(pointService.getPoint().pointAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }
}
