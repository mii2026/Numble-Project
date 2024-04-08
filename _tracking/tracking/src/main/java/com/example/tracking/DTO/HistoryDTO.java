package com.example.tracking.DTO;

import com.example.tracking.Entity.UrlRecordHistory;
import lombok.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data @Getter
public class HistoryDTO {
    private HistoryData[] historyData = new HistoryData[7];

    @Getter @Setter @AllArgsConstructor
    private class HistoryData{
        private LocalDate date;
        private Long hit;
    }

    @Builder
    public HistoryDTO(Long todayHit, List<UrlRecordHistory> urlRecordHistories){
        LocalDate today = LocalDate.now();
        this.historyData[0] = new HistoryData(today, todayHit);

        Collections.sort(urlRecordHistories, (x,y)->y.getDate().compareTo(x.getDate()));
        for(int i = 1; i <= 6; i++){
            Long hit = 0L;
            if(urlRecordHistories.size()>=i)
                hit = urlRecordHistories.get(i-1).getHit();
            this.historyData[i] = new HistoryData(today.minusDays(i), hit);
        }
    }

    public Long getHit(Integer idx){
        return historyData[idx].getHit();
    }
}

