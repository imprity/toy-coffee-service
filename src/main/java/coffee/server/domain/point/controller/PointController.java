package coffee.server.domain.point.controller;

import coffee.server.common.dto.BaseResponse;
import coffee.server.domain.point.dto.AddPointRequest;
import coffee.server.domain.point.dto.GetPointRequest;
import coffee.server.domain.point.dto.PointDto;
import coffee.server.domain.point.dto.SetPointRequest;
import coffee.server.domain.point.facade.PointFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PointController {
    private final PointFacade pointFacade;

    @GetMapping("/api/points")
    public ResponseEntity<BaseResponse<PointDto>> getPoint(@RequestBody @Valid GetPointRequest getPointRequest) {
        PointDto res = pointFacade.getPoint(getPointRequest.customerId());

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(res));
    }

    @PostMapping("/api/points/set")
    public ResponseEntity<BaseResponse<PointDto>> setPoint(@RequestBody @Valid SetPointRequest setPointRequest) {
        PointDto res = pointFacade.setPoint(setPointRequest);

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(res));
    }

    @PostMapping("/api/points/add")
    public ResponseEntity<BaseResponse<PointDto>> addPoint(@RequestBody @Valid AddPointRequest addPointRequest) {
        PointDto res = pointFacade.addPoint(addPointRequest);

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(res));
    }
}
