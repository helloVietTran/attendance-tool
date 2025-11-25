package com.kits.tool.repository;

import com.kits.tool.entity.attendance_entity.DatabaseRowDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessLogRepository extends JpaRepository<DatabaseRowDTO, Integer> {
}
