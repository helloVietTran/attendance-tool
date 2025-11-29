package com.kits.tool.entity.compkey;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class AttendanceLogCompositeKey implements Serializable {
    private int empId;
    private LocalDate processDate;

    public AttendanceLogCompositeKey() {}

    public AttendanceLogCompositeKey(int employeeId, LocalDate date) {
        this.empId = employeeId;
        this.processDate = date;
    }

    // getters, setters

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttendanceLogCompositeKey)) return false;
        AttendanceLogCompositeKey that = (AttendanceLogCompositeKey) o;
        return Objects.equals(empId, that.empId) &&
                Objects.equals(processDate, that.processDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId, processDate);
    }
}
