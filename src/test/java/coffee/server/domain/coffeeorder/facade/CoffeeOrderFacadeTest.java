package coffee.server.domain.coffeeorder.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import coffee.server.domain.coffee.entity.Coffee;
import coffee.server.domain.coffee.enums.CoffeeStatus;
import coffee.server.domain.coffee.repository.CoffeeRepository;
import coffee.server.domain.coffeeorder.dto.CoffeeOrderRequest;
import coffee.server.domain.coffeeorder.repository.CoffeeOrderRepository;
import coffee.server.domain.idempotencycache.repository.IdempotencyCacheRepository;
import coffee.server.domain.point.service.PointService;
import coffee.server.domain.pointaudit.repository.PointAuditRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
    private CoffeeOrderRepository coffeeOrderRepository;

    @Autowired
    private IdempotencyCacheRepository idempotencyCacheRepository;

    @Autowired
    private PointAuditRepository pointAuditRepository;

    @Autowired
    private PointService pointService;

    @Autowired
    private CoffeeOrderFacade coffeeOrderFacade;

    @BeforeEach
    void clearDatabase() {
        coffeeRepository.deleteAll();
        coffeeOrderRepository.deleteAll();
        idempotencyCacheRepository.deleteAll();
        pointAuditRepository.deleteAll();
    }

    @Test
    @DisplayName("커피_주문_멱등성_보장")
    void idempotencyTest() throws Throwable {
        // ==================
        // GIVEN
        // ==================
        pointService.setPoint(BigDecimal.valueOf(100000));
        Coffee coffee =
                coffeeRepository.save(Coffee.create("coffee", BigDecimal.valueOf(1000), 10L, CoffeeStatus.SELLING));

        final int requestCount = 1000;
        CoffeeOrderRequest orderRequest = new CoffeeOrderRequest(UUID.randomUUID(), coffee.getCoffeeId(), 1L, "momo");

        // ==================
        // WHEN
        // ==================
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(requestCount);

        List<Future<?>> futures = new ArrayList<>();

        AtomicInteger successCounter = new AtomicInteger(0);

        for (int i = 0; i < requestCount; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    startLatch.await(10L, TimeUnit.SECONDS);
                    coffeeOrderFacade.orderCoffee(orderRequest);

                    successCounter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }));
        }

        startLatch.countDown();

        doneLatch.await(10L, TimeUnit.SECONDS);

        // ==================
        // LOGGING
        // ==================

        // 성공 횟수를 출력
        System.out.println("===========================");
        System.out.println("success: %s/%s".formatted(successCounter.get(), requestCount));
        System.out.println("===========================");

        int exceptionCounter = 0;

        // 실패한 요청들의 에러 들을 출력
        for (int i = 0; i < futures.size(); i++) {
            Future future = futures.get(i);
            try {
                future.get();
            } catch (Exception e) {
                if (e instanceof ExecutionException) {
                    System.out.println("======== Exception %s ========".formatted(exceptionCounter));
                    System.out.println(e.getCause().getMessage());
                    System.out.println("==============================");
                    exceptionCounter++;
                } else {
                    throw e;
                }
            }
        }

        exceptionCounter = 0;

        // 실패한 요청들의 stackTrace를 출력
        for (int i = 0; i < futures.size(); i++) {
            Future future = futures.get(i);
            try {
                future.get();
            } catch (Exception e) {
                if (e instanceof ExecutionException) {
                    System.out.println("======== Exception %s ========".formatted(exceptionCounter));
                    e.printStackTrace(System.out);
                    System.out.println("==============================");
                    exceptionCounter++;
                } else {
                    throw e;
                }
            }
        }

        // ==================
        // THEN
        // ==================

        // 오류가 없고
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                if (e instanceof ExecutionException) {
                    throw e.getCause();
                }
                throw e;
            }
        }

        // 값이 맞는지
        assertThat(successCounter.get()).isEqualTo(1000);
        assertThat(pointService.getPoint().pointAmount()).isEqualByComparingTo(BigDecimal.valueOf(99000));
        assertThat(coffeeRepository.findById(coffee.getCoffeeId()).get().getCoffeeStock())
                .isEqualTo(9L);
    }
}
