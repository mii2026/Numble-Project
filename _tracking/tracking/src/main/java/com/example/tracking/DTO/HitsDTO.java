package com.example.tracking.DTO;
import com.example.tracking.Entity.Daily;
import lombok.Data;
import lombok.Getter;

@Data @Getter
public class HitsDTO {
    private Integer todayHit;
    private Long totalHit;

    public HitsDTO(Daily daily){
        this.todayHit = daily.getTodayHit();
        this.totalHit = daily.getTotalHit();
    }
}
