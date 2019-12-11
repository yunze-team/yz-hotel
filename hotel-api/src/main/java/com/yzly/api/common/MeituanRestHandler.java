package com.yzly.api.common;

import com.meituan.hotel.openplatform.DefaultMtHotelClient;
import com.meituan.hotel.openplatform.MtHotelApiException;
import com.meituan.hotel.openplatform.MtHotelConfiguration;
import com.meituan.hotel.openplatform.domain.InventoryPriceParam;
import com.meituan.hotel.openplatform.domain.PoiParam;
import com.meituan.hotel.openplatform.domain.RoomStatusAllParam;
import com.meituan.hotel.openplatform.domain.RoomStatusAllParams;
import com.meituan.hotel.openplatform.internal.domain.Environment;
import com.meituan.hotel.openplatform.request.MtHotelPoiPushRequest;
import com.meituan.hotel.openplatform.request.MtHotelPoiQueryRequest;
import com.meituan.hotel.openplatform.request.MtHotelRoomStatusAllPushRequest;
import com.meituan.hotel.openplatform.response.MtHotelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class MeituanRestHandler {

    /**
     * POI同步
     *
     * @return
     */
    public MtHotelResponse mtHotelPoiPushRequest() {
        // get MtHotelResponse
        PoiParam params = new PoiParam();
        params.setAddress("");//酒店地址
        List<PoiParam> poiParamList = new ArrayList<>();
        poiParamList.add(params);
        // put PoiParam into MtHotelPoiPushRequest
        MtHotelPoiPushRequest request = new MtHotelPoiPushRequest();
        request.setPoiParamList(poiParamList);
        // initialization configure
        MtHotelConfiguration conf;
        conf = new MtHotelConfiguration("partnerId", "encryptKey");
        conf.setEnv(Environment.DEV); // Environment.DEV for developing or testing
        // go to configure. e.g. conf.setXxx ……
        // create client for send request
        DefaultMtHotelClient client = new DefaultMtHotelClient(conf);
        MtHotelResponse response = null;
        try {
            response = client.execute(request);
        } catch (MtHotelApiException e) {
            // there will throw MtHotelApiException when request parameter error
            System.out.println(e.getErrCode() + " -- " + e.getErrMsg());
        }
        // get request body from respnose
        System.out.println("RequestBody: " + response.getReqBody());
        // get configuration from response
        System.out.println("Configuration: " + response.getConfiguration());
        return response;
    }

    /**
     * POI查询
     * @return
     */
    public MtHotelResponse mtHotelPoiQueryRequest(){
        // get MtHotelResponse
       String poiId="";//酒店地址
        Set<String> poiIds = new HashSet<>();
        poiIds.add(poiId);
        // put queryIds into MtHotelPoiQueryRequest
        MtHotelPoiQueryRequest request = new MtHotelPoiQueryRequest();
        request.setPoiSet(poiIds);
        // initialization configure
        MtHotelConfiguration conf;
        conf = new MtHotelConfiguration("partnerId", "encryptKey");
        conf.setEnv(Environment.DEV); // Environment.DEV for developing or testing
        // go to configure. e.g. conf.setXxx ……
        // create client for send request
        DefaultMtHotelClient client = new DefaultMtHotelClient(conf);
        MtHotelResponse response = null;
        try {
            response = client.execute(request);
        } catch (MtHotelApiException e) {
            // there will throw MtHotelApiException when request parameter error
            System.out.println(e.getErrCode() + " -- " + e.getErrMsg());
        }
        // get request body from respnose
        System.out.println("RequestBody: " + response.getReqBody());
        // get configuration from response
        System.out.println("Configuration: " + response.getConfiguration());
        return response;
    }

    /**
     * 房态全量同步
     * @return
     */
    public MtHotelResponse mtHotelRoomStatusAllPushRequest(){
        // get MtHotelResponse
        RoomStatusAllParams roomStatusAllParams =new RoomStatusAllParams();
        InventoryPriceParam inventoryPriceParam= new InventoryPriceParam();
        //房态map
        Map<String,List<RoomStatusAllParam>> map= new HashMap<>();
        List<InventoryPriceParam> inventoryPriceParamList =new ArrayList<>();
        inventoryPriceParamList.add(inventoryPriceParam);
        roomStatusAllParams.setDetail(map);
        // put roomStatusAllParam into MtHotelRoomStatusAllPushRequest
        MtHotelRoomStatusAllPushRequest request = new MtHotelRoomStatusAllPushRequest();
        request.setRoomStatusAllParams(roomStatusAllParams);
        // initialization configure
        MtHotelConfiguration conf;
        conf = new MtHotelConfiguration("partnerId", "encryptKey");
        conf.setEnv(Environment.DEV); // Environment.DEV for developing or testing
        // go to configure. e.g. conf.setXxx ……
        // create client for send request
        DefaultMtHotelClient client = new DefaultMtHotelClient(conf);
        MtHotelResponse response = null;
        try {
            response = client.execute(request);
        } catch (MtHotelApiException e) {
            // there will throw MtHotelApiException when request parameter error
            System.out.println(e.getErrCode() + " -- " + e.getErrMsg());
        }
        // get request body from respnose
        System.out.println("RequestBody: " + response.getReqBody());
        // get configuration from response
        System.out.println("Configuration: " + response.getConfiguration());
        return response;
    }

}
