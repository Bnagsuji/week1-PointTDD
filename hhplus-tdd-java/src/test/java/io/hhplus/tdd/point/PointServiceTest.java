package io.hhplus.tdd.point;

import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    PointHistoryRepository pointHistoryRepository;

    @Mock
    UserPointRepository userPointRepository;

    @InjectMocks
    PointService pointService;



    @Test
    void 포인트_조회() {

        //given
        long userId = 123L;
        long point = 100;

        UserPoint userPoint = new UserPoint(userId,point, System.currentTimeMillis());


        //when
        Mockito.when(userPointRepository.findById(userId)).thenReturn(userPoint);


        //verify


    }
}
