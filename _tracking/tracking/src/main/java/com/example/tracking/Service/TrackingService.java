package com.example.tracking.Service;

import com.example.tracking.DTO.HistoryDTO;
import com.example.tracking.DTO.HitsDTO;
import com.example.tracking.Entity.Daily;
import com.example.tracking.Entity.History;
import com.example.tracking.Repository.DailyBulkRepository;
import com.example.tracking.Repository.DailyRepository;
import com.example.tracking.Repository.HistoryBulkRepository;
import com.example.tracking.Repository.HistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class TrackingService {
    private final DailyRepository dailyRepository;
    private final HistoryRepository historyRepository;
    private final DailyBulkRepository dailyBulkRepository;
    private final HistoryBulkRepository historyBulkRepository;

    public void addHits(String url){
        //url 데이터 불러오기
        Optional<Daily> od = this.dailyRepository.findByUrl(url);

        Daily d;
        if(od.isEmpty()){
            //데이터가 없는 경우 생성
            d = new Daily(url, 1, 1L);
        }else{
            //데이터가 있는 경우 1씩 증가
            d = od.get();
            d.setTodayHit(d.getTodayHit()+1);
            d.setTotalHit(d.getTotalHit()+1);
        }
        //데이터 저장
        this.dailyRepository.save(d);
    }

    public HitsDTO getHits(String url){
        //데이터 불러오기
        Optional<Daily> od = this.dailyRepository.findByUrl(url);
        if(od.isEmpty()) {
            //데이터가 없는 경우 조회수는 모두 0
            return new HitsDTO(new Daily(url, 0, 0L));
        }else{
            //데이터가 있는 경우 조회수 반환
            return new HitsDTO(od.get());
        }
    }

    public HistoryDTO getHistory(String url){
        //데이터를 히스토리와 함께 불러오기
        Optional<Daily> od = this.dailyRepository.findByUrlWithHistory(url);
        if(od.isEmpty()){
            //데이터가 없는 경우 조회수와 히스토리 모두 0 출력
            return new HistoryDTO(new Daily(url, 0, 0L));
        }else{
            //데이터가 있는 경우 데이터 출력
            return new HistoryDTO(od.get());
        }
    }

    @Transactional
    public void nextDay(){
        int i = 0;
        while(true) {
            //쪼개진 오늘의 데이터 불러오기
            List<Daily> dlist = this.dailyRepository.findAllBy(PageRequest.of(i, 10000));

            //페이지가 비었으면 모두 확인한 것으로 종료
            if(dlist.size()==0)
                break;

            //오늘의 데이터를 바탕으로 히스토리 데이터 생성, 오늘 데이터의 오늘 조회수를 0으로 변경
            List<History> hlist = new ArrayList<>();
            for (Daily d : dlist) {
                hlist.add(new History(d, LocalDate.now().minusDays(1), d.getTodayHit()));
                d.setTodayHit(0);
            }

            //생성한 히스토리와 변경한 오늘의 데이터 저장
            this.historyBulkRepository.saveAll(hlist);
            this.dailyBulkRepository.updateAll(dlist);

            //페이지 인덱스 증가
            i++;
        }
    }
}
