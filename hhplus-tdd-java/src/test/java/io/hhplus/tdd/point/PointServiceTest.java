package io.hhplus.tdd.point;

import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    PointHistoryRepository pointHistoryRepository;

    @Mock
    UserPointRepository userPointRepository;

    @InjectMocks
    PointServiceImpl pointService;



    @Test
    void 포인트_조회_테스트() {

        //given
        long userId = 123L;
        long point = 100;

        UserPoint userPoint = new UserPoint(userId,point, System.currentTimeMillis());

        //when
        Mockito.when(userPointRepository.findById(userId)).thenReturn(userPoint);

        //then
        UserPoint result = pointService.getUserPoint(userId);
        assertEquals(point, result.point());

        //verify
        Mockito.verify(userPointRepository,Mockito.times(1)).findById(userId);
    }
}
