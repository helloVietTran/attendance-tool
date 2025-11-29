package com.kits.tool.dto;

public enum MailSubject {
    CHECK_IN_ERROR_MAIL("Thông báo lỗi Timesheet"),
    CUTOFF_MAIL("Thông báo chốt công");

    private final String subject;

    MailSubject(String subject) { this.subject = subject; }

    public String getSubject() { return subject; }
}
