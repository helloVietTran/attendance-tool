package com.kits.tool.service.excel;

import com.kits.tool.dto.ExcelExportDTO;
import com.kits.tool.dto.ExcelRowDTO;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

//Thực hiện chuyển đổi từ format checkedTime cũ sang format checkInTime và checkOutTime mới
public class AttendanceTransformer {
    public static List<ExcelExportDTO> transform(List<ExcelRowDTO> rawDatas) {
        // lưu key theo id và giữ thứ tự xuất hiện
        Map<String, ExcelExportDTO> map = new LinkedHashMap<>();

        for(ExcelRowDTO rawData : rawDatas) {
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String timeStr = timeFormat.format(rawData.getCheckedTime());

            //Tách giờ phút: từ dạng Date gốc về dạng LocalTime
            Date fullTime = rawData.getCheckedTime();
            LocalTime rawTime = fullTime
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime();

            //Tách ngày: từ dạng Date gốc về dạng LocalDate
            Date fullDate = rawData.getDate();
            LocalDate rawDate = fullDate
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            //Lọc theo empId và date
            String key = rawData.getEmpId() + "_" + rawData.getDate();

            ExcelExportDTO newData = map.get(key);

            if(newData == null) {
                //Nếu chưa có nghĩa là empId + date lần đầu xuất hiện -> tạo mới
                newData = new ExcelExportDTO();
                newData.setCheckinId(rawData.getId());
                newData.setEmpId(rawData.getEmpId());
                newData.setDate(rawDate);
                newData.setCheckinTime(rawTime);

                //So sánh để tìm nhân viên nghỉ -> gán checkout time
                if("23:59".equals(timeStr)) {
                    newData.setCheckoutTime(rawTime);
                    newData.setCheckoutId(rawData.getId());
                }

                map.put(key, newData);
            } else {
                // đã có record cho empId + date → gán checkout
                newData.setCheckoutId(rawData.getId());
                newData.setCheckoutTime(rawTime);
            }
        }

        return new ArrayList<>(map.values());
    }
}
