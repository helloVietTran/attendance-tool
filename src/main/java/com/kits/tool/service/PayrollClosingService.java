package com.kits.tool.service;

import com.kits.tool.dto.payrolldto.DateClosingDTO;
import com.kits.tool.dto.payrolldto.YearMonthClosingDTO;
import com.kits.tool.entity.payrollclosing_entity.PayrollClosingEntity;
import com.kits.tool.repository.PayRollClosingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayrollClosingService {
    @Autowired
    PayRollClosingRepository payRollClosingRepository;

    //Method chuyển dạng ngày tháng năm rời rạc sang dạng chuẩn date
    public List<DateClosingDTO> fromRawtoDate(List<YearMonthClosingDTO> excelRows) throws Exception {
        List<DateClosingDTO> result = new ArrayList<>();
        for (YearMonthClosingDTO row : excelRows) {
            for(int m = 1; m <= 12; m++) {
                //Xử lí lấy số ngày theo tháng
                YearMonth ym = YearMonth.of(row.getYear(), m);
                int days = ym.lengthOfMonth();
                int day = getClosingDay(row, m);
                String date = "";

                if(m==2){
                    if(day>0 && day<=29){
                        date = LocalDate.of(row.getYear(), m, day).toString();
                    }
                } else if(days == 31){
                    if(day>0 && day<=31){
                        date = LocalDate.of(row.getYear(), m, day).toString();
                    }
                } else if(days == 30) {
                    if(day>0 && day<=30){
                        date = LocalDate.of(row.getYear(), m, day).toString();
                    }
                } else {
                    throw new Exception("Ngày không hợp lệ tại tháng "+m+" năm "+row.getYear());
                }
                DateClosingDTO standardDate = new DateClosingDTO();
                standardDate.setClosingDate(date);

                result.add(standardDate);
            }
        }
        return result;
    }

    //Method lấy ngày theo cột tháng
    public static int getClosingDay(YearMonthClosingDTO row, int month) {
        switch (month) {
            case 1: return row.getMonth1();
            case 2: return row.getMonth2();
            case 3: return row.getMonth3();
            case 4: return row.getMonth4();
            case 5: return row.getMonth5();
            case 6: return row.getMonth6();
            case 7: return row.getMonth7();
            case 8: return row.getMonth8();
            case 9: return row.getMonth9();
            case 10: return row.getMonth10();
            case 11: return row.getMonth11();
            case 12: return row.getMonth12();
            default: throw new IllegalArgumentException("Tháng không hợp lệ: " + month);
        }
    }

    public void saveToDb(List<DateClosingDTO> dates) {

        for(DateClosingDTO dto : dates) {
            //Ngày chốt công
            LocalDate closingDate = LocalDate.parse(dto.getClosingDate());
            //Ngày đầu tháng -> tính cho phần tử đầu của danh sách
            LocalDate firstDayofMonth = closingDate.withDayOfMonth(1);

            //Entity lưu dữ liệu vào db
            PayrollClosingEntity prcEntity= new PayrollClosingEntity();


            if(dates.getFirst().equals(dto)) {
                //Set id theo index của list dates để cập nhật
                prcEntity.setId(1);
                prcEntity.setStartDate(firstDayofMonth);
                prcEntity.setEndDate(closingDate);
            } else {
                //Ngày chốt công tháng trước
                LocalDate previousPrcDate = LocalDate.parse(dates.get(dates.indexOf(dto)-1).getClosingDate());
                //Ngày bắt đầu tính công = Ngày chốt tháng trước + 1 (trừ tháng đầu của danh sách)
                LocalDate startingDate =  previousPrcDate.plusDays(1);

                prcEntity.setId(dates.indexOf(dto)+1);
                prcEntity.setStartDate(startingDate);
                prcEntity.setEndDate(closingDate);
            }

            payRollClosingRepository.save(prcEntity);
        }
    }

}

