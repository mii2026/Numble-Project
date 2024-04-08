package com.example.tracking.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UrlRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer urlId;
    @Column(unique = true)
    private String url;
    private Long totalHit;

    @OneToMany(mappedBy = "urlRecord", fetch = FetchType.LAZY)
    private List<UrlRecordHistory> urlRecordHistory;

    @Builder
    public UrlRecord(String url, Long totalHit){
        this.url = url;
        this.totalHit = totalHit;
    }

    public void increaseTotalHit(Long todayHit){
        this.totalHit += todayHit;
    }
}
