package com.yzly.core.domain.meit.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 美团酒店扩展信息视图
 * @author lazyb
 * @create 2019/12/25
 * @desc
 **/
@Getter
@Setter
public class MeitHotelExt {

    private String hotelId;
    private List<Extsn> extsn;// 属性列表
    private List<Facilities> facilities;// 酒店设施
    private List<Service> service;// 酒店服务
    private List<Img> img;// 酒店图片,1=周边环境，9=客房，19=配套设施，32=大厅，34=门面，10=其他

}
