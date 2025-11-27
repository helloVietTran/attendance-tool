CREATE TABLE IF NOT EXISTS ForgotCheckoutEmailLog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employeeId INT NOT NULL,
    incidentDate DATE NOT NULL,
    emailSentCount INT DEFAULT 0,
    lastSentDate DATE,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_employee_incident (employeeId, incidentDate),
    INDEX idx_email_count (emailSentCount),
    INDEX idx_last_sent (lastSentDate)
);

