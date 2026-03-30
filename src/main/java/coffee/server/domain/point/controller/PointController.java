package coffee.server.domain.point.controller;

import coffee.server.common.dto.BaseResponse;
import coffee.server.domain.point.dto.AddPointRequest;
import coffee.server.domain.point.dto.GetPointResponse;
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
    public ResponseEntity<BaseResponse<GetPointResponse>> getPoint() {
        GetPointResponse res = pointFacade.getPoint();

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(res));
    }

    @PostMapping("/api/points/set")
    public ResponseEntity<BaseResponse<GetPointResponse>> setPoint(
            @RequestBody @Valid SetPointRequest setPointRequest) {
        GetPointResponse res = pointFacade.setPoint(setPointRequest);

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(res));
    }

    @PostMapping("/api/points/add")
    public ResponseEntity<BaseResponse<GetPointResponse>> addPoint(
            @RequestBody @Valid AddPointRequest AddPointRequest) {
        GetPointResponse res = pointFacade.addPoint(AddPointRequest);

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success(res));
    }
}
