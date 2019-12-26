package com.yzly.api.common;

import com.meituan.hotel.openplatform.response.MtHotelResponse;
import com.yzly.api.vo.HotelInfoVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MeituanRestHandlerTest {
    @Autowired
    private MeituanRestHandler meituanRestHandler;
    @Test
    public void mtHotelPoiPushRequest() {
        HotelInfoVo hotelInfoVo =  new HotelInfoVo("1","11","180","wuhan","22","22",11);
        List<HotelInfoVo> hotelInfoVos = new ArrayList<>();
        hotelInfoVos.add(hotelInfoVo);
        List<MtHotelResponse> mtHotelResponses =  meituanRestHandler.mtHotelPoiPushRequest(hotelInfoVos);
        mtHotelResponses.forEach(u-> System.out.println(u.toString()));

    }
}
