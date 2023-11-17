package com.example.tracking.DTO;

import com.example.tracking.Entity.Daily;
import com.example.tracking.Entity.History;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data @Getter
public class HistoryDTO {
    private HistoryData[] historyData = new HistoryData[7];

    public HistoryDTO(Daily daily){
        List<History> historyList = daily.getHistory();
        Collections.sort(historyList, (x,y)->y.getDate().compareTo(x.getDate()));
        LocalDate today = LocalDate.now();
        this.historyData[0] = new HistoryData(today, daily.getTodayHit());
        for(int i = 1; i <= 6; i++){
            Integer hit = 0;
            if(historyList.size()>=i)
                hit = historyList.get(i-1).getHit();
            this.historyData[i] = new HistoryData(today.minusDays(i), hit);
        }
    }

    public Integer getHit(Integer idx){
        return historyData[idx].getHit();
    }
}

@Data @Getter
class HistoryData{
    private LocalDate date;
    private Integer hit;

    public HistoryData(LocalDate date, Integer hit){
        this.date = date;
        this.hit = hit;
    }
}
