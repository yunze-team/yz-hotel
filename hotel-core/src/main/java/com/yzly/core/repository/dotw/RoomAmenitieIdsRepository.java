package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.RoomAmenitieIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/24
 * @desc
 **/
@Repository
public interface RoomAmenitieIdsRepository extends JpaRepository<RoomAmenitieIds, Long> {

    RoomAmenitieIds findByCode(String code);

}
