package coffee.server.domain.coffee.service;

import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.ServiceException;
import coffee.server.common.exception.UserFacingServiceException;
import coffee.server.domain.coffee.dto.CoffeeDto;
import coffee.server.domain.coffee.entity.Coffee;
import coffee.server.domain.coffee.enums.CoffeeStatus;
import coffee.server.domain.coffee.exception.CoffeeExceptionHelper;
import coffee.server.domain.coffee.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeService {
    private final CoffeeRepository coffeeRepository;

    @Transactional(readOnly = true)
    public CoffeeDto getCoffe(Long coffeeId) {
        Coffee coffee = coffeeRepository
                .findById(coffeeId)
                .orElseThrow(() -> CoffeeExceptionHelper.createCoffeeNotFound(coffeeId));

        return CoffeeDto.of(coffee);
    }

    /**
     * 커피의 재고를 줄입니다.
     * <p>
     * 커피가 단종 되었거나 현 커피의 재고 양보다 줄일려고 하는 양이 많을 경우 에러를 던집니다.
     * @param coffeeId 재고를 줄이고 싶은 커피 id
     * @param amount 재고를 줄일 양
     */
    @Transactional
    public CoffeeDto decreaseCoffeeStockForOrder(Long coffeeId, Long amount) {
        if (amount < 0) {
            throw new ServiceException(
                    ErrorCode.ERROR,
                    "tried to decrease coffee(id %s)'s stock by (%s) amount. can't decrease stock by negative number"
                            .formatted(coffeeId, amount));
        }

        Coffee coffee = coffeeRepository
                .findByIdWithLock(coffeeId)
                .orElseThrow(() -> CoffeeExceptionHelper.createCoffeeNotFound(coffeeId));

        if (coffee.getCoffeeStatus() == CoffeeStatus.DISCONTINUED) {
            throw new UserFacingServiceException(
                    ErrorCode.COFFEE_DICONTINUED,
                    HttpStatus.CONFLICT,
                    CoffeeDto.of(coffee),
                    "coffee has been discontinued");
        }

        Long newStock = coffee.getCoffeeStock() - amount;

        if (newStock < 0) {
            throw new UserFacingServiceException(
                    ErrorCode.COFFEE_INSUFFICIENT_STOCK,
                    HttpStatus.CONFLICT,
                    CoffeeDto.of(coffee),
                    "tried to order (%s) amount of coffee. but we only have (%s) coffee(id %s)"
                            .formatted(amount, coffee.getCoffeeStock(), coffee.getCoffeeId()));
        }

        coffee.updateCoffeeStock(newStock);

        coffee = coffeeRepository.save(coffee);

        return CoffeeDto.of(coffee);
    }
}
