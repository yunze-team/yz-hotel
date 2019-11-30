package com.yzly.core.service.jltour;

import com.yzly.core.domain.jltour.JLHotel;
import com.yzly.core.repository.jltour.JLHotelRepository;
import com.yzly.core.util.CommonUtil;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author lazyb
 * @create 2019/12/1
 * @desc
 **/
@Service
@CommonsLog
public class JLHotelService {

    @Autowired
    private JLHotelRepository jlHotelRepository;
    @Value("${jl.hotel.excel}")
    private String jlExcelPath;
    @Autowired
    private CommonUtil commonUtil;

    @Transactional
    public void syncByExcel() throws Exception {
        List<JLHotel> hlist = commonUtil.excelToJLBean(jlExcelPath);
        int i = 0;
        for (JLHotel hotel : hlist) {
            if (jlHotelRepository.findByHid(hotel.getHid()) != null) {
                continue;
            }
            jlHotelRepository.save(hotel);
            i++;
            log.info("insert jl success size:" + i);
        }
    }

}
