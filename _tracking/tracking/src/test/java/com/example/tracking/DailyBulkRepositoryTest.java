package com.example.tracking;

import com.example.tracking.Entity.Daily;
import com.example.tracking.Repository.DailyBulkRepository;
import com.example.tracking.Repository.DailyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DailyBulkRepositoryTest {
    @Autowired
    private DailyBulkRepository dailyBulkRepository;
    @Autowired
    private DailyRepository dailyRepository;

    @Test
    public void bulkInsertTest(){
        //데이터 저장
        List<Daily> dailyList = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            dailyList.add(new Daily("www." + i +".com", 1, 1L));
        }
        this.dailyBulkRepository.saveAll(dailyList);

        //개수 및 값 확인
        List<Daily> dailyList2 = this.dailyRepository.findAll();
        assertEquals(100, dailyList2.size());
        assertEquals(1, dailyList2.get(0).getTodayHit());
    }

    @Test
    public void bulkUpdateTest(){
        //데이터 저장
        List<Daily> dailyList = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            dailyList.add(new Daily("www." + i +".com", 1, 1L));
        }
        this.dailyBulkRepository.saveAll(dailyList);

        //데이터 값 업데이트
        List<Daily> dailyList2 = this.dailyRepository.findAll();
        for(Daily daily: dailyList2)
            daily.setTodayHit(0);
        this.dailyBulkRepository.updateAll(dailyList2);

        //개수 및 값 확인
        List<Daily> dailyList3 = this.dailyRepository.findAll();
        assertEquals(100, dailyList3.size());
        assertEquals(0, dailyList3.get(0).getTodayHit());
    }
}
