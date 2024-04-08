package com.example.tracking.DTO;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data @Getter
public class HitsDTO {
    private Long todayHit;
    private Long totalHit;

    @Builder
    public HitsDTO(Long todayHit, Long totalHit){
        this.todayHit = todayHit;
        this.totalHit = totalHit;
    }
}
