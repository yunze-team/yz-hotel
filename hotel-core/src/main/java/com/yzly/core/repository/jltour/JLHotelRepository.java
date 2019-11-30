package com.yzly.core.repository.jltour;

import com.yzly.core.domain.jltour.JLHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/1
 * @desc
 **/
@Repository
public interface JLHotelRepository extends JpaRepository<JLHotel, Long> {

    JLHotel findByHid(String hid);

}
