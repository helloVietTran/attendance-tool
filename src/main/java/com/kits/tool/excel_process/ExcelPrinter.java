package com.kits.tool.excel_process;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.kits.tool.dto.ExcelExportDTO;
import com.kits.tool.dto.ExcelRowDTO;
import com.kits.tool.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

@Component
public class ExcelPrinter implements CommandLineRunner {
    @Autowired
    ExcelService excelService;

    public void run(String... args) throws Exception {
        //Đọc file
        ClassPathResource resource  = new ClassPathResource("input/cham-cong.xlsx");
        List<ExcelRowDTO> rawData; //Lưu data gốc

        try(InputStream is = resource.getInputStream()) {
            //Check đọc file: in ra console
            ExcelListener listener = new ExcelListener();
            rawData = EasyExcel.read(is, ExcelRowDTO.class, listener)
                                .head(ExcelRowDTO.class)
                                .excelType(ExcelTypeEnum.XLSX)
                                .sheet()
                                .doReadSync();
        }

        //Chuyển format gốc sang format mới
//        List<ExcelExportDTO> export = new ArrayList<>();
//        for(ExcelRowDTO raw : rawData) {
//            ExcelExportDTO exp = new ExcelExportDTO();
//            exp.setId(raw.getId());
//            exp.setEmpId(raw.getEmpId());
//            exp.setStartTime(raw.getStartTime());
//            exp.setEndTime(raw.getEndTime());
//            exp.setDate(raw.getDate());
//            exp.setCheckinTime(raw.getCheckedTime());
//            exp.setCheckoutTime(raw.getCheckedTime());
//            export.add(exp);
//        }

        //Chuyển đổi format gốc sang format mới
        List<ExcelExportDTO> export = AttendanceTransformer.transform(rawData);

        //Lưu vào db dưới dạng format gốc
        excelService.saveRawFromExcel(rawData);

        //Lưu vào db dưới dạng format mới
        excelService.saveNewFromExcel(export);

        // Đường dẫn tới thư mục resources
        String path = Paths.get("src", "main", "resources", "output", "attendance.xlsx").toString();
        //Lưu ra file excel mới đặt trong trong thư mục output
        EasyExcel.write(path, ExcelExportDTO.class)
                .sheet()
                .doWrite(export);
        System.out.println("Đã ghi ra file: " + path);
    }
}
