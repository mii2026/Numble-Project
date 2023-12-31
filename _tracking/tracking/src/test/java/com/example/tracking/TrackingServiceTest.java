package com.example.tracking;

import com.example.tracking.DTO.HistoryDTO;
import com.example.tracking.DTO.HitsDTO;
import com.example.tracking.Entity.Daily;
import com.example.tracking.Entity.History;
import com.example.tracking.Repository.DailyBulkRepository;
import com.example.tracking.Repository.DailyRepository;
import com.example.tracking.Repository.HistoryBulkRepository;
import com.example.tracking.Repository.HistoryRepository;
import com.example.tracking.Service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest @ActiveProfiles("test")
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TrackingServiceTest {
    @Autowired
    private DailyRepository dailyRepository;
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private DailyBulkRepository dailyBulkRepository;
    @Autowired
    private HistoryBulkRepository historyBulkRepository;
    private TrackingService trackingService;

    @BeforeEach
    public void beforeEach(){
        this.trackingService = new TrackingService(this.dailyRepository, this.dailyBulkRepository, this.historyBulkRepository);
    }

    @Test
    public void addHitsTest(){
        //최초 생성, 오늘 조회수와 전체 조회수 모두 1
        this.trackingService.addHits("www.naver.com");
        Optional<Daily> optionalDaily1 = this.dailyRepository.findById(1);
        assertTrue(optionalDaily1.isPresent());
        assertEquals("www.naver.com", optionalDaily1.get().getUrl());
        assertEquals(1, optionalDaily1.get().getTodayHit());
        assertEquals(1, optionalDaily1.get().getTotalHit());

        //존재하는 url hit 증가, 오늘 조회수와 전체 조회수 1씩 증가
        this.trackingService.addHits("www.naver.com");
        Optional<Daily> optionalDaily2 = this.dailyRepository.findById(1);
        assertTrue(optionalDaily2.isPresent());
        assertEquals("www.naver.com", optionalDaily2.get().getUrl());
        assertEquals(2, optionalDaily2.get().getTodayHit());
        assertEquals(2, optionalDaily2.get().getTotalHit());
    }

    @Test
    public void getHitsTest(){
        //데이터에 존재하는 url, 가진 값 출력
        this.dailyRepository.save(new Daily("www.naver.com", 6, 10L));
        HitsDTO hits1 = this.trackingService.getHits("www.naver.com");
        assertEquals(6, hits1.getTodayHit());
        assertEquals(10, hits1.getTotalHit());

        //데이터에 존재하지 않는 url, 0으로 출력
        HitsDTO hits2 = this.trackingService.getHits("www.google.com");
        assertEquals(0, hits2.getTodayHit());
        assertEquals(0, hits2.getTotalHit());
    }

    @Test
    public void getHistoryTest(){
        //데이터에 존재하지 않는 url, hit 전부 0으로 나오는지 확인
        HistoryDTO hits1 = this.trackingService.getHistory("www.google.com");
        for(int i = 0; i < 7; i++)
            assertEquals(0, hits1.getHit(i));

        //데이터에 존재하는 url, history가 7일 이하로 있는 경우, history 없는 날짜에는 hit 0인지 확인
        Daily daily = new Daily("www.google.com", 6, 30L);
        LocalDate today = LocalDate.now();
        this.dailyRepository.save(daily);
        for(int i = 1; i < 4; i++)
            this.historyRepository.save(new History(daily, today.minusDays(i), 8));

        HistoryDTO hits2 = this.trackingService.getHistory("www.google.com");
        assertEquals(6, hits2.getHit(0));
        for(int i = 1; i < 4; i++)
            assertEquals(8, hits2.getHit(i));
        for(int i = 4; i < 7 ; i++)
            assertEquals(0, hits2.getHit(i));

        //데이터에 존재하는 url, history가 7일 이상 있는 경우
        daily.setTotalHit(42L);
        this.dailyRepository.save(daily);
        for(int i = 4; i < 7; i++)
            this.historyRepository.save(new History(daily, today.minusDays(i), 4));

        HistoryDTO hits3 = this.trackingService.getHistory("www.google.com");
        assertEquals(6, hits3.getHit(0));
        for(int i = 1; i < 4; i++)
            assertEquals(8, hits3.getHit(i));
        for(int i = 4; i < 7 ; i++)
            assertEquals(4, hits3.getHit(i));
    }

    @Test
    public void updateTodayHitsToHistroyTest(){
        //데이터 저장
        Daily daily = new Daily("www.google.com", 6, 22L);
        LocalDate today = LocalDate.now();
        this.dailyRepository.save(daily);
        for(int i = 1; i < 3; i++)
            this.historyRepository.save(new History(daily, today.minusDays(i+1), 8));

        //다음날에 오늘 조회수 0, 히스토리 변화 확인
        this.trackingService.updateTodayHitsToHistroy();
        Optional<Daily> optionalDaily = this.dailyRepository.findByUrlWithHistory("www.google.com");
        assertTrue(optionalDaily.isPresent());
        assertEquals(0, optionalDaily.get().getTodayHit());
        assertEquals(3, optionalDaily.get().getHistory().size());
        assertEquals(6, optionalDaily.get().getHistory().get(2).getHit());
    }

    public void updateTodayHitsToHistroyTestWithLargeData(){
        //데이터 저장
        List<Daily> dailyList = new ArrayList<>();
        for(int i = 0; i < 10000; i++) {
            dailyList.add(new Daily("www." + i +".com", 1, 1L));
        }
        dailyBulkRepository.saveAll(dailyList);

        //실행 시간 출력
        long start = System.currentTimeMillis();
        this.trackingService.updateTodayHitsToHistroy();
        long end = System.currentTimeMillis();
        System.out.println((double)(end-start)/1000 + "seconds");
    }

    @Test
    public void addHitsTestWithLargeTraffic() throws InterruptedException {
        //thread 생성
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(10);

        //조회수 증가 테스트
        this.dailyRepository.save(new Daily("www.google.com", 1, 1L));
        for(int i = 0; i < 10; i++){
            executorService.execute(()->{
                this.trackingService.addHits("www.google.com");
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        assertEquals(11, this.dailyRepository.findByUrl("www.google.com").get().getTodayHit());
    }
}
