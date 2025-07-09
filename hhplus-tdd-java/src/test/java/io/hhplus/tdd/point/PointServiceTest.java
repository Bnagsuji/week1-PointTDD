package io.hhplus.tdd.point;

import io.hhplus.tdd.CustomPointException;
import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


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


    @Test
    void 포인트_충전_성공_테스트() {
        //point > 0, 500단위로 충전 성공

        //given
        long userId = 123L;
        long point = 100;
        long amount = 500;

        //기존 상태를 가짜객체에 주입
        Mockito.when(userPointRepository.findById(userId)).
                thenReturn(new UserPoint(userId,point,System.currentTimeMillis()));

        //충전상태의를 가짜객체에 주입
        Mockito.when(userPointRepository.insertOrUpdate(userId,point+amount)).
                thenReturn(new UserPoint(userId,point+amount,System.currentTimeMillis()));

        //when
        UserPoint afterUser = pointService.chargePoint(userId,amount);

        //then
        assertEquals(point+amount,afterUser.point());

        //verify
        Mockito.verify(userPointRepository,Mockito.times(1)).findById(userId);
        Mockito.verify(userPointRepository,Mockito.times(1)).insertOrUpdate(userId,point+amount);
        Mockito.verify(pointHistoryRepository,Mockito.times(1)).insert(userId,point+amount,TransactionType.CHARGE,System.currentTimeMillis());


    }

    @Test
    void 포인트_충전_100L미만시_실패_테스트() {

        //given
        long userId = 123L;
        long point = 90L;

        //값을 넣어서 가짜DB 정보 만들기
        Mockito.when(userPointRepository.findById(userId)).thenReturn(new UserPoint(userId,500L,System.currentTimeMillis()));
        //when
        //100미만 값 넣어서 원하는 예외가 나오는 지 테스트
        CustomPointException ex =
                assertThrows(CustomPointException.class,()-> pointService.chargePoint(userId,point));
        //then
        //원하는 예외 결과 비교
        assertEquals(ex.getMessage(),"최소 포인트 충전 정책에 맞지 않는 금액입니다.");

        //verify 예외처리 확인했기 때문에 생략
    }



    @Test
    void 포인트_충전_100000L초과시_실패_테스트() {

        //given
        long userId = 123L;
        long point = 100001L;

        //값을 넣어서 가짜DB 정보 만들기
        Mockito.when(userPointRepository.findById(userId)).thenReturn(new UserPoint(userId,500L,System.currentTimeMillis()));
        //when
        //100미만 값 넣어서 원하는 예외가 나오는 지 테스트
        CustomPointException ex =
                assertThrows(CustomPointException.class,()-> pointService.chargePoint(userId,point));
        //then
        //원하는 예외 결과 비교
        assertEquals(ex.getMessage(),"최대 포인트 충전 정책에 맞지 않는 금액입니다.");

        //verify 예외처리 확인했기 때문에 생략
    }




}
