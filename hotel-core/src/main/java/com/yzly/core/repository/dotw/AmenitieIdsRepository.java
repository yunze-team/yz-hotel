package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.AmenitieIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/24
 * @desc
 **/
@Repository
public interface AmenitieIdsRepository extends JpaRepository<AmenitieIds, Long> {

    AmenitieIds findByCode(String code);

}
