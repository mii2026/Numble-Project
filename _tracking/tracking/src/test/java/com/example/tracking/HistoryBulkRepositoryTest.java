package com.example.tracking;

import com.example.tracking.Entity.Daily;
import com.example.tracking.Entity.History;
import com.example.tracking.Repository.DailyBulkRepository;
import com.example.tracking.Repository.DailyRepository;
import com.example.tracking.Repository.HistoryBulkRepository;
import com.example.tracking.Repository.HistoryRepository;
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
public class HistoryBulkRepositoryTest {
    @Autowired
    private DailyRepository dailyRepository;
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private DailyBulkRepository dailyBulkRepository;
    @Autowired
    private HistoryBulkRepository historyBulkRepository;

    @Test
    public void bulkInsertTest(){
        //데일리 데이터 저장
        List<Daily> dlist = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            dlist.add(new Daily("www." + i +".com", 1, 2L));
        }
        this.dailyBulkRepository.saveAll(dlist);

        //저장된 데일리 데이터 불러오기
        List<Daily> dlist2 = this.dailyRepository.findAll();

        //데일리 데이터마다 하나의 히스토리 데이터 생성 및 저장
        List<History> hlist = new ArrayList<>();
        for(Daily d: dlist2)
            hlist.add(new History(d, LocalDate.now(), 1));
        this.historyBulkRepository.saveAll(hlist);

        //히스토리 데이터 개수 및 값 확인
        List<History> hlist2 = this.historyRepository.findAll();
        assertEquals(100, hlist2.size());
        assertEquals(1, hlist2.get(0).getHit());
    }
}
