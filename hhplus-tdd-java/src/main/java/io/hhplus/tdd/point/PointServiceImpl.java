package io.hhplus.tdd.point;


import io.hhplus.tdd.CustomPointException;
import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;


    @Override
    public UserPoint getUserPoint(long userId) {
        return userPointRepository.findById(userId);
    }

    @Override
    public UserPoint usePoint(long userId, long amount) {
        return null;
    }

    @Override
    public UserPoint chargePoint(long userId, long amount) {


        try {
            long originPoint =  userPointRepository.findById(userId).point();

            //한 번에 100000L를 초과하는 금액 충전 불가
            if(amount > 100000L) {
                throw new CustomPointException("최대 포인트 충전 정책에 맞지 않는 금액입니다.");
            }

            //충전 시 100L 미만의 금액 충전 불가
            if(amount < 100L) {
                throw new CustomPointException("최소 포인트 충전 정책에 맞지 않는 금액입니다.");
            }

            UserPoint result = userPointRepository.insertOrUpdate(userId,originPoint+amount);
            pointHistoryRepository.insert(result.id(), result.point(), TransactionType.CHARGE, result.updateMillis());
            return  result;
        }finally {

        }


    }

    @Override
    public List<PointHistory> userPointList(long userId) {
        return null;
    }
}
