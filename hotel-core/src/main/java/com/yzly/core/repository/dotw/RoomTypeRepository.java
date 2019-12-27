package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lazyb
 * @create 2019/11/29
 * @desc
 **/
@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    RoomType findByRoomTypeCode(String roomTypeCode);

    List<RoomType> findAllByHotelId(String hotelId);

}
