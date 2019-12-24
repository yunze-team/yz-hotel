package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.LeisureIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/24
 * @desc
 **/
@Repository
public interface LeisureIdsRepository extends JpaRepository<LeisureIds, Long> {

    LeisureIds findByCode(String code);

}
