package com.example.tracking;

import com.example.tracking.Entity.UrlRecord;
import com.example.tracking.Entity.UrlRecordHistory;
import com.example.tracking.Repository.UrlRecordRepository;
import com.example.tracking.Repository.UrlRecordHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"/truncate.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UrlRecordHistoryRepositoryTest {
    @Autowired
    private UrlRecordRepository urlRecordRepository;
    @Autowired
    private UrlRecordHistoryRepository urlRecordHistoryRepository;

    @Test
    public void findUrlRecordHistories(){
        //history 없는 경우
        urlRecordRepository.save(new UrlRecord("github.com", 0L));
        List<UrlRecordHistory> urlRecordHistories1 = urlRecordHistoryRepository.findUrlRecordHistories(
                "github.com",
                LocalDate.now().minusDays(6),
                LocalDate.now().minusDays(1)
        );
        assertEquals(0, urlRecordHistories1.size());

        //history 있는 경우
        UrlRecord urlRecord = new UrlRecord("www.naver.com", 40L);
        this.urlRecordRepository.save(urlRecord);
        for(int i = 1; i <= 4; i++){
            UrlRecordHistory urlRecordHistory = new UrlRecordHistory(urlRecord, LocalDate.now().minusDays(i), 10L);
            this.urlRecordHistoryRepository.save(urlRecordHistory);
        }
        List<UrlRecordHistory> urlRecordHistories2 = urlRecordHistoryRepository.findUrlRecordHistories(
                "www.naver.com",
                LocalDate.now().minusDays(6),
                LocalDate.now().minusDays(1)
        );
        assertEquals(4, urlRecordHistories2.size());
        assertEquals(10, urlRecordHistories2.get(2).getHit());

        // history 7개 이상
        for(int i = 5; i <= 10; i++){
            UrlRecordHistory urlRecordHistory = new UrlRecordHistory(urlRecord, LocalDate.now().minusDays(i), 5L);
            this.urlRecordHistoryRepository.save(urlRecordHistory);
        }
        List<UrlRecordHistory> urlRecordHistories3 = urlRecordHistoryRepository.findUrlRecordHistories(
                "www.naver.com",
                LocalDate.now().minusDays(6),
                LocalDate.now().minusDays(1)
        );
        assertEquals(6, urlRecordHistories3.size());
        assertEquals(5, urlRecordHistories3.get(4).getHit());

        //존재하지 않는 url
        List<UrlRecordHistory> urlRecordHistories4 = urlRecordHistoryRepository.findUrlRecordHistories(
                "google.com",
                LocalDate.now().minusDays(6),
                LocalDate.now().minusDays(1)
        );
        assertEquals(0, urlRecordHistories4.size());
    }
}
