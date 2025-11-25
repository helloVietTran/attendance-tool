package com.kits.tool.repository;

import com.kits.tool.entity.payrollclosing_entity.PayrollClosingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRollClosingRepository extends JpaRepository<PayrollClosingEntity, Integer> {
}
