package com.kits.tool.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MailLogEntry {
    private String recipient;
    private String subject;
    private LocalDateTime sentTime;
    private String status;
    private String errorMessage;
}
