package com.example.tracking.DTO;
import com.example.tracking.Entity.Daily;
import lombok.Data;
import lombok.Getter;

@Data @Getter
public class HitsDTO {
    private Integer todayHit;
    private Long totalHit;

    public HitsDTO(Daily d){
        this.todayHit = d.getTodayHit();
        this.totalHit = d.getTotalHit();
    }
}
