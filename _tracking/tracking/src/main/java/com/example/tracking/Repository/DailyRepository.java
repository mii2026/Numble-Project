package com.example.tracking.Repository;

import com.example.tracking.Entity.Daily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DailyRepository extends JpaRepository<Daily, Integer> {
    public Optional<Daily> findByUrl(String url);

    @Query(value = "select d from Daily d left join fetch d.history where d.url=:url")
    public Optional<Daily> findByUrlWithHistory(@Param("url") String url);
}
