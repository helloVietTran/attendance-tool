# 🚀 Setup Guide

## 📋 Bước 1: Cấu hình Database & Email

### 1. Copy file config:
```bash
cp src/main/resources/application.yaml.example src/main/resources/application.yaml
```

### 2. Sửa `application.yaml`:
```yaml
spring:
  datasource:
    password: YOUR_MYSQL_PASSWORD      # ← Đổi password MySQL
  mail:
    username: your-email@gmail.com     # ← Đổi email thật
    password: your-app-password        # ← Đổi app password
```

---

## 📧 Bước 2: Lấy Gmail App Password

1. Vào: https://myaccount.google.com/apppasswords
2. Tạo app password (16 ký tự)
3. Copy vào `application.yaml`

---

## 🗄️ Bước 3: Tạo Database

```sql
CREATE DATABASE attendance_tool;
USE attendance_tool;

-- Chạy các file SQL trong src/main/resources/sql/
```

---

## ▶️ Bước 4: Chạy

```bash
mvn spring-boot:run
```

---

✅ Done!

