package com.kits.tool.excel_process;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.kits.tool.dto.ExcelRowDTO;

import java.util.ArrayList;
import java.util.List;

public class ExcelListener extends AnalysisEventListener<ExcelRowDTO> {
    private final List<ExcelRowDTO> data = new ArrayList<>();

    @Override
    public void invoke(ExcelRowDTO row, AnalysisContext context) {
        data.add(row);
        System.out.println(row);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //In số dòng
        System.out.println("Đã đọc xong, tổng số dòng: " + data.size());
    }

    public List<ExcelRowDTO> getData() {
        return data;
    }
}
