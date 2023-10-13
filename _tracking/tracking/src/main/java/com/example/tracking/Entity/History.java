package com.example.tracking.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class History {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "url_id")
    private Daily daily;
    private LocalDate date;
    private Integer hit;

    @Builder
    public History(Daily daily, LocalDate date, Integer hit){
        this.daily = daily;
        this.date = date;
        this.hit = hit;
    }
}
