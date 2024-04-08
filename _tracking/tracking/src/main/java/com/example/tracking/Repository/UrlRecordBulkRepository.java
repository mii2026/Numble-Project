package com.example.tracking.Repository;

import com.example.tracking.Entity.UrlRecord;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UrlRecordBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void updateAll(List<UrlRecord> dailyList){
        String sql = "UPDATE url_record SET total_hit=? WHERE url_id=?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, dailyList.get(i).getTotalHit().toString());
                ps.setString(2, dailyList.get(i).getUrlId().toString());
            }

            @Override
            public int getBatchSize() {
                return dailyList.size();
            }
        });

    }
}
