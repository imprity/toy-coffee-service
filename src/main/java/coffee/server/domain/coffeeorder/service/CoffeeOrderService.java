package coffee.server.domain.coffeeorder.service;

import coffee.server.common.config.AppConfig;
import coffee.server.domain.coffee.dto.CoffeeDto;
import coffee.server.domain.coffeeorder.dto.CoffeeOrderDto;
import coffee.server.domain.coffeeorder.entity.CoffeeOrder;
import coffee.server.domain.coffeeorder.repository.CoffeeOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoffeeOrderService {
    private final CoffeeOrderRepository coffeeOrderRepository;
    private final RestClient restClient;
    private final AppConfig appConfig;

    @Transactional
    public CoffeeOrderDto createCoffeeOrder(CoffeeDto coffeeDto, Long coffeeOrderAmount, String customerId) {
        CoffeeOrder coffeeOrder = CoffeeOrder.create(coffeeDto, coffeeOrderAmount, customerId);

        coffeeOrder = coffeeOrderRepository.saveAndFlush(coffeeOrder);

        return CoffeeOrderDto.of(coffeeOrder);
    }

    @Async("coffeeOrderLoggingTaskExecutor")
    public void sendCoffeeOrderData(CoffeeOrderDto coffeeOrderDto) {
        String url = appConfig.getCoffeeOrderLogUrl();

        // url가 setting 이 안되어 있다면 logging을 원하지 않는 다고 이해하고 무시
        if (url == null || url.isBlank()) {
            return;
        }

        try {
            restClient
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(coffeeOrderDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("logging coffee order failed: {}", e.getMessage());
        }
    }
}
