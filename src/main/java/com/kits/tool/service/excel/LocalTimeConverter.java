package com.kits.tool.service.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeConverter implements Converter<LocalTime> {
        @Override
        public Class<?> supportJavaTypeKey() {
            return LocalTime.class;
        }

        @Override
        public LocalTime convertToJavaData(ReadCellData<?> cellData,
                                           ExcelContentProperty contentProperty,
                                           GlobalConfiguration globalConfiguration) {
            if (cellData.getType() == CellDataTypeEnum.NUMBER) {
                // Excel lưu giờ dạng fraction of day
                double fraction = cellData.getNumberValue().doubleValue();
                // Làm tròn để tránh lỗi floating point precision
                long totalSeconds = Math.round(fraction * 24 * 60 * 60);
                return LocalTime.ofSecondOfDay(totalSeconds);
            } else {
                // Nếu là chuỗi thì parse theo format
                String raw = cellData.getStringValue();
                return LocalTime.parse(raw, DateTimeFormatter.ofPattern("HH:mm:ss"));
            }
        }
    }

