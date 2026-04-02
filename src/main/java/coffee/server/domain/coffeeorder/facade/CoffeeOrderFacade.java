package coffee.server.domain.coffeeorder.facade;

import coffee.server.domain.coffee.dto.CoffeeDto;
import coffee.server.domain.coffee.service.CoffeeService;
import coffee.server.domain.coffeeorder.dto.CoffeeOrderDto;
import coffee.server.domain.coffeeorder.dto.CoffeeOrderRequest;
import coffee.server.domain.coffeeorder.service.CoffeeOrderService;
import coffee.server.domain.idempotencycache.service.IdempotencyCacheService;
import coffee.server.domain.idempotencycache.service.IdempotentExecutorService;
import coffee.server.domain.point.dto.PointDto;
import coffee.server.domain.point.service.PointService;
import coffee.server.domain.pointaudit.enums.PointAuditType;
import coffee.server.domain.pointaudit.service.PointAuditService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.core.type.TypeReference;

@Component
@RequiredArgsConstructor
public class CoffeeOrderFacade {
    private final CoffeeService coffeeService;
    private final CoffeeOrderService coffeeOrderService;
    private final PointService pointService;
    private final IdempotencyCacheService idempotencyCacheService;
    private final IdempotentExecutorService idempotentExecutorService;
    private final PointAuditService pointAuditService;

    private final TransactionTemplate tx;

    public CoffeeOrderDto orderCoffee(CoffeeOrderRequest req) {
        return idempotentExecutorService.executeWithIdempotency(
                () -> {
                    OrderResult res = tx.execute((status) -> {
                        CoffeeDto coffee = coffeeService.onOrderCoffee(req.coffeeId(), req.coffeeOrderAmount());
                        BigDecimal totalPoint =
                                coffee.coffeePrice().multiply(BigDecimal.valueOf(req.coffeeOrderAmount()));
                        PointDto pointDto = pointService.usePoint(totalPoint);

                        CoffeeOrderDto coffeeOrderDto =
                                coffeeOrderService.createCoffeeOrder(coffee, req.coffeeOrderAmount(), req.customerId());
                        idempotencyCacheService.putCache(req.idempotencyKey(), coffeeOrderDto);

                        return new OrderResult(coffeeOrderDto, pointDto.pointId(), totalPoint);
                    });

                    pointAuditService.savePointAudit(
                            res.pointId(),
                            PointAuditType.COFFEE_ORDER_SUBTRACTED,
                            res.pointAmount(),
                            res.coffeeOrderDto().coffeeOrderId(),
                            req.customerId());

                    return res.coffeeOrderDto();
                },
                new TypeReference<>() {},
                req.idempotencyKey());
    }

    private record OrderResult(CoffeeOrderDto coffeeOrderDto, Long pointId, BigDecimal pointAmount) {}
}
