package com.example.tracking.Controller;

import com.example.tracking.DTO.HistoryDTO;
import com.example.tracking.DTO.HitsDTO;
import com.example.tracking.Service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
public class TrackingController {
    private final TrackingService trackingService;

    @Operation(summary = "url 조회수 증가", description = "해당 url이 존재하면 일일 조회수와 총 조회수를 증가하고, 아니면 url 조회수 정보를 생성합니다.")
    @PutMapping("/hits")
    public ResponseEntity<Object> addHits(@Parameter(name = "url", description = "url을 작성합니다.(':'는 '%3A', '/'는 '%2F'로 작성)")
                                              @RequestParam String url){
        this.trackingService.addHits(url);

        Map<String, String> response = new HashMap<>(){};
        response.put("result", "success");
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "url 조회수 조회", description = "해당 url의 일일 조회수와 총 조회수를 조회합니다.")
    @GetMapping("/hits")
    public ResponseEntity<Object> getHits(@Parameter(name = "url", description = "url을 작성합니다.(':'는 '%3A', '/'는 '%2F'로 작성)")
                                              @RequestParam String url){
        HitsDTO info = this.trackingService.getHits(url);
        return ResponseEntity.ok().body(info);
    }

    @Operation(summary = "url 히스토리 조회", description = "해당 url의 7일간의 조회수를 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<Object> getHistory(@Parameter(name = "url", description = "url을 작성합니다.(':'는 '%3A', '/'는 '%2F'로 작성)")
                                                 @RequestParam String url){
        HistoryDTO info = this.trackingService.getHistory(url);
        return ResponseEntity.ok().body(info);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void nextDay(){
        this.trackingService.nextDay();
    }
}
