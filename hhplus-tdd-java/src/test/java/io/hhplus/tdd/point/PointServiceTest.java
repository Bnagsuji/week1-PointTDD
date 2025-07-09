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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;


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
        Mockito.verify(pointHistoryRepository,Mockito.times(1)).insert(eq(userId),eq(point+amount),eq(TransactionType.CHARGE),anyLong());
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

    @Test
    void 포인트_사용시_성공_테스트(){
        //예상결과 : 기존 DB 포인트에서 차감되고,내역이 history에 저장돼야 함
        //1. pointService.usePoint() return 값이 아직 null이기 때문에 테스트 실패
        //2. 요구사항에 맞도록 pointService.usePoint() 수정

        //given
        long userId = 123L;
        long point = 1000L;
        long amount = 500L;

        //가짜 객체 담기
        Mockito.when(userPointRepository.findById(userId)).thenReturn(new UserPoint(userId,point,System.currentTimeMillis()));

        //사용상태를 가짜객체에 주입
        Mockito.when(userPointRepository.insertOrUpdate(userId,point-amount)).
                thenReturn(new UserPoint(userId,point-amount,System.currentTimeMillis()));

        //when
        UserPoint result = pointService.usePoint(userId,amount);

        //then
        assertEquals(500L,result.point());

        //verify
        Mockito.verify(userPointRepository,Mockito.times(1)).findById(userId);
        Mockito.verify(userPointRepository,Mockito.times(1)).insertOrUpdate(userId,point-amount);
        Mockito.verify(pointHistoryRepository,Mockito.times(1)).insert(eq(userId),eq(point-amount),eq(TransactionType.USE),anyLong());
    }

    @Test
    void 보유포인트보다_초과사용시_실패_테스트() {
        //given
        long userId = 123L;
        long point = 1000L;
        long amount = 1100L;

        //가짜 객체 담기
        Mockito.when(userPointRepository.findById(userId)).thenReturn(new UserPoint(userId,point,System.currentTimeMillis()));

        CustomPointException ex =
                assertThrows(CustomPointException.class,()-> pointService.usePoint(userId,amount));
        //then
        //원하는 예외 결과 비교
        assertEquals(ex.getMessage(),"사용하려는 포인트가 현재 보유 중인 포인트보다 많습니다.");
    }

    @Test
    void 보유포인트보다_정책기준_미달_사용시_실패_테스트() {
        //given
        long userId = 123L;
        long point = 1000L;
        long amount = 10L;

        //가짜 객체 담기
        Mockito.when(userPointRepository.findById(userId)).thenReturn(new UserPoint(userId,point,System.currentTimeMillis()));

        CustomPointException ex =
                assertThrows(CustomPointException.class,()-> pointService.usePoint(userId,amount));
        //then
        //원하는 예외 결과 비교
        assertEquals(ex.getMessage(),"현재 100포인트부터 사용 가능합니다.");
    }




}
