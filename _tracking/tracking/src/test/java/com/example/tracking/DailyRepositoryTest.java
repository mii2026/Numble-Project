package com.example.tracking;

import com.example.tracking.Entity.Daily;
import com.example.tracking.Entity.History;
import com.example.tracking.Repository.DailyRepository;
import com.example.tracking.Repository.HistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"/truncate.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DailyRepositoryTest {
    @Autowired
    private DailyRepository dailyRepository;
    @Autowired
    private HistoryRepository historyRepository;

    @Test
    public void findByUrlWithHistoryTest(){
        //history 없는 경우
        dailyRepository.save(new Daily("github.com"));
        Optional<Daily> optionalDaily1 = this.dailyRepository.findByUrlWithHistory("github.com");
        assertTrue(optionalDaily1.isPresent());
        assertEquals(0, optionalDaily1.get().getHistory().size());

        //history 있는 경우
        Daily daily = new Daily("www.naver.com");
        this.dailyRepository.save(daily);
        for(int i = 1; i <= 4; i++){
            History history = new History(daily, LocalDate.now().minusDays(i), 10L);
            this.historyRepository.save(history);
        }
        Optional<Daily> optionalDaily2 = this.dailyRepository.findByUrlWithHistory("www.naver.com");
        assertTrue(optionalDaily1.isPresent());
        assertEquals(4, optionalDaily2.get().getHistory().size());
        assertEquals(10, optionalDaily2.get().getHistory().get(2).getHit());

        //존재하지 않는 url
        Optional<Daily> optionalDaily3 = this.dailyRepository.findByUrlWithHistory("www.google.com");
        assertFalse(optionalDaily3.isPresent());
    }

    @Test
    public void findAllByTest(){
        //데이터 저장
        List<Daily> dailyList = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            dailyList.add(new Daily("www." + i +".com"));
        }
        this.dailyRepository.saveAll(dailyList);

        //페이징으로 불러오기
        List<Daily> dailyList2 = this.dailyRepository.findAllBy(PageRequest.of(0, 10));

        //길이 및 값 확인
        assertEquals(10, dailyList2.size());
        assertEquals("www.0.com", dailyList2.get(0).getUrl());
    }
}
