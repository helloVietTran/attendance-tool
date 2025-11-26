package com.kits.tool.entity.compkey;

import java.io.Serializable;
import java.time.LocalDate;

public class AttendanceLogCompositeKey implements Serializable {
    private int empId;
    private LocalDate processDate;
}
