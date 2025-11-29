package com.kits.tool.repository;

import com.kits.tool.entity.CutoffSchedule;
import com.kits.tool.entity.PayrollClosingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayRollClosingRepository extends JpaRepository<PayrollClosingEntity, Integer> {


}
