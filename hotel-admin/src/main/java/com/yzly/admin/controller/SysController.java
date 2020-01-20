package com.yzly.admin.controller;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author lazyb
 * @create 2020/1/19
 * @desc
 **/
@Controller
@CommonsLog
public class SysController {

    @Value("${dotw.room.price.excel}")
    private String excelFileName;

    @RequestMapping("/sys/price_file_download")
    public String downloadPriceFile(HttpServletResponse response) {
        String fileName = "room_price.xls";
        File file = new File(excelFileName);
        if (file.exists()) {
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                log.info("price excel file download success");
                return "SUCCESS";
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
        log.info("price excel file download fail");
        return "FAIL";
    }

}
