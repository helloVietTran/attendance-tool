### Code mẫu dùng hàm send Mail

```
public void sendCheckInErrorMail() {
        // 1️⃣ Tạo DTO và gán dữ liệu
        CheckInErrorDTO dto = new CheckInErrorDTO();
        dto.setUserId("U001");
        dto.setUsername("Nguyen Van A");
        dto.setEmail("user@example.com");
        dto.setStartWorkingShift("08:00");
        dto.setEndWorkingShift("17:00");
        dto.setDate(LocalDate.now());
        dto.setFirstCheckInTime(LocalTime.of(8, 15));
        dto.setLastCheckInTime(LocalTime.of(17, 0));
        dto.setLateCheckInMinutes(15);
        dto.setEarlyCheckOutMinutes(0);

        // 2️⃣ Gọi MailService sử dụng DTO và template Thymeleaf
        mailService.sendHtmlEmail(
                dto.getEmail(),
                MailSubject.CHECK_IN_ERROR_MAIL.getSubject(),
                "checkin-error-mail", // tên template Thymeleaf
                dto              
        );
    }
```

`Note`: 
- Lưu ý, truyền tên template mail trong thự mục templates là được
- Với mỗi template mail ứng với một DTO riêng: VD: Với `check-in-error-mail` ứng với DTO: `CheckInErrorDTO`
