package com.kits.tool.repository;

import com.kits.tool.entity.AppLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppLogRepository extends JpaRepository<AppLog, Long> {
}