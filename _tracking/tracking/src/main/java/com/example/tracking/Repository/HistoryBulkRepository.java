package com.example.tracking.Repository;

import com.example.tracking.Entity.History;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository @RequiredArgsConstructor
public class HistoryBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<History> historyList){
        String sql = "INSERT INTO history (url_id, hit, date) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, historyList.get(i).getDaily().getUrlId().toString());
                ps.setString(2, historyList.get(i).getHit().toString());
                ps.setString(3, historyList.get(i).getDate().toString());
            }

            @Override
            public int getBatchSize() {
                return historyList.size();
            }
        });
    }
}
