package io.hhplus.tdd.repository;

import io.hhplus.tdd.point.UserPoint;

public interface UserPointRepository {
    UserPoint findById(long userId);
    UserPoint insertOrUpdate(long userId, long amount);

    void clear();

}
