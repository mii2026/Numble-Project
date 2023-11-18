package com.example.tracking.Repository;

import com.example.tracking.Entity.Daily;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DailyRepository extends JpaRepository<Daily, Integer> {

    @Modifying @Transactional
    @Query(value = "INSERT INTO daily (url, today_hit, total_hit) VALUES (:url, 1, 1) " +
            "ON DUPLICATE KEY UPDATE today_hit=today_hit+1, total_hit=total_hit+1",
            nativeQuery = true)
    public void upsertHits(@Param("url") String url);

    @Modifying @Transactional
    @Query(value = "UPDATE daily SET today_hit=today_hit+1, total_hit=total_hit+1 WHERE url=:url",
            nativeQuery = true)
    public void updateHits(@Param("url") String ulr);

    public Optional<Daily> findByUrl(String url);

    @Query(value = "select d from Daily d left join fetch d.history where d.url=:url")
    public Optional<Daily> findByUrlWithHistory(@Param("url") String url);

    List<Daily> findAllBy(PageRequest pageRequest);
}
