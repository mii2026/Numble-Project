package com.example.tracking.Repository;

import com.example.tracking.Entity.UrlRecord;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRecordRepository extends JpaRepository<UrlRecord, Integer> {
    Optional<UrlRecord> findByUrl(String url);

    List<UrlRecord> findAllBy(PageRequest pageRequest);
}
