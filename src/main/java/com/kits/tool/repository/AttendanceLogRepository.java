package com.kits.tool.repository;

import com.kits.tool.entity.attendance_entity.DatabaseExportDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceLogRepository extends JpaRepository<DatabaseExportDTO, Integer> {
}
