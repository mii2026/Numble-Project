package com.example.tracking.Controller;

import com.example.tracking.DTO.HistoryDTO;
import com.example.tracking.DTO.HitsDTO;
import com.example.tracking.Service.TrackingHitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController @RequiredArgsConstructor
public class TrackingHitApplication {
    private final TrackingHitService trackingHitService;

    @PutMapping("/hits")
    public ResponseEntity<Object> addHits(@RequestParam String url){
        this.trackingHitService.addHits(url);

        Map<String, String> response = new HashMap<>(){};
        response.put("result", "success");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/hits")
    public ResponseEntity<Object> getHits(@RequestParam String url){
        HitsDTO info = this.trackingHitService.getHits(url);
        return ResponseEntity.ok().body(info);
    }

    @GetMapping("/history")
    public ResponseEntity<Object> getHistory(@RequestParam String url){
        HistoryDTO info = this.trackingHitService.getHistory(url);
        return ResponseEntity.ok().body(info);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void nextDay(){
        this.trackingHitService.nextDay();
    }
}
