package com.example.tracking.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Daily {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer urlId;
    private String url;
    private Integer todayHit;
    private Long totalHit;

    @OneToMany(mappedBy = "daily", fetch = FetchType.LAZY)
    private List<History> history = new ArrayList<>();

    @Builder
    public Daily(String url, Integer todayHit, Long totalHit){
        this.url = url;
        this.todayHit = todayHit;
        this.totalHit = totalHit;
    }
}
