package io.hhplus.tdd.point;

import io.hhplus.tdd.dto.PointHistoryResponse;
import io.hhplus.tdd.dto.PointRequest;
import io.hhplus.tdd.dto.PointResponse;
import io.hhplus.tdd.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointServiceImpl pointService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public PointResponse point(
            @PathVariable long id
    ) {
        UserPoint userPoint = pointService.getUserPoint(id);
        return PointResponse.from(userPoint);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistoryResponse> history(
            @PathVariable long id
    ) {
        List<PointHistory> list = pointService.userPointList(id);
        return PointHistoryResponse.fromList(list);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */

    @PatchMapping("{id}/charge")
    public PointResponse charge(
            @PathVariable long id,
            @RequestBody PointRequest pointRequest
    ) {
        UserPoint userPoint = pointService.chargePoint(id, pointRequest.amount());

        return PointResponse.from(userPoint);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public PointResponse use(
            @PathVariable long id,
            @RequestBody PointRequest pointRequest
    ) {
        UserPoint userPoint = pointService.usePoint(id, pointRequest.amount());

        return PointResponse.from(userPoint);
    }
}
