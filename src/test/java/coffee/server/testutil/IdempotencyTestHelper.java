package coffee.server.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class IdempotencyTestHelper {
    public static void doIdempotencyTest(Runnable task) throws Throwable {
        final int requestCount = 1000;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(requestCount);

        List<Future<?>> futures = new ArrayList<>();

        AtomicInteger successCounter = new AtomicInteger(0);

        for (int i = 0; i < requestCount; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    startLatch.await(10L, TimeUnit.SECONDS);
                    task.run();

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

        // 오류가 있다면 던지기
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

        assertThat(successCounter.get()).isEqualTo(1000);
    }
}
