package com.yzly.core.repository.meit;

import com.yzly.core.domain.meit.MeitCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lazyb
 * @create 2019/12/20
 * @desc
 **/
@Repository
public interface MeitCityRepository extends JpaRepository<MeitCity, Long> {

    MeitCity findByNameEN(String name);

    List<MeitCity> findAllByNameEN(String name);

    List<MeitCity> findAllByNameENLike(String name);

}
