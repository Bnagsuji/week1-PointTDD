package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class PointServiceIntegrationTest {

    //실제 서비스, DB 붙여서 테스트 해보기
    @Autowired
    PointService pointService;

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @Autowired
    UserPointRepository userPointRepository;

    @Autowired
    UserPointTable userPointTable;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void 유저_포인트_조회_통합테스트(){
        // given
        long userId = 1L;

        // when
        UserPoint result = pointService.getUserPoint(userId);

        // then
        assertEquals(0, result.point());
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
    void 포인트_충전_시_요청은_성공한다() throws Exception {
        //given
        long userId = 5L;
        long amount = 100L;
        long plusAmount = 200L;
        long resultAmount = amount + plusAmount;

        // when
        userPointRepository.insertOrUpdate(userId, amount);

        // then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType("application/json")
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(resultAmount));
    }


}
