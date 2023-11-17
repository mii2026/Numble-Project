package com.example.tracking;

import com.example.tracking.Entity.Daily;
import com.example.tracking.Repository.DailyBulkRepository;
import com.example.tracking.Repository.DailyRepository;
import com.example.tracking.Repository.HistoryBulkRepository;
import com.example.tracking.Repository.HistoryRepository;
import com.example.tracking.Service.DailyLockFacade;
import com.example.tracking.Service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DailyLockFacadeTest {
    @Autowired
    private DailyRepository dailyRepository;
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private DailyBulkRepository dailyBulkRepository;
    @Autowired
    private HistoryBulkRepository historyBulkRepository;
    @Autowired
    private RedissonClient redissonClient;
    private TrackingService trackingService;
    private DailyLockFacade dailyLockFacade;


    @BeforeEach
    public void beforeEach(){
        this.trackingService = new TrackingService(this.dailyRepository, this.dailyBulkRepository, this.historyBulkRepository);
        this.dailyLockFacade = new DailyLockFacade(this.trackingService, this.redissonClient);
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
                this.dailyLockFacade.addHits("www.google.com");
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        assertEquals(11, this.dailyRepository.findByUrl("www.google.com").get().getTodayHit());
    }
}
