package com.example.tracking.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service @RequiredArgsConstructor @Slf4j
public class DailyLockFacade {
    private final TrackingService trackingService;
    private final RedissonClient redissonClient;

    public void addHits(String url){
        RLock rLock = redissonClient.getLock("addHits:url:"+url);
        try{
            boolean available = rLock.tryLock(10, 1, TimeUnit.SECONDS);
            if(!available){
                log.error("getLock timeout");
                throw new IllegalArgumentException();
            }
            trackingService.addHits(url);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }finally {
            rLock.unlock();
        }
    }
}
