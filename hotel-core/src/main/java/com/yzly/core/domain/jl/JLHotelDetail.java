package com.yzly.core.domain.jl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 捷旅-酒店明细信息(包含酒店信息、房型信息、价格信息和图片信息)
 * @author lazyb
 * @create 2020/5/18
 * @desc
 **/
@Document(collection = "jl_hotel_detail")
@Data
@NoArgsConstructor
@ToString
public class JLHotelDetail {

    @Id
    private ObjectId id;

    private Integer hotelId;// 酒店编号

    private JSONObject hotelInfo;// 酒店明细

    private JSONArray roomTypeList;// 房型数组

    private JSONArray rateTypeList;// 价格类型数组

    private JSONArray imageList;// 图片数组

}
