package com.example.tracking;

import com.example.tracking.DTO.HistoryDTO;
import com.example.tracking.DTO.HitsDTO;
import com.example.tracking.Entity.Daily;
import com.example.tracking.Entity.History;
import com.example.tracking.Repository.DailyRepository;
import com.example.tracking.Repository.HistoryRepository;
import com.example.tracking.Service.TrackingHitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest @Sql(scripts = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TrackingHitServiceTest {
    @Autowired
    private DailyRepository dailyRepository;
    @Autowired
    private HistoryRepository historyRepository;
    private TrackingHitService trackingHitService;

    @BeforeEach
    public void beforeEach(){
        this.trackingHitService = new TrackingHitService(this.dailyRepository, this.historyRepository);
    }

    @Test
    public void addHitsTest(){
        //최초 생성, 오늘 조회수와 전체 조회수 모두 1
        this.trackingHitService.addHits("www.naver.com");
        Optional<Daily> od = this.dailyRepository.findById(1);
        assertTrue(od.isPresent());
        assertEquals("www.naver.com", od.get().getUrl());
        assertEquals(1, od.get().getTodayHit());
        assertEquals(1, od.get().getTotalHit());

        //존재하는 url hit 증가, 오늘 조회수와 전체 조회수 1씩 증가
        this.trackingHitService.addHits("www.naver.com");
        Optional<Daily> od2 = this.dailyRepository.findById(1);
        assertTrue(od.isPresent());
        assertEquals("www.naver.com", od.get().getUrl());
        assertEquals(2, od2.get().getTodayHit());
        assertEquals(2, od2.get().getTotalHit());
    }

    @Test
    public void getHitsTest(){
        //데이터에 존재하는 url, 가진 값 출력
        this.dailyRepository.save(new Daily("www.naver.com", 6, 10L));
        HitsDTO hits = this.trackingHitService.getHits("www.naver.com");
        assertEquals(6, hits.getTodayHit());
        assertEquals(10, hits.getTotalHit());

        //데이터에 존재하지 않는 url, 0으로 출력
        HitsDTO hits2 = this.trackingHitService.getHits("www.google.com");
        assertEquals(0, hits2.getTodayHit());
        assertEquals(0, hits2.getTotalHit());
    }

    @Test
    public void getHistoryTest(){
        //데이터에 존재하지 않는 url, 전부 0으로 나오는지 확인
        HistoryDTO hits = this.trackingHitService.getHistory("www.google.com");
        for(int i = 0; i < 7; i++)
            System.out.println(hits.getHistoryData()[i]);

        //데이터에 존재하는 url, history가 없는 경우에는 0
        Daily d = new Daily("www.google.com", 6, 30L);
        LocalDate today = LocalDate.now();
        this.dailyRepository.save(d);
        for(int i = 1; i < 4; i++)
            this.historyRepository.save(new History(d, today.minusDays(i), 8));

        HistoryDTO hits2 = this.trackingHitService.getHistory("www.google.com");
        for(int i = 0; i < 7; i++)
            System.out.println(hits2.getHistoryData()[i]);

        //데이터에 존재하는 url, history가 7일 이상 있는 경우
        d.setTotalHit(42L);
        this.dailyRepository.save(d);
        for(int i = 4; i < 7; i++)
            this.historyRepository.save(new History(d, today.minusDays(i), 4));

        HistoryDTO hits3 = this.trackingHitService.getHistory("www.google.com");
        for(int i = 0; i < 7; i++)
            System.out.println(hits3.getHistoryData()[i]);
    }

    @Test
    public void nextDayTest(){
        //데이터 저장
        Daily d = new Daily("www.google.com", 6, 22L);
        LocalDate today = LocalDate.now();
        this.dailyRepository.save(d);
        for(int i = 1; i < 3; i++)
            this.historyRepository.save(new History(d, today.minusDays(i+1), 8));

        //다음날에 오늘 조회수 0, 히스토리 변화 확인
        this.trackingHitService.nextDay();
        Optional<Daily> od = this.dailyRepository.findByUrlWithHistory("www.google.com");
        assertTrue(od.isPresent());
        assertEquals(0, od.get().getTodayHit());
        assertEquals(3, od.get().getHistory().size());
        assertEquals(6, od.get().getHistory().get(2).getHit());
    }
}
