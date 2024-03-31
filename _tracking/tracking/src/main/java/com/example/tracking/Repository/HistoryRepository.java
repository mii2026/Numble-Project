package com.example.tracking.Repository;

import com.example.tracking.Entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
