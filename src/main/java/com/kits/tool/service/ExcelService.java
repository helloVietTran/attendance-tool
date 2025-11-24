package com.kits.tool.service;

import com.kits.tool.entity.DatabaseExportDTO;
import com.kits.tool.entity.DatabaseRowDTO;
import com.kits.tool.dto.ExcelExportDTO;
import com.kits.tool.dto.ExcelRowDTO;
import com.kits.tool.repository.AttendanceLogRepository;
import com.kits.tool.repository.ProcessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelService {
    @Autowired
    private ProcessLogRepository processLogRepository;
    @Autowired
    private AttendanceLogRepository attendanceLogRepository;

    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public void saveRawFromExcel(List<ExcelRowDTO> rawDatas) {
        for(ExcelRowDTO rawData : rawDatas) {
            DatabaseRowDTO dbRawLog = new DatabaseRowDTO();

            //Tìm theo logId để lưu record theo thời gian
            Optional<DatabaseRowDTO> row = processLogRepository.findById(dbRawLog.getId());
            if(row.isEmpty()){
                dbRawLog.setId(rawData.getId());
                dbRawLog.setEmpId(rawData.getEmpId());
                dbRawLog.setStartTime(LocalTime.parse(rawData.getStartTime(), timeFmt));
                dbRawLog.setEndTime(LocalTime.parse(rawData.getEndTime(),timeFmt));
                dbRawLog.setDate(LocalDate.parse(rawData.getDate(),dateFmt));
                dbRawLog.setCheckedTime(LocalTime.parse(sdf.format(rawData.getCheckedTime()),timeFmt));
            }

            processLogRepository.save(dbRawLog);
        }
    }

    public void saveNewFromExcel(List<ExcelExportDTO> newDatas) {
        for(ExcelExportDTO newData : newDatas) {
            DatabaseExportDTO dbNewLog = new DatabaseExportDTO();
            dbNewLog.setEmpId(newData.getEmpId());
            dbNewLog.setProcessLogInId(newData.getCheckinId());
            dbNewLog.setProcessLogOutId(newData.getCheckoutId());
            dbNewLog.setProcessDate(LocalDate.parse(newData.getDate(),dateFmt));
            dbNewLog.setCheckinTime(LocalTime.parse(newData.getCheckinTime(),timeFmt));
            dbNewLog.setCheckoutTime(LocalTime.parse(newData.getCheckoutTime(),timeFmt));

            attendanceLogRepository.save(dbNewLog);
        }
    }
}
