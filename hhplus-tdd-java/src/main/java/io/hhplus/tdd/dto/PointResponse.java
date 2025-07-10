package io.hhplus.tdd.dto;

import io.hhplus.tdd.point.UserPoint;

public record PointResponse(
        long id,
        long point
) {
    public static PointResponse from(UserPoint userPoint) {
        return new PointResponse(
                userPoint.id(),
                userPoint.point()
        );
    }
}