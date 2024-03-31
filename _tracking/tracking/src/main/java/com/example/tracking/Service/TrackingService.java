package com.example.tracking.Service;

import com.example.tracking.DTO.HistoryDTO;
import com.example.tracking.DTO.HitsDTO;
import com.example.tracking.Entity.Daily;
import com.example.tracking.Entity.History;
import com.example.tracking.Repository.DailyRepository;
import com.example.tracking.Repository.HistoryBulkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor @Slf4j
public class TrackingService {
    private final DailyRepository dailyRepository;
    private final HistoryBulkRepository historyBulkRepository;
    private final StringRedisTemplate redisTemplate;

    public void addHits(String url){
        redisTemplate.opsForValue().increment("url:"+url+":todayHit");
        Long totalHit = redisTemplate.opsForValue().increment("url:"+url+":totalHit");
        if(totalHit == 1L){
            dailyRepository.save(new Daily(url));
        }
    }

    public HitsDTO getHits(String url){

        String todayHit = redisTemplate.opsForValue().get("url:"+url+":todayHit");
        String totalHit = redisTemplate.opsForValue().get("url:"+url+":totalHit");

        return HitsDTO.builder()
                .todayHit(todayHit == null ? 0L : Long.valueOf(todayHit))
                .totalHit(totalHit == null ? 0L : Long.valueOf(totalHit))
                .build();
    }

    public HistoryDTO getHistory(String url){

        String todayHit = redisTemplate.opsForValue().get("url:"+url+":todayHit");
        Optional<Daily> daily = dailyRepository.findByUrlWithHistory(url);

        return HistoryDTO.builder()
                .todayHit(todayHit==null ? 0L : Long.valueOf(todayHit))
                .historyList(daily.isEmpty() ? new ArrayList<History>() : daily.get().getHistory())
                .build();
    }

    @Transactional
    public void updateTodayHitsToHistroy(){
        for(int i = 0; i < 100; i++) {
            List<Daily> dailyList = this.dailyRepository.findAllBy(PageRequest.of(i, 10000));

            if(dailyList.size()==0)
                break;

            List<String> keys = new ArrayList<>();
            Map<String, String> todayHitUpdateList = new HashMap<>();
            for(Daily d: dailyList){
                keys.add("url:"+d.getUrl()+":todayHit");
                todayHitUpdateList.put("url:"+d.getUrl()+":todayHit", "0");
            }
            List<String> values = redisTemplate.opsForValue().multiGet(keys);

            List<History> historyList = new ArrayList<>();
            for (int j = 0; j < dailyList.size(); j++) {
                String todayHit = values.get(j)==null ? "0" : values.get(i);
                historyList.add(
                        new History(
                                dailyList.get(i),
                                LocalDate.now().minusDays(1),
                                Long.valueOf(todayHit)
                        )
                );
            }

            this.historyBulkRepository.saveAll(historyList);
            redisTemplate.opsForValue().multiSet(todayHitUpdateList);
        }
        if(this.dailyRepository.findAllBy(PageRequest.of(100, 10000)).size()!=0)
            log.warn("Warning at updateTodayHitsToHistroy():Increase limit of page!");
    }
}
