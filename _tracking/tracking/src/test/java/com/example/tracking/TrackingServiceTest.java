package com.example.tracking;

import com.example.tracking.DTO.HistoryDTO;
import com.example.tracking.DTO.HitsDTO;
import com.example.tracking.Entity.UrlRecord;
import com.example.tracking.Entity.UrlRecordHistory;
import com.example.tracking.Repository.UrlRecordBulkRepository;
import com.example.tracking.Repository.UrlRecordRepository;
import com.example.tracking.Repository.UrlRecordHistoryBulkRepository;
import com.example.tracking.Repository.UrlRecordHistoryRepository;
import com.example.tracking.Service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TrackingServiceTest {
    @Autowired
    private UrlRecordRepository urlRecordRepository;
    @Autowired
    private UrlRecordHistoryRepository urlRecordHistoryRepository;
    @Autowired
    private UrlRecordBulkRepository urlRecordBulkRepository;
    @Autowired
    private UrlRecordHistoryBulkRepository urlRecordHistoryBulkRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private TrackingService trackingService;

    @BeforeEach
    public void beforeEach(){
        this.trackingService = new TrackingService(
                this.urlRecordRepository,
                this.urlRecordHistoryRepository,
                this.urlRecordBulkRepository,
                this.urlRecordHistoryBulkRepository,
                this.redisTemplate
        );
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    public void addHitsTest(){
        //최초 생성, 오늘 조회수와 모두 1
        this.trackingService.addHits("www.naver.com");
        assertEquals("1", redisTemplate.opsForValue().get("url:www.naver.com:todayHit"));

        //존재하는 url hit 증가, 오늘 조회수 1 증가
        this.trackingService.addHits("www.naver.com");
        assertEquals("2", redisTemplate.opsForValue().get("url:www.naver.com:todayHit"));
    }

    @Test
    public void getHitsTest(){
        //데이터에 존재하는 url, 가진 값 출력
        redisTemplate.opsForValue().increment("url:www.naver.com:todayHit", 6);
        HitsDTO hits1 = this.trackingService.getHits("www.naver.com");
        assertEquals(6L, hits1.getTodayHit());
        assertEquals(6L, hits1.getTotalHit());

        //데이터에 존재하지 않는 url, 0으로 출력
        HitsDTO hits2 = this.trackingService.getHits("www.google.com");
        assertEquals(0L, hits2.getTodayHit());
        assertEquals(0L, hits2.getTotalHit());
    }

    @Test
    public void getHistoryTest(){
        //데이터에 존재하지 않는 url, hit 전부 0으로 나오는지 확인
        HistoryDTO hits1 = this.trackingService.getHistory("www.google.com");
        for(int i = 0; i < 7; i++)
            assertEquals(0L, hits1.getHit(i));

        //데이터에 존재하는 url, history가 7일 이하로 있는 경우, history 없는 날짜에는 hit 0인지 확인
        redisTemplate.opsForValue().increment("url:www.google.com:todayHit", 6);
        UrlRecord urlRecord = new UrlRecord("www.google.com", 24L);
        LocalDate today = LocalDate.now();
        this.urlRecordRepository.save(urlRecord);
        for(int i = 1; i < 4; i++)
            this.urlRecordHistoryRepository.save(new UrlRecordHistory(urlRecord, today.minusDays(i), 8L));

        HistoryDTO hits2 = this.trackingService.getHistory("www.google.com");
        assertEquals(6L, hits2.getHit(0));
        for(int i = 1; i < 4; i++)
            assertEquals(8L, hits2.getHit(i));
        for(int i = 4; i < 7 ; i++)
            assertEquals(0L, hits2.getHit(i));

        //데이터에 존재하는 url, history가 7일 이상 있는 경우
        redisTemplate.opsForValue().set("url:www.google.com:totalHit", "44");
        for(int i = 4; i < 7; i++)
            this.urlRecordHistoryRepository.save(new UrlRecordHistory(urlRecord, today.minusDays(i), 4L));

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
        redisTemplate.opsForValue().set("url:www.google.com:todayHit", "6");
        UrlRecord urlRecord = new UrlRecord("www.google.com", 24L);
        this.urlRecordRepository.save(urlRecord);
        LocalDate today = LocalDate.now();
        for(int i = 1; i < 3; i++)
            this.urlRecordHistoryRepository.save(new UrlRecordHistory(urlRecord, today.minusDays(i+1), 8L));

        //다음날에 오늘 조회수 0, 히스토리 변화 확인
        this.trackingService.updateTodayHitsToHistroy();

        assertNull(redisTemplate.opsForValue().get("url:www.google.com:todayHit"));
        List<UrlRecordHistory> urlRecordHistories = urlRecordHistoryRepository.findUrlRecordHistories(
                "www.google.com",
                LocalDate.now().minusDays(6),
                LocalDate.now().minusDays(1)
        );
        assertEquals(3, urlRecordHistories.size());
        assertEquals(6L, urlRecordHistories.get(2).getHit());
    }

    public void updateTodayHitsToHistroyTestWithLargeData(){
        //데이터 저장
        List<UrlRecord> urlRecordList = new ArrayList<>();
        for(int i = 0; i < 10000; i++) {
            urlRecordList.add(new UrlRecord("www." + i +".com", 0L));
            redisTemplate.opsForValue().set("url:www."+i+".com:todayHit", "1");
        }
        urlRecordRepository.saveAll(urlRecordList);

        //실행 시간 출력
        long start = System.currentTimeMillis();
        this.trackingService.updateTodayHitsToHistroy();
        long end = System.currentTimeMillis();
        System.out.println((double)(end-start)/1000 + "seconds");
        assertEquals(10000, urlRecordHistoryRepository.count());
    }

    public void addHitsTestWithLargeTraffic() throws InterruptedException {
        //thread 생성
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(100);

        for(int i = 0; i < 100; i++){
            executorService.execute(()->{
                this.trackingService.addHits("www.google.com");
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        assertEquals("100", redisTemplate.opsForValue().get("url:www.google.com:todayHit"));
    }
}
