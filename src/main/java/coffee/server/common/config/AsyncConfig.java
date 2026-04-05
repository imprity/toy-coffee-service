package coffee.server.common.config;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean()
    public Executor coffeeOrderLoggingTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(5);

        taskExecutor.setQueueCapacity(500);

        // thread가 core pool size 보다 많아진 경우, 그리고 그 thread가 다시 쉬고 있을 경우
        // 몇초 동안이나 살려 줄지
        taskExecutor.setKeepAliveSeconds(60);
        // 그리고 corr pool thread도 60초 동안 하는 일이 없으면 그냥 죽이기
        taskExecutor.setAllowCoreThreadTimeOut(true);
        taskExecutor.setThreadNamePrefix("coffee-order-log-");
        // 아래에 만든 rejection policy 쓰기
        taskExecutor.setRejectedExecutionHandler(new LoggingDiscardPolicy());
        // 서버 프로그램 종료 전에 할일 을 끝마치게 하기
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // 그리고 종료전까지 할일을 끝마칠 시간을 30초 주기
        taskExecutor.setAwaitTerminationSeconds(30);
        taskExecutor.initialize();

        return taskExecutor;
    }

    /**
     * Thread pool도 꽉찼고 queue도 다 꽉 찼을 경우 실행 되는 policy입니다.
     *
     * AbortPolicy의 경우 caller한테 에러가 퍼지기 때문에 맞지 않고
     * CallerRunsPolicy는 caller한테 요청을 부담하기 때문에 맞지 않다고 생각하여 이 policy를... AI가 추천해 주었습니다 :D
     *
     */
    @Slf4j
    public static class LoggingDiscardPolicy implements RejectedExecutionHandler {
        // TODO : 몇번이나 실패했는지 metric 도 집어넣기
        // https://medium.com/@AlexanderObregon/tracking-metrics-in-spring-boot-with-micrometer-and-prometheus-d61b97520477
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            log.warn(
                    "executor coffee-order-log saturated, dropping task. pool={}/{}, queue={}",
                    e.getPoolSize(),
                    e.getMaximumPoolSize(),
                    e.getQueue().size());
        }
    }
}
