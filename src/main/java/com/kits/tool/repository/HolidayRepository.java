package com.kits.tool.repository;

import com.kits.tool.entity.HolidayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface HolidayRepository extends JpaRepository<HolidayEntity, Integer> {
    boolean existsByHolidayDate(LocalDate holidayDate);
}

