package io.hhplus.tdd.repository;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;

import java.util.List;

public class PointHistoryRepositoryImpl implements PointHistoryRepository {
    @Override
    public void insert(long userId, long amount, TransactionType type, long timestamp) {

    }

    @Override
    public List<PointHistory> selectAllByUserId(long userId) {
        return List.of();
    }
}
