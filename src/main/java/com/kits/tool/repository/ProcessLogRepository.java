package com.kits.tool.repository;

import com.kits.tool.entity.ProcessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessLogRepository extends JpaRepository<ProcessLog, Integer> {
}
