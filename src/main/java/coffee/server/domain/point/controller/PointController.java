package coffee.server.domain.point.controller;

import coffee.server.common.dto.BaseResponse;
import coffee.server.domain.point.dto.GetPointResponse;
import coffee.server.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;

    @GetMapping("/api/points")
    public ResponseEntity<BaseResponse<GetPointResponse>> getPoint() {
        GetPointResponse res = pointService.getPoint();

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(res));
    }
}
