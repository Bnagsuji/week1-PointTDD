package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.dto.PointRequest;
import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class PointServiceIntegrationTest {

    //실제 서비스, DB 붙여서 테스트 해보기
    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @Autowired
    UserPointRepository userPointRepository;

    @Autowired
    UserPointTable userPointTable;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PointServiceImpl pointService;

    @AfterEach
    void tearDown() {
        userPointRepository.clear();
        pointHistoryRepository.clear();
    }


    @Test
    void 포인트_조회_성공_테스트() throws Exception {
        // given
        long userId = 2L;
        long amount = 100L;

        // when
        UserPoint userPoint = userPointRepository.insertOrUpdate(userId, amount);

        // then
        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userPoint.id()))
                .andExpect(jsonPath("$.point").value(userPoint.point()));
    }





    @Test
    void 포인트_충전_요청_성공_후_히스토리_저장_테스트() throws Exception {
        //given
        long userId = 123L;
        long amount = 100L;
        long plusAmount = 200L;
        long resultAmount = amount + plusAmount;

        // when
        userPointRepository.insertOrUpdate(userId, amount);
        PointRequest req = new PointRequest(userId,plusAmount);

        ObjectMapper objectMapper = new ObjectMapper();
        String resContent = objectMapper.writeValueAsString(req);

        // then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType("application/json")
                        .content(resContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(resultAmount));

        List<PointHistory> historyList = pointHistoryRepository.selectAllByUserId(userId);
        assertThat(historyList).hasSize(1);
        PointHistory history = historyList.get(0);
        assertThat(history.userId()).isEqualTo(userId);
        assertThat(history.amount()).isEqualTo(resultAmount);
        assertThat(history.type()).isEqualTo(TransactionType.CHARGE);
    }

    @Test
    void 포인트_충전_시_100000_초과일_경우_요청_실패_테스트() throws Exception {
        //given
        long userId = 1234L;
        long amount = 100L;
        long plusAmount = 100001L;

        // when
        userPointTable.insertOrUpdate(userId, amount);
        PointRequest pointRequest = new PointRequest(userId, plusAmount);

        // ObjectMapper를 사용하여 DTO를 JSON으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(pointRequest);

        // then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType("application/json")
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("최대 포인트 충전 정책에 맞지 않는 금액입니다."));
    }

    @Test
    void 포인트_충전_시_100_미만일_경우_요청_실패_테스트() throws Exception {
        //given
        long userId = 1234L;
        long amount = 100L;
        long plusAmount = 99L;

        // when
        userPointTable.insertOrUpdate(userId, amount);
        PointRequest pointRequest = new PointRequest(userId, plusAmount);

        // ObjectMapper를 사용하여 DTO를 JSON으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(pointRequest);

        // then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType("application/json")
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("최소 포인트 충전 정책에 맞지 않는 금액입니다."));
    }

    @Test
    void 포인트_사용_요청_성공_후_히스토리_저장_테스트() throws Exception {
        //given
        long userId = 123L;
        long amount = 300L;
        long useAmount = 200L;
        long resultAmount = amount - useAmount;

        // when
        userPointRepository.insertOrUpdate(userId, amount);
        PointRequest req = new PointRequest(userId,useAmount);

        ObjectMapper objectMapper = new ObjectMapper();
        String resContent = objectMapper.writeValueAsString(req);

        // then
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType("application/json")
                        .content(resContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(resultAmount));

        List<PointHistory> historyList = pointHistoryRepository.selectAllByUserId(userId);
        assertThat(historyList).hasSize(1);
        PointHistory history = historyList.get(0);
        assertThat(history.userId()).isEqualTo(userId);
        assertThat(history.amount()).isEqualTo(resultAmount);
        assertThat(history.type()).isEqualTo(TransactionType.USE);
    }


    @Test
    void 포인트_사용_요청_시_잔액부족_실패_테스트() throws Exception {
        //given
        long userId = 123L;
        long amount = 300L;
        long useAmount = 350L;

        // when
        userPointRepository.insertOrUpdate(userId, amount);
        PointRequest req = new PointRequest(userId,useAmount);

        ObjectMapper objectMapper = new ObjectMapper();
        String resContent = objectMapper.writeValueAsString(req);

        // then
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType("application/json")
                        .content(resContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("사용하려는 포인트가 현재 보유 중인 포인트보다 많습니다."));
    }

    @Test
    void 포인트_사용_요청_시_정책기준_미달_사용_실패_테스트() throws Exception {
        //given
        long userId = 123L;
        long amount = 300L;
        long useAmount = 10;

        // when
        userPointRepository.insertOrUpdate(userId, amount);
        PointRequest req = new PointRequest(userId,useAmount);

        ObjectMapper objectMapper = new ObjectMapper();
        String resContent = objectMapper.writeValueAsString(req);

        // then
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType("application/json")
                        .content(resContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("현재 100포인트부터 사용 가능합니다."));
    }

    @Test
    void 포인트_충전_사용_후_포인트_내역_조회_성공_테스트() throws Exception {
        //given
        long userId = 123L;
        long amount = 100L;
        long plusAmount = 1000L;
        long useAmount = 500L;

        // when
        userPointRepository.insertOrUpdate(userId, amount);
        pointService.chargePoint(userId, plusAmount) ;
        pointService.usePoint(userId, useAmount) ;

        long afterChargePoint = amount+plusAmount;
        long afterUserPoint = afterChargePoint-useAmount;
        // then
        mockMvc.perform(get("/point/{id}/histories", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(userId))
                .andExpect(jsonPath("$[0].amount").value(afterChargePoint))
                .andExpect(jsonPath("$[0].type").value(TransactionType.CHARGE.toString()))
                .andExpect(jsonPath("$[1].id").value(userId))
                .andExpect(jsonPath("$[1].amount").value(afterUserPoint))
                .andExpect(jsonPath("$[1].type").value(TransactionType.USE.toString()));
    }

    @Test
    void 유저_포인트_내역_미존재_시_실패_테스트() throws Exception {
        long userId = 123L;

        mockMvc.perform(get("/point/{id}/histories", userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("포인트 내역이 존재하지 않습니다."));
    }



}
