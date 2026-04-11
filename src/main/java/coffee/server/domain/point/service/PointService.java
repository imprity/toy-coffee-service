package coffee.server.domain.point.service;

import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.ServiceException;
import coffee.server.common.exception.UserFacingServiceException;
import coffee.server.domain.point.dto.PointDto;
import coffee.server.domain.point.entity.Point;
import coffee.server.domain.point.exception.PointExceptionHelper;
import coffee.server.domain.point.repository.PointRepository;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    public PointDto getPoint(String customerId) {
        Point point = pointRepository
                .findByCustomerId(customerId)
                .orElseThrow(() -> PointExceptionHelper.createPointNotFoundByCustomerId(customerId));

        return PointDto.of(point);
    }

    @Transactional
    public PointDto setPoint(String customerId, BigDecimal toSet) {
        throwIfOpNumberNotPositive(customerId, toSet, "set");

        Optional<Point> maybePoint = pointRepository.findByCustomerIdWithLock(customerId);

        Point point;
        if (maybePoint.isPresent()) {
            point = maybePoint.get();
        } else {
            point = Point.create(BigDecimal.ZERO, customerId);
        }

        point.updatePointAmount(toSet);

        point = pointRepository.saveAndFlush(point);

        return PointDto.of(point);
    }

    @Transactional
    public PointDto addPoint(String customerId, BigDecimal toAdd) {
        throwIfOpNumberNotPositive(customerId, toAdd, "add");

        Optional<Point> maybePoint = pointRepository.findByCustomerIdWithLock(customerId);

        Point point;
        if (maybePoint.isPresent()) {
            point = maybePoint.get();
        } else {
            point = Point.create(BigDecimal.ZERO, customerId);
        }

        point.updatePointAmount(point.getPointAmount().add(toAdd));

        point = pointRepository.saveAndFlush(point);

        return PointDto.of(point);
    }

    @Transactional
    public PointDto usePoint(String customerId, BigDecimal toUse) {
        throwIfOpNumberNotPositive(customerId, toUse, "use");

        Point point = pointRepository
                .findByCustomerIdWithLock(customerId)
                .orElseThrow(() -> PointExceptionHelper.createPointNotFoundByCustomerId(customerId));

        BigDecimal newPointAmount = point.getPointAmount().subtract(toUse);

        if (newPointAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new UserFacingServiceException(
                    ErrorCode.POINT_INSUFFICIENT_AMOUNT,
                    HttpStatus.CONFLICT,
                    PointDto.of(point),
                    "Insufficient point. Tried to use (%s) when point amount is only (%s)."
                            .formatted(toUse, point.getPointAmount()));
        }

        point.updatePointAmount(newPointAmount);

        point = pointRepository.saveAndFlush(point);

        return PointDto.of(point);
    }

    private void throwIfOpNumberNotPositive(String customerId, BigDecimal amount, String opName) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException(
                    ErrorCode.ERROR,
                    "Tried to %s point(customerId %s)`s point amount with (%s) value. Number should be >= 0."
                            .formatted(opName, customerId, amount));
        }
    }
}
