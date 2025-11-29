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

# 📘 MailService -- Data Flow Documentation

Tài liệu mô tả chi tiết **luồng xử lý**, **quy trình gửi email**, và
**cơ chế binding dữ liệu vào template HTML** trong class `MailService`.

------------------------------------------------------------------------

## 📂 **1. Giới thiệu**

`MailService` cung cấp hai chức năng chính:

1.  **sendHtmlEmail(String, String, String, Map\<String, Object\>)**\
    → Gửi email HTML sử dụng Thymeleaf template và dữ liệu truyền qua
    Map.

2.  **sendHtmlEmail(String, String, String, Object)**\
    → Gửi email HTML từ một DTO bất kỳ (tự động convert DTO → Map).

------------------------------------------------------------------------

# 📄 **2. Luồng xử lý chi tiết**

------------------------------------------------------------------------

# 🔵 2.1. `sendHtmlEmail(String to, String subject, String templateName, Map<String,Object> variables)`

### **▶ Input**

-   **to** -- Email người nhận\
-   **subject** -- Tiêu đề email\
-   **templateName** -- Tên file template Thymeleaf\
-   **variables** -- Map chứa biến để render vào template

------------------------------------------------------------------------

## **📌 Quy trình xử lý**

### **Bước 1: Chuẩn bị Context cho Thymeleaf**

``` java
Context context = new Context();
context.setVariables(variables);
```

------------------------------------------------------------------------

### **Bước 2: Render nội dung HTML từ Template**

``` java
String htmlContent = templateEngine.process(templateName, context);
```

------------------------------------------------------------------------

### **Bước 3: Tạo email MIME**

``` java
MimeMessage message = mailSender.createMimeMessage();
MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
```

------------------------------------------------------------------------

### **Bước 4: Thiết lập thông tin email**

``` java
helper.setTo(to);
helper.setSubject(subject);
helper.setText(htmlContent, true);
helper.setFrom("noreply@yourcompany.com");
```

------------------------------------------------------------------------

### **Bước 5: Gửi email**

``` java
mailSender.send(message);
```

------------------------------------------------------------------------

### **Bước 6: Ghi log (TODO)**

``` java
System.out.println("TODO: Save log in database");
```

------------------------------------------------------------------------

# 🔵 2.2. `sendHtmlEmail(String to, String subject, String templateName, Object dto)`

### **▶ Input**

-   **dto** -- bất kỳ object nào (UserDTO, MailDTO,...)

------------------------------------------------------------------------

## **📌 Quy trình xử lý**

### **Bước 1: Convert DTO → Map**

``` java
Map<String, Object> variables = Arrays.stream(
    BeanUtils.getPropertyDescriptors(dto.getClass())
)
```

------------------------------------------------------------------------

### **Bước 2: Tạo map từ các getter**

Ví dụ DTO → Map:

``` json
{
  "username": "Lan",
  "email": "lan@example.com",
  "otp": "123456"
}
```

------------------------------------------------------------------------

### **Bước 3: Gọi lại hàm gửi email chính**

``` java
sendHtmlEmail(to, subject, templateName, variables);
```

------------------------------------------------------------------------

# 📈 **3. Sơ đồ luồng dữ liệu**

    Case 1: Map Input
    Map → Thymeleaf Context → Render HTML → MIME Message → Send Email

    Case 2: DTO Input
    DTO → Convert to Map → Thymeleaf → MIME → Send Email

------------------------------------------------------------------------

# ⚠️ **4. Gợi ý cải thiện**

-   Implement hệ thống log email vào database\
-   Validate email đầu vào\
-   Hỗ trợ file đính kèm\
-   Sử dụng @Async cho gửi email nền

------------------------------------------------------------------------

# 📬 Kết luận

`MailService` giúp linh hoạt gửi email HTML, hỗ trợ template cùng khả
năng tự động binding DTO → Template, dễ mở rộng và tái sử dụng.

