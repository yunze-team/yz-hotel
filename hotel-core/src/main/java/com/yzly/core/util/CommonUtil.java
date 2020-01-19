package com.yzly.core.util;

import com.yzly.core.domain.HotelSyncList;
import com.yzly.core.domain.dotw.HotelInfo;
import com.yzly.core.domain.dotw.RoomPriceByDate;
import com.yzly.core.domain.dotw.RoomType;
import com.yzly.core.domain.dotw.vo.RoomPriceExcelData;
import com.yzly.core.domain.jltour.JLHotel;
import com.yzly.core.enums.DistributorEnum;
import com.yzly.core.enums.SupplierEnum;
import com.yzly.core.enums.SyncStatus;
import com.yzly.core.repository.dotw.RoomPriceByDateRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by toby on 2019/11/22.
 */
@Component
@CommonsLog
public class CommonUtil {

    @Autowired
    private RoomPriceByDateRepository roomPriceByDateRepository;

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
                Double hotelId = row.getCell(3).getNumericCellValue();
                if (hotelId > 0) {
                    HotelSyncList hotelSyncList = new HotelSyncList();
                    hotelSyncList.setDistributor(DistributorEnum.MEIT);
                    hotelSyncList.setSupplier(SupplierEnum.DOTW);
                    hotelSyncList.setSyncStatus(SyncStatus.UNSYNC);
                    hotelSyncList.setHotelId(String.valueOf(hotelId.intValue()));
                    list.add(hotelSyncList);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return list;
    }

    /**
     * 生成酒店30天房态的价格excel
     * @param rlist
     * @param days
     * @param filePath
     */
    public void generateRoomPriceExcel(List<RoomPriceExcelData> rlist, List<String> days, String filePath) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("房态30天价格");
        // 创建表头
        HSSFRow rowTitle = sheet.createRow(0);
        // 创建单元格样式
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle = buildBorderStyle(cellStyle);
        // 创建表头单元格
        // 创建酒店code
        HSSFCell cell_hotel_code = rowTitle.createCell(0);
        cell_hotel_code.setCellValue("酒店CODE");
        cell_hotel_code.setCellStyle(cellStyle);
        // 创建酒店名称
        HSSFCell cell_hotel_name = rowTitle.createCell(1);
        cell_hotel_name.setCellValue("酒店名称");
        cell_hotel_name.setCellStyle(cellStyle);
        // 创建房型code
        HSSFCell cell_room_code = rowTitle.createCell(2);
        cell_room_code.setCellValue("房型CODE");
        cell_room_code.setCellStyle(cellStyle);
        // 创建房型名称
        HSSFCell cell_room_name = rowTitle.createCell(3);
        cell_room_name.setCellValue("房型名称");
        cell_room_name.setCellStyle(cellStyle);
        // 创建房型价格
        for (int i = 0; i < days.size(); i++) {
            HSSFCell cell_room_price = rowTitle.createCell(3 + i + 1);
            cell_room_price.setCellValue(days.get(i));
            cell_room_price.setCellStyle(cellStyle);
        }
        HSSFCellStyle bodyCellStyle = workbook.createCellStyle();
        bodyCellStyle = buildBorderStyle(bodyCellStyle);
        // 表头结束，创建内容
        for (int i = 0; i < rlist.size(); i++) {
            RoomPriceExcelData roomPrice = rlist.get(i);
            List<RoomType> tlist = roomPrice.getRoomTypeList();
            for (int j = 0; j < tlist.size(); j++) {
                HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
                RoomType roomType = tlist.get(j);
                // 创建酒店code
                HSSFCell body_cell_hotel_code = row.createCell(0);
                body_cell_hotel_code.setCellValue(roomPrice.getHotelCode());
                body_cell_hotel_code.setCellStyle(bodyCellStyle);
                // 创建酒店名称
                HSSFCell body_cell_hotel_name = row.createCell(1);
                body_cell_hotel_name.setCellValue(roomPrice.getHotelName());
                body_cell_hotel_name.setCellStyle(bodyCellStyle);
                // 创建房型code
                HSSFCell body_cell_room_code = row.createCell(2);
                body_cell_room_code.setCellValue(roomType.getRoomTypeCode());
                body_cell_room_code.setCellStyle(bodyCellStyle);
                // 创建房型名称
                HSSFCell body_cell_room_name = row.createCell(3);
                body_cell_room_name.setCellValue(roomType.getName());
                body_cell_room_name.setCellStyle(bodyCellStyle);
                // 填充30天房价数据
                for (int k = 0; k < days.size(); k++) {
                    HSSFCell price_cell = row.createCell(3 + k + 1);
                    List<RoomPriceByDate> priceDates = roomPriceByDateRepository.
                            findAllByRoomTypeCodeAndFromDate(roomType.getRoomTypeCode(), days.get(k));
                    String rateBasisTotal = "";
                    for (RoomPriceByDate price : priceDates) {
                        rateBasisTotal += price.getRateBasis() + "/" + price.getTotal() + ";";
                    }
                    price_cell.setCellValue(rateBasisTotal);
                    price_cell.setCellStyle(bodyCellStyle);
                }
                // 最后一行时，合并酒店单元格
                if (j == tlist.size() - 1 && tlist.size() > 1) {
                    try {
                        bodyCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                        // 合并酒店code单元格
                        CellRangeAddress hotel_code_region = new CellRangeAddress(sheet.getLastRowNum() - j, sheet.getLastRowNum(), 0, 0);
                        sheet.addMergedRegion(hotel_code_region);
                        HSSFCell merge_hotel_code = sheet.getRow(sheet.getLastRowNum() - j).getCell(0);
                        merge_hotel_code.setCellStyle(bodyCellStyle);
                        merge_hotel_code.setCellValue(roomPrice.getHotelCode());
                        // 合并酒店名称单元格
                        CellRangeAddress hotel_name_region = new CellRangeAddress(sheet.getLastRowNum() - j, sheet.getLastRowNum(), 1, 1);
                        sheet.addMergedRegion(hotel_name_region);
                        HSSFCell merge_hotel_name = sheet.getRow(sheet.getLastRowNum() - j).getCell(1);
                        merge_hotel_name.setCellStyle(bodyCellStyle);
                        merge_hotel_name.setCellValue(roomPrice.getHotelName());
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        continue;
                    }
                }
            }
        }
        // 生成excel
        File file = new File(filePath);
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
            workbook.write(fout);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }

    }

    private HSSFCellStyle buildBorderStyle(HSSFCellStyle cellStyle) {
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        return cellStyle;
    }

}
