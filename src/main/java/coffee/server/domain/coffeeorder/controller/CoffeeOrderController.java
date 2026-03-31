package coffee.server.domain.coffeeorder.controller;

import coffee.server.common.dto.BaseResponse;
import coffee.server.domain.coffeeorder.dto.CoffeeOrderDto;
import coffee.server.domain.coffeeorder.dto.CoffeeOrderRequest;
import coffee.server.domain.coffeeorder.facade.CoffeeOrderFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CoffeeOrderController {
    private final CoffeeOrderFacade coffeeOrderFacade;

    @PostMapping("/api/coffee-orders")
    public ResponseEntity<BaseResponse<CoffeeOrderDto>> getCoffee(@Valid @RequestBody CoffeeOrderRequest req) {
        CoffeeOrderDto res = coffeeOrderFacade.orderCoffee(req);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(res));
    }
}
