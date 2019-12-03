package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.SalutationsIds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2019/12/3
 * @desc
 **/
@Repository
public interface SalutationsIdsRepository extends JpaRepository<SalutationsIds, Long> {

    SalutationsIds findByCode(String code);

}
