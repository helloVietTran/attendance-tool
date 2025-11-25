package com.kits.tool.excel_process;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.kits.tool.dto.attendancedto.ExcelRowDTO;
import com.kits.tool.dto.payrolldto.YearMonthClosingDTO;

import java.util.ArrayList;
import java.util.List;

public class PayRollClosingListener extends AnalysisEventListener<YearMonthClosingDTO> {
    private final List<YearMonthClosingDTO> data = new ArrayList<>();

    @Override
    public void invoke(YearMonthClosingDTO row, AnalysisContext context) {
        data.add(row);
        System.out.println(row);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //In số dòng
        System.out.println("Đã đọc xong, tổng số dòng payroll: " + data.size());
    }

    public List<YearMonthClosingDTO> getData() {
        return data;
    }
}
