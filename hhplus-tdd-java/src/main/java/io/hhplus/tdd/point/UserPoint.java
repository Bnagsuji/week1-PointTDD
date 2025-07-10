package io.hhplus.tdd.point;

import io.hhplus.tdd.CustomPointException;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    public UserPoint {
        if(point < 0){
            throw new CustomPointException("유효한 포인트 값이 아닙니다.");
        }
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
