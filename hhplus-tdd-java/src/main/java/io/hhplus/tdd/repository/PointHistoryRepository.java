package io.hhplus.tdd.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PointHistoryRepository {
    void insert(long userId, long amount, TransactionType type, long timestamp);
    List<PointHistory> selectAllByUserId(long userId);
}
