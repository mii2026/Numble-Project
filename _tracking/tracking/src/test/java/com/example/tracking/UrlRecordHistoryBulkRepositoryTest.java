package com.example.tracking;

import com.example.tracking.Entity.UrlRecord;
import com.example.tracking.Entity.UrlRecordHistory;
import com.example.tracking.Repository.UrlRecordRepository;
import com.example.tracking.Repository.UrlRecordHistoryBulkRepository;
import com.example.tracking.Repository.UrlRecordHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UrlRecordHistoryBulkRepositoryTest {
    @Autowired
    private UrlRecordRepository urlRecordRepository;
    @Autowired
    private UrlRecordHistoryRepository urlRecordHistoryRepository;
    @Autowired
    private UrlRecordHistoryBulkRepository urlRecordHistoryBulkRepository;

    @Test
    public void bulkInsertTest(){
        //데일리 데이터 저장
        List<UrlRecord> urlRecordList1 = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            urlRecordList1.add(new UrlRecord("www." + i +".com", 0L));
        }
        this.urlRecordRepository.saveAll(urlRecordList1);

        //저장된 데일리 데이터 불러오기
        List<UrlRecord> urlRecordList2 = this.urlRecordRepository.findAll();

        //데일리 데이터마다 하나의 히스토리 데이터 생성 및 저장
        List<UrlRecordHistory> urlRecordHistoryList1 = new ArrayList<>();
        for(UrlRecord urlRecord : urlRecordList2)
            urlRecordHistoryList1.add(new UrlRecordHistory(urlRecord, LocalDate.now(), 1L));
        this.urlRecordHistoryBulkRepository.saveAll(urlRecordHistoryList1);

        //히스토리 데이터 개수 및 값 확인
        List<UrlRecordHistory> urlRecordHistoryList2 = this.urlRecordHistoryRepository.findAll();
        assertEquals(100, urlRecordHistoryList2.size());
        assertEquals(1, urlRecordHistoryList2.get(0).getHit());
    }
}
