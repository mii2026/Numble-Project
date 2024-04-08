package com.example.tracking.Service;

import com.example.tracking.DTO.HistoryDTO;
import com.example.tracking.DTO.HitsDTO;
import com.example.tracking.Entity.UrlRecord;
import com.example.tracking.Entity.UrlRecordHistory;
import com.example.tracking.Repository.UrlRecordBulkRepository;
import com.example.tracking.Repository.UrlRecordHistoryRepository;
import com.example.tracking.Repository.UrlRecordRepository;
import com.example.tracking.Repository.UrlRecordHistoryBulkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor
public class TrackingService {
    private final UrlRecordRepository urlRecordRepository;
    private final UrlRecordHistoryRepository urlRecordHistoryRepository;
    private final UrlRecordBulkRepository urlRecordBulkRepository;
    private final UrlRecordHistoryBulkRepository urlRecordHistoryBulkRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void addHits(String url){
        redisTemplate.opsForValue().increment("url:"+url+":todayHit");
        if(redisTemplate.opsForSet().add("urlList", url)==1)
            urlRecordRepository.save(new UrlRecord(url, 0L));
    }

    public HitsDTO getHits(String url){
        Long todayHit = Long.valueOf(
                Optional.ofNullable(redisTemplate.opsForValue().get("url:"+url+":todayHit")).orElse("0")
        );

        Long totalHit = urlRecordRepository.findByUrl(url).orElse(new UrlRecord(url, 0L)).getTotalHit();

        return HitsDTO.builder()
                .todayHit(todayHit)
                .totalHit(totalHit + todayHit)
                .build();
    }

    public HistoryDTO getHistory(String url){
        Long todayHit = Long.valueOf(
                Optional.ofNullable(redisTemplate.opsForValue().get("url:"+url+":todayHit")).orElse("0")
        );

        List<UrlRecordHistory> urlRecordHistories = urlRecordHistoryRepository.
                findUrlRecordHistories(url, LocalDate.now().minusDays(6), LocalDate.now().minusDays(1));

        return HistoryDTO.builder()
                .todayHit(todayHit)
                .urlRecordHistories(urlRecordHistories)
                .build();
    }

    @Transactional
    public void updateTodayHitsToHistroy(){
        int pageSize = 10000;
        LocalDate today = LocalDate.now().minusDays(1);
        long urlLength = urlRecordRepository.count();
        for(int i = 0; i < urlLength/pageSize+1; i++) {
            List<UrlRecord> urlRecords = urlRecordRepository.findAllBy(PageRequest.of(i, pageSize));
            List<UrlRecordHistory> urlRecordHistories = new ArrayList<>();
            for (UrlRecord urlRecord: urlRecords) {
                Long todayHit = Long.valueOf(
                        Optional.ofNullable(redisTemplate.opsForValue().getAndDelete("url:"+urlRecord.getUrl()+":todayHit"))
                                .orElse("0")
                );
                urlRecord.increaseTotalHit(todayHit);
                urlRecordHistories.add(new UrlRecordHistory(urlRecord, today, todayHit));
            }
            urlRecordBulkRepository.updateAll(urlRecords);
            urlRecordHistoryBulkRepository.saveAll(urlRecordHistories);
        }
    }
}
