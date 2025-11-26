package com.kits.tool.service;

import com.kits.tool.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CalendarService {
    @Autowired
    private HolidayRepository holidayRepository;

    /**
     * Kiểm tra xem ngày nghỉ có tồn tại trong DB không
     * @param date ngày cần kiểm tra
     * @return true nếu có, false nếu không
     */
    public boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByHolidayDate(date);
    }
}
