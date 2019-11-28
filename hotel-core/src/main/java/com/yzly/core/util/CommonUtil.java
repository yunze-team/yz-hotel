package com.yzly.core.util;

import com.yzly.core.domain.dotw.HotelInfo;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by toby on 2019/11/22.
 */
@Component
@CommonsLog
public class CommonUtil {

    public List<HotelInfo> excelToHotelBean(String path) throws Exception {
        InputStream is = new FileInputStream(path);
        XSSFWorkbook excel = new XSSFWorkbook(is);
        HotelInfo hotelInfo = null;
        List<HotelInfo> list = new ArrayList<>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < excel.getNumberOfSheets(); numSheet++) {
            XSSFSheet sheet = excel.getSheetAt(numSheet);
            if (sheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                hotelInfo = new HotelInfo();
                hotelInfo.setRegion(row.getCell(0).getStringCellValue());
                hotelInfo.setCountry(row.getCell(1).getStringCellValue());
                hotelInfo.setShortCountryName(row.getCell(2).getStringCellValue());
                hotelInfo.setCountryCode(row.getCell(3).getRawValue());
                hotelInfo.setCity(row.getCell(4).getStringCellValue());
                hotelInfo.setCityCode(row.getCell(5).getRawValue());
                hotelInfo.setDotwHotelCode(row.getCell(6).getRawValue());
                hotelInfo.setHotelbedsHotelCode(row.getCell(7).getRawValue());
                hotelInfo.setExpediaHotelCode(row.getCell(8).getRawValue());
                try {
                    hotelInfo.setHotelName(row.getCell(9).getStringCellValue());
                } catch (Exception e) {
                    hotelInfo.setHotelName(row.getCell(9).getRawValue());
                }
                try {
                    hotelInfo.setStarRating(row.getCell(10).getStringCellValue());
                } catch (Exception e) {
                    hotelInfo.setStarRating(row.getCell(10).getRawValue());
                }
                try {
                    hotelInfo.setReservationTelephone(row.getCell(11).getStringCellValue());
                } catch (Exception e) {
                    hotelInfo.setReservationTelephone(row.getCell(11).getRawValue());
                }
                hotelInfo.setHotelAddress(row.getCell(12).getStringCellValue());
                hotelInfo.setLatitude(row.getCell(13).getRawValue());
                hotelInfo.setLongitude(row.getCell(14).getRawValue());
                try {
                    hotelInfo.setChainName(row.getCell(15).getStringCellValue());
                } catch (Exception e) {
                    hotelInfo.setChainName(row.getCell(15).getRawValue());
                }
                try {
                    hotelInfo.setBrandName(row.getCell(16).getStringCellValue());
                } catch (Exception e) {
                    hotelInfo.setBrandName(row.getCell(16).getRawValue());
                }
                try {
                    hotelInfo.setNewProperty(row.getCell(17).getStringCellValue());
                } catch (Exception e) {
                    hotelInfo.setNewProperty(row.getCell(17).getRawValue());
                }
                list.add(hotelInfo);
            }
        }
        return list;
    }

}
