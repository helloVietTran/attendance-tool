package com.kits.tool.service.excel;

import com.kits.tool.dto.ExcelExportDTO;
import com.kits.tool.dto.ExcelRowDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//Thực hiện chuyển đổi từ format checkedTime cũ sang format checkInTime và checkOutTime mới
public class AttendanceTransformer {
    public static List<ExcelExportDTO> transform(List<ExcelRowDTO> rawDatas) {
        // lưu key theo id và giữ thứ tự xuất hiện
        Map<String, ExcelExportDTO> map = new LinkedHashMap<>();

        for(ExcelRowDTO rawData : rawDatas) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String timeStr = timeFormat.format(rawData.getCheckedTime());

            //Lọc theo empId và date
            String key = rawData.getEmpId() + "_" + rawData.getDate();

            ExcelExportDTO newData = map.get(key);

            if(newData == null) {
                //Nếu chưa có nghĩa là empId + date lần đầu xuất hiện -> tạo mới
                newData = new ExcelExportDTO();
                newData.setCheckinId(rawData.getId());
                newData.setEmpId(rawData.getEmpId());
                newData.setDate(rawData.getDate());
                newData.setCheckinTime(timeStr);

                //So sánh để tìm nhân viên nghỉ -> gán checkout time
                if("23:59".equals(timeStr)) {
                    newData.setCheckoutTime(timeStr);
                    newData.setCheckoutId(rawData.getId());
                }

                map.put(key, newData);
            } else {
                // đã có record cho empId + date → gán checkout
                newData.setCheckoutId(rawData.getId());
                newData.setCheckoutTime(timeStr);
            }
        }

        return new ArrayList<>(map.values());
    }
}
