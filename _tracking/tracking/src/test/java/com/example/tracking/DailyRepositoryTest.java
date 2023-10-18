package com.example.tracking;

import com.example.tracking.Entity.Daily;
import com.example.tracking.Entity.History;
import com.example.tracking.Repository.DailyBulkRepository;
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

@SpringBootTest @ActiveProfiles("test")
@Sql(scripts = {"/truncate.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DailyRepositoryTest {
    @Autowired
    private DailyRepository dailyRepository;
    @Autowired
    private DailyBulkRepository dailyBulkRepository;
    @Autowired
    private HistoryRepository historyRepository;

    @Test
    public void findByUrlWithHistoryTest(){
        //history 없는 경우
        Daily d1 = new Daily("https%3A%2F%2Fgithub.com", 1, 1L);
        this.dailyRepository.save(d1);
        Optional<Daily> od = this.dailyRepository.findByUrlWithHistory("https%3A%2F%2Fgithub.com");
        assertTrue(od.isPresent());
        assertEquals(0, od.get().getHistory().size());

        //history 있는 경우
        Daily d2 = new Daily("https%3A%2F%2Fwww.naver.com", 10, 50L);
        this.dailyRepository.save(d2);
        for(int i = 1; i <= 4; i++){
            History h = new History(d2, LocalDate.now().minusDays(i), 10);
            this.historyRepository.save(h);
        }
        Optional<Daily> od2 = this.dailyRepository.findByUrlWithHistory("https%3A%2F%2Fwww.naver.com");
        assertTrue(od.isPresent());
        assertEquals(4, od2.get().getHistory().size());
        assertEquals(10, od2.get().getHistory().get(2).getHit());

        //존재하지 않는 url
        od = this.dailyRepository.findByUrlWithHistory("https%3A%2F%2Fwww.google.com");
        assertFalse(od.isPresent());
    }

    @Test
    public void findAllByTest(){
        //데이터 저장
        List<Daily> dlist = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            dlist.add(new Daily("www." + i +".com", 1, 1L));
        }
        this.dailyBulkRepository.saveAll(dlist);

        //페이징으로 불러오기
        List<Daily> dlist2 = this.dailyRepository.findAllBy(PageRequest.of(0, 10));

        //길이 및 값 확인
        assertEquals(10, dlist2.size());
        assertEquals("www.0.com", dlist2.get(0).getUrl());
    }
}
