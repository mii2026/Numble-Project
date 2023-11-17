package com.example.tracking.Service;

import com.example.tracking.DTO.HistoryDTO;
import com.example.tracking.DTO.HitsDTO;
import com.example.tracking.Entity.Daily;
import com.example.tracking.Entity.History;
import com.example.tracking.Repository.DailyBulkRepository;
import com.example.tracking.Repository.DailyRepository;
import com.example.tracking.Repository.HistoryBulkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor @Slf4j
public class TrackingService {
    private final DailyRepository dailyRepository;
    private final DailyBulkRepository dailyBulkRepository;
    private final HistoryBulkRepository historyBulkRepository;

    @Transactional
    public void addHits(String url){
        //url 데이터 불러오기
        Optional<Daily> optionalDaily = this.dailyRepository.findByUrl(url);

        Daily daily;
        if(optionalDaily.isEmpty()){
            //데이터가 없는 경우 생성
            daily = new Daily(url, 1, 1L);
        }else{
            //데이터가 있는 경우 1씩 증가
            daily = optionalDaily.get();
            daily.setTodayHit(daily.getTodayHit()+1);
            daily.setTotalHit(daily.getTotalHit()+1);
        }
        //데이터 저장
        this.dailyRepository.save(daily);
    }

    public HitsDTO getHits(String url){
        //데이터 불러오기
        Optional<Daily> optionalDaily = this.dailyRepository.findByUrl(url);
        if(optionalDaily.isEmpty()) {
            //데이터가 없는 경우 조회수는 모두 0
            return new HitsDTO(new Daily(url, 0, 0L));
        }
        //데이터가 있는 경우 조회수 반환
        return new HitsDTO(optionalDaily.get());
    }

    public HistoryDTO getHistory(String url){
        //데이터를 히스토리와 함께 불러오기
        Optional<Daily> optionalDaily = this.dailyRepository.findByUrlWithHistory(url);
        if(optionalDaily.isEmpty()){
            //데이터가 없는 경우 조회수와 히스토리 모두 0 출력
            return new HistoryDTO(new Daily(url, 0, 0L));
        }
        //데이터가 있는 경우 데이터 출력
        return new HistoryDTO(optionalDaily.get());
    }

    @Transactional
    public void updateTodayHitsToHistroy(){
        // 10000개씩 100번 가져오고 아직 남았다면 로그로 프린트처리
        for(int i = 0; i < 100; i++) {
            //오늘의 조회수 데이터 10000개씩 불러오기
            List<Daily> dailyList = this.dailyRepository.findAllBy(PageRequest.of(i, 10000));

            //페이지가 비었으면 모두 확인한 것으로 종료
            if(dailyList.size()==0)
                break;

            //오늘의 데이터를 바탕으로 히스토리 데이터 생성, 오늘 데이터의 오늘 조회수를 0으로 변경
            List<History> historyList = new ArrayList<>();
            for (Daily d : dailyList) {
                historyList.add(new History(d, LocalDate.now().minusDays(1), d.getTodayHit()));
                d.setTodayHit(0);
            }

            //생성한 히스토리와 변경한 오늘의 데이터 저장
            this.historyBulkRepository.saveAll(historyList);
            this.dailyBulkRepository.updateAll(dailyList);
        }
        if(this.dailyRepository.findAllBy(PageRequest.of(0, 10000)).size()!=0)
            log.warn("Warning at updateTodayHitsToHistroy():Increase limit of page!");
    }
}
