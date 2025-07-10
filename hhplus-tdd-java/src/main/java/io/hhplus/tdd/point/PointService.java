package io.hhplus.tdd.point;

import java.util.List;

public interface PointService {
    //충전, 사용
    UserPoint getUserPoint(long userId);
    UserPoint usePoint(long userId, long amount);
    UserPoint chargePoint(long userId, long amount);
    List<PointHistory> userPointList(long userId);
}
