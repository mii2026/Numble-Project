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
    @Column(unique = true)
    private String url;

    @OneToMany(mappedBy = "daily", fetch = FetchType.LAZY)
    private List<History> history = new ArrayList<>();

    @Builder
    public Daily(String url){
        this.url = url;
    }
}
