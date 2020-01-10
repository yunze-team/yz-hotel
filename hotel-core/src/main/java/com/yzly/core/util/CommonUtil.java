package com.yzly.core.util;

import com.yzly.core.domain.HotelSyncList;
import com.yzly.core.domain.dotw.HotelInfo;
import com.yzly.core.domain.jltour.JLHotel;
import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.enums.SupplierEnum;
import com.yzly.core.enums.SyncStatus;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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

    public List<JLHotel> excelToJLBean(String path) throws Exception {
        // 注意，此处是2003excel格式
        InputStream is = new FileInputStream(path);
        // XSSFWorkbook excel = new XSSFWorkbook(is);
        Workbook workbook = WorkbookFactory.create(is);
        JLHotel jlHotel = null;
        List<JLHotel> hlist = new ArrayList<>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
            Sheet sheet = workbook.getSheetAt(numSheet);
            if (sheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                jlHotel = new JLHotel();
                try {
                    jlHotel.setHid(row.getCell(0).getStringCellValue());
                } catch (Exception e) {
                    jlHotel.setHid(null);
                }
                try {
                    jlHotel.setName(row.getCell(1).getStringCellValue());
                } catch (Exception e) {
                    jlHotel.setName(null);
                }
                try {
                    jlHotel.setAddress(row.getCell(2).getStringCellValue());
                } catch (Exception e) {
                    jlHotel.setAddress(null);
                }
                try {
                    jlHotel.setTel(row.getCell(3).getStringCellValue());
                } catch (Exception e) {
                    jlHotel.setTel(null);
                }
                try {
                    jlHotel.setLongitude(row.getCell(4).getStringCellValue());
                } catch (Exception e) {
                    jlHotel.setLongitude(null);
                }
                try {
                    jlHotel.setLatitude(row.getCell(5).getStringCellValue());
                } catch (Exception e) {
                    jlHotel.setLatitude(null);
                }
                if (StringUtils.isNotEmpty(jlHotel.getHid())) {
                    hlist.add(jlHotel);
                }
            }
        }
        log.info("jl excel list size:" + hlist.size());
        return hlist;
    }

    /**
     * 读取excel获取美团酒店同步列表
     * @param path
     * @return
     * @throws Exception
     */
    public List<HotelSyncList> execlToSyncList(String path) throws Exception {
        InputStream is = new FileInputStream(path);
        XSSFWorkbook excel = new XSSFWorkbook(is);
        List<HotelSyncList> list = new ArrayList<>();
        // 取工作表第一个
        XSSFSheet sheet = excel.getSheetAt(0);
        // 循环行Row
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            // 取出hotelid
            try {
                String hotelId = row.getCell(3).getStringCellValue();
                Long dotwHotelId = Long.valueOf(hotelId);
                if (dotwHotelId > 0) {
                    HotelSyncList hotelSyncList = new HotelSyncList();
                    hotelSyncList.setDistributor(DistributorEnum.MEIT);
                    hotelSyncList.setSupplier(SupplierEnum.DOTW);
                    hotelSyncList.setSyncStatus(SyncStatus.UNSYNC);
                    hotelSyncList.setHotelId(hotelId);
                    list.add(hotelSyncList);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return list;
    }

}
