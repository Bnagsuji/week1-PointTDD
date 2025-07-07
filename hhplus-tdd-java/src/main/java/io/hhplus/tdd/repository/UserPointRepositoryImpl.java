package io.hhplus.tdd.repository;

import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Repository;

@Repository
public class UserPointRepositoryImpl implements UserPointRepository {

    @Override
    public UserPoint findById(long userId) {
        return null;
    }

    @Override
    public UserPoint insertOrUpdate(long userId, long amount) {
        return null;
    }
}
