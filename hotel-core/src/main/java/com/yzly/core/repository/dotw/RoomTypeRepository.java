package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author lazyb
 * @create 2019/11/29
 * @desc
 **/
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    RoomType findByRoomTypeCode(String roomTypeCode);

}
