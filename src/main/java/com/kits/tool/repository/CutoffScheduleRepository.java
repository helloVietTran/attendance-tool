package com.kits.tool.repository;

import com.kits.tool.entity.CutoffSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CutoffScheduleRepository extends JpaRepository<CutoffSchedule, Long> {
    Optional<CutoffSchedule> findByMonthAndYear(Integer month, Integer year);
}
