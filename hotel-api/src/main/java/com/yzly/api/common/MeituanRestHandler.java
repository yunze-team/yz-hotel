package com.yzly.api.common;

import com.alibaba.fastjson.JSON;
import com.meituan.hotel.openplatform.DefaultMtHotelClient;
import com.meituan.hotel.openplatform.MtHotelApiException;
import com.meituan.hotel.openplatform.MtHotelConfiguration;
import com.meituan.hotel.openplatform.domain.*;
import com.meituan.hotel.openplatform.request.*;
import com.meituan.hotel.openplatform.response.MtHotelResponse;
import com.yzly.api.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MeituanRestHandler {


    @Autowired
    private MtHotelConfiguration mtHotelConfiguration;

    /**
     * POI同步
     *
     * @return
     */
    public List<MtHotelResponse> mtHotelPoiPushRequest(List<HotelInfoVo> hotelInfoVos) {
        List<MtHotelResponse> mtHotelResponses = new ArrayList<>();
        if (hotelInfoVos.size() > 0){
            int count = (hotelInfoVos.size()/100)+1;
            for (int i=0;i<count;i++){
                List<PoiParam> poiParams = hotelInfoVos.stream().map(u->JSON.parseObject(JSON.toJSONString(u), PoiParam.class))
                        .collect(Collectors.toList());
                MtHotelPoiPushRequest request = new MtHotelPoiPushRequest();
                request.setPoiParamList(poiParams);
                mtHotelResponses.add(send(request));
            }
        }
        return mtHotelResponses;
    }

    /**
     * POI查询
     * @return
     */
    public MtHotelResponse mtHotelPoiQueryRequest(String poiId){
        // get MtHotelResponse
        Set<String> poiIds = new HashSet<>();
        poiIds.add(poiId);
        // put queryIds into MtHotelPoiQueryRequest
        MtHotelPoiQueryRequest request = new MtHotelPoiQueryRequest();
        request.setPoiSet(poiIds);
        return send(request);
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
        return send(request);
    }

    /**
     * 房态同步查询
     * @param roomQueryVo
     * @return
     */
    public MtHotelResponse MtHotelRoomStatusQueryRequest(RoomQueryVo roomQueryVo){
        MtHotelRoomStatusQueryRequest request =   new MtHotelRoomStatusQueryRequest();
        RoomStatusQueryParam queryParam = JSON.parseObject(JSON.toJSONString(roomQueryVo),RoomStatusQueryParam.class);
        request.setRoomStatusQueryParam(queryParam);
        return send(request);
    }

    /**
     * 房态更新价格
     * @param roomPriceVo
     * @return
     */
    public MtHotelResponse MtHotelRoomStatusPricePushRequest(RoomPriceVo roomPriceVo){
        MtHotelRoomStatusPricePushRequest request =   new MtHotelRoomStatusPricePushRequest();
        RoomStatusPriceParams queryParam = JSON.parseObject(JSON.toJSONString(roomPriceVo),RoomStatusPriceParams.class);
        request.setRoomStatusPriceParams(queryParam);
        return send(request);
    }

    /**
     * 产品库存同步
     * @param roomPriceVo
     * @return
     */
    public MtHotelResponse MtHotelRoomStatusStockPushRequest(RoomStockVo roomPriceVo){
        MtHotelRoomStatusStockPushRequest request =   new MtHotelRoomStatusStockPushRequest();
        RoomStatusQuotaParams queryParam = JSON.parseObject(JSON.toJSONString(roomPriceVo),RoomStatusQuotaParams.class);
        request.setRoomStatusQuotaParams(queryParam);
        return send(request);
    }

    /**
     * 房态产品上下线
     * @param roomStatesOnOff
     * @return
     */
    public MtHotelResponse MtHotelRoomStatusOnOffPushRequest(RoomStatesOnOff roomStatesOnOff){
        MtHotelRoomStatusOnOffPushRequest request =   new MtHotelRoomStatusOnOffPushRequest();
        RoomStatusOnOffParams roomStatusOnOffParams = JSON.parseObject(JSON.toJSONString(roomStatesOnOff),RoomStatusOnOffParams.class);
        request.setRoomStatusOnOffParams(roomStatusOnOffParams);
        return send(request);
    }


    /**
     * 房态创建新产品
     * @param roomInfoVo
     * @return
     */
    public MtHotelResponse MtHotelRoomStatusNewProdPushRequest(RoomInfoVo roomInfoVo){
        MtHotelRoomStatusNewProdPushRequest request =   new MtHotelRoomStatusNewProdPushRequest();
        RoomStatusNewParams roomStatusNewParams = JSON.parseObject(JSON.toJSONString(roomInfoVo),RoomStatusNewParams.class);
        request.setRoomStatusNewParams(roomStatusNewParams);
        return send(request);
    }


    private MtHotelResponse send(MtHotelRequest request){
        DefaultMtHotelClient client = new DefaultMtHotelClient(mtHotelConfiguration);
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
