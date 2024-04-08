package com.example.tracking.Repository;

import com.example.tracking.Entity.UrlRecordHistory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository @RequiredArgsConstructor
public class UrlRecordHistoryBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<UrlRecordHistory> urlRecordHistoryList){
        String sql = "INSERT INTO url_record_history (url_id, hit, date) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, urlRecordHistoryList.get(i).getUrlRecord().getUrlId().toString());
                ps.setString(2, urlRecordHistoryList.get(i).getHit().toString());
                ps.setString(3, urlRecordHistoryList.get(i).getDate().toString());
            }

            @Override
            public int getBatchSize() {
                return urlRecordHistoryList.size();
            }
        });
    }
}
