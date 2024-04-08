package com.example.tracking.Repository;

import com.example.tracking.Entity.UrlRecordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UrlRecordHistoryRepository extends JpaRepository<UrlRecordHistory, Long> {
    @Query(value = "select u " +
            "from UrlRecordHistory u " +
            "where u.urlRecord.url=:url " +
            "and u.date between :startDate and :endDate"
    )
    List<UrlRecordHistory> findUrlRecordHistories(
            @Param("url") String url,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
