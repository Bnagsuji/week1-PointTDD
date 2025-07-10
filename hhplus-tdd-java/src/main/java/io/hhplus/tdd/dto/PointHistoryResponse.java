package io.hhplus.tdd.dto;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;

import java.util.List;

public record PointHistoryResponse(
        long id,
        long amount,
        TransactionType type
) {
    public static PointHistoryResponse from(PointHistory pointHistory) {
        return new PointHistoryResponse(
                pointHistory.userId(),
                pointHistory.amount(),
                pointHistory.type()
        );
    }

    public static List<PointHistoryResponse> fromList(List<PointHistory> list) {
        return list.stream().map(PointHistoryResponse::from). toList();
    }
}