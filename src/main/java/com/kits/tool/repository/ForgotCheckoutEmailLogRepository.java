package com.kits.tool.repository;

import com.kits.tool.entity.ForgotCheckoutEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForgotCheckoutEmailLogRepository extends JpaRepository<ForgotCheckoutEmailLog, Integer> {
    
    Optional<ForgotCheckoutEmailLog> findByEmployeeIdAndIncidentDate(int employeeId, LocalDate incidentDate);
    
    List<ForgotCheckoutEmailLog> findByEmailSentCountLessThan(int maxCount);
}

