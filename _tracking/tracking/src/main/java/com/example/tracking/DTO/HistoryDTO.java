package com.example.tracking.DTO;

import com.example.tracking.Entity.Daily;
import com.example.tracking.Entity.History;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data @Getter
public class HistoryDTO {
    private HistoryData[] historyData = new HistoryData[7];

    @Builder
    public HistoryDTO(Long todayHit, List<History> historyList){
        LocalDate today = LocalDate.now();
        this.historyData[0] = new HistoryData(today, todayHit);

        Collections.sort(historyList, (x,y)->y.getDate().compareTo(x.getDate()));
        for(int i = 1; i <= 6; i++){
            Long hit = 0L;
            if(historyList.size()>=i)
                hit = historyList.get(i-1).getHit();
            this.historyData[i] = new HistoryData(today.minusDays(i), hit);
        }
    }

    public Long getHit(Integer idx){
        return historyData[idx].getHit();
    }
}

@Data @Getter
class HistoryData{
    private LocalDate date;
    private Long hit;

    public HistoryData(LocalDate date, Long hit){
        this.date = date;
        this.hit = hit;
    }
}
