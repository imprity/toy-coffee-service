package coffee.server.common.initializer;

import coffee.server.domain.point.entity.Point;
import coffee.server.domain.point.repository.PointRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointInitializer implements ApplicationRunner {
    private final PointRepository pointRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        List<Point> points = pointRepository.findAll();

        if (points.isEmpty()) {
            pointRepository.save(Point.create(BigDecimal.ZERO));
        }
    }
}
