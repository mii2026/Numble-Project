package com.example.tracking;

import com.example.tracking.Entity.Daily;
import com.example.tracking.Entity.History;
import com.example.tracking.Repository.DailyRepository;
import com.example.tracking.Repository.HistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @Sql(scripts = {"/sql/truncate.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DailyRepositoryTest {
    @Autowired
    private DailyRepository dailyRepository;
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
        od = this.dailyRepository.findByUrlWithHistory("https%3A%2F%2Fwww.naver.com");
        assertTrue(od.isPresent());
        assertEquals(4, od.get().getHistory().size());
        assertEquals(10, od.get().getHistory().get(2).getHit());

        //존재하지 않는 url
        od = this.dailyRepository.findByUrlWithHistory("https%3A%2F%2Fwww.google.com");
        assertFalse(od.isPresent());
    }
}
