package com.yzly.core.repository.jl;

import com.yzly.core.domain.jl.JLRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author lazyb
 * @create 2021/1/25
 * @desc
 **/
@Repository
public interface JLRoomTypeRepository extends JpaRepository<JLRoomType, Long>,
        JpaSpecificationExecutor<JLRoomType> {

    JLRoomType findByRoomTypeId(String roomTypeId);

}
