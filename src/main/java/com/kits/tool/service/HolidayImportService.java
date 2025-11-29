package com.kits.tool.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.kits.tool.dto.HolidayDTO;
import com.kits.tool.entity.HolidayEntity;
import com.kits.tool.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class HolidayImportService {

    @Autowired
    private HolidayRepository holidayRepository;

    public void importFromExcel(ClassPathResource resource) {
        System.out.println("Start importing holiday calendar...");
        List<HolidayDTO> dtos;

        try(InputStream is = resource.getInputStream()) {

            dtos = EasyExcel.read(is, HolidayDTO.class, null)
                    .head(HolidayDTO.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .headRowNumber(1)
                    .sheet()
                    .doReadSync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (HolidayDTO dto : dtos) {
            Date fullDate = dto.getHolidayDate();
            LocalDate rawDate = fullDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

//            LocalDate date = LocalDate.parse(dto.getHolidayDate(), DateTimeFormatter.ofPattern("yyyy/M/d"));
            if (!holidayRepository.existsByHolidayDate(rawDate)) {
                HolidayEntity holiday = new HolidayEntity();
                holiday.setHolidayDate(rawDate);
                holiday.setDetail(dto.getDetail());
                holiday.setType(HolidayEntity.HolidayType.valueOf(dto.getType())); // Public, Company, Others
                holidayRepository.save(holiday);
            }
        }
        System.out.println("End importing holiday calendar!");
    }

    public void generateWeekendHolidays(int year) {
        LocalDate date = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        while (!date.isAfter(end)) {
            DayOfWeek day = date.getDayOfWeek();
            if ((day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY)
                    && !holidayRepository.existsByHolidayDate(date)) {
                HolidayEntity holiday = new HolidayEntity();
                holiday.setHolidayDate(date);
                holiday.setDetail("Weekend Day");
                holiday.setType(HolidayEntity.HolidayType.Company);
                holidayRepository.save(holiday);
            }
            date = date.plusDays(1);
        }
    }
}
