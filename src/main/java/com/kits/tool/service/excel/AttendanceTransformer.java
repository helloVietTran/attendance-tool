package com.kits.tool.service.excel;

import com.kits.tool.dto.ExcelExportDTO;
import com.kits.tool.dto.ExcelRowDTO;

import java.time.LocalTime;
import java.util.*;

//Thực hiện chuyển đổi từ format checkedTime cũ sang format checkInTime và checkOutTime mới
public class AttendanceTransformer {
    public static List<ExcelExportDTO> transform(List<ExcelRowDTO> rawDatas) {
        // lưu key theo id và giữ thứ tự xuất hiện
        Map<String, ExcelExportDTO> map = new LinkedHashMap<>();

        for(ExcelRowDTO rawData : rawDatas) {
            //Lọc theo empId và date
            String key = rawData.getEmpId() + "_" + rawData.getDate();

            ExcelExportDTO newData = map.get(key);

            if(newData == null) {
                //Nếu chưa có nghĩa là empId + date lần đầu xuất hiện -> tạo mới
                newData = new ExcelExportDTO();
                newData.setCheckinId(rawData.getId());
                newData.setEmpId(rawData.getEmpId());
                newData.setDate(rawData.getDate());
                newData.setCheckinTime(rawData.getCheckedTime());

                //So sánh để tìm nhân viên nghỉ -> gán checkout time
                if(rawData.getCheckedTime().equals(LocalTime.of(23, 59, 0))) {
                    newData.setCheckoutTime(rawData.getCheckedTime());
                    newData.setCheckoutId(rawData.getId());
                }

                map.put(key, newData);
            } else {
                // đã có record cho empId + date → gán checkout
                newData.setCheckoutId(rawData.getId());
                newData.setCheckoutTime(rawData.getCheckedTime());
            }
        }

        return new ArrayList<>(map.values());
    }
}
