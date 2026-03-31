package coffee.server.domain.coffee.controller;

import coffee.server.common.dto.BaseResponse;
import coffee.server.domain.coffee.dto.CoffeeDto;
import coffee.server.domain.coffee.service.CoffeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CoffeeController {
    private final CoffeeService coffeeService;

    @GetMapping("/api/coffees/{coffeeId}")
    public ResponseEntity<BaseResponse<CoffeeDto>> getCoffee(@PathVariable Long coffeeId) {
        CoffeeDto res = coffeeService.getCoffe(coffeeId);

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(res));
    }
}
