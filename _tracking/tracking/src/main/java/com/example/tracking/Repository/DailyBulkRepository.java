package com.example.tracking.Repository;

import com.example.tracking.Entity.Daily;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository @RequiredArgsConstructor
public class DailyBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Daily> dailyList){
        String sql = "INSERT INTO daily (url, today_hit, total_hit) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, dailyList.get(i).getUrl());
                ps.setString(2, dailyList.get(i).getTodayHit().toString());
                ps.setString(3, dailyList.get(i).getTotalHit().toString());
            }

            @Override
            public int getBatchSize() {
                return dailyList.size();
            }
        });
    }

    @Transactional
    public void updateAll(List<Daily> dailyList){
        String sql = "UPDATE daily SET today_hit=? WHERE url_id=?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, dailyList.get(i).getTodayHit().toString());
                ps.setString(2, dailyList.get(i).getUrlId().toString());
            }

            @Override
            public int getBatchSize() {
                return dailyList.size();
            }
        });

    }
}
