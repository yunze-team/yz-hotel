package com.yzly.core.repository.jl;

import com.yzly.core.domain.jl.JLCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2020/4/20
 * @desc
 **/
@Repository
public interface JLCityRepository extends JpaRepository<JLCity, Long>, JpaSpecificationExecutor<JLCity> {

    JLCity findByCityId(Integer cityId);

}
