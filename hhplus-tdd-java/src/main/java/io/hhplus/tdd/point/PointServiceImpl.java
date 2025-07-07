package io.hhplus.tdd.point;


import io.hhplus.tdd.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final UserPointRepository userPointRepository;


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
        return null;
    }

    @Override
    public List<PointHistory> userPointList(long userId) {
        return null;
    }
}
