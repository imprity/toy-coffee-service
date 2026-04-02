package coffee.server.domain.point.service;

import coffee.server.common.config.AppConfig;
import coffee.server.common.exception.ErrorCode;
import coffee.server.common.exception.ServiceException;
import coffee.server.common.exception.UserFacingServiceException;
import coffee.server.domain.point.dto.PointDto;
import coffee.server.domain.point.entity.Point;
import coffee.server.domain.point.exception.PointExceptionHelper;
import coffee.server.domain.point.repository.PointRepository;
import java.math.BigDecimal;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final AppConfig appConfig;

    @Transactional(readOnly = true)
    public PointDto getPoint() {
        Long pointId = appConfig.getPointIdInDatabase();

        Point point =
                pointRepository.findById(pointId).orElseThrow(() -> PointExceptionHelper.createPointNotFound(pointId));

        return PointDto.of(point);
    }

    @Transactional()
    public PointDto setPoint(@NonNull BigDecimal amount) {
        Long pointId = appConfig.getPointIdInDatabase();

        throwIfOpNumberNotPositive(pointId, amount, "set");

        Point point = pointRepository
                .findByIdWithLock(pointId)
                .orElseThrow(() -> PointExceptionHelper.createPointNotFound(pointId));

        point.updatePointAmount(amount);

        point = pointRepository.saveAndFlush(point);

        return PointDto.of(point);
    }

    @Transactional()
    public PointDto addPoint(@NonNull BigDecimal toAdd) {
        Long pointId = appConfig.getPointIdInDatabase();

        throwIfOpNumberNotPositive(pointId, toAdd, "add");

        Point point = pointRepository
                .findByIdWithLock(pointId)
                .orElseThrow(() -> PointExceptionHelper.createPointNotFound(pointId));

        point.updatePointAmount(point.getPointAmount().add(toAdd));

        point = pointRepository.saveAndFlush(point);

        return PointDto.of(point);
    }

    @Transactional()
    public PointDto usePoint(@NonNull BigDecimal toUse) {
        Long pointId = appConfig.getPointIdInDatabase();

        throwIfOpNumberNotPositive(pointId, toUse, "use");

        Point point = pointRepository
                .findByIdWithLock(pointId)
                .orElseThrow(() -> PointExceptionHelper.createPointNotFound(pointId));

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

    private void throwIfOpNumberNotPositive(Long pointId, BigDecimal amount, String opName) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException(
                    ErrorCode.ERROR,
                    "Tried to %s point(id %s)`s point amount with (%s) value. Number should be >= 0."
                            .formatted(opName, pointId, amount));
        }
    }
}
