package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.SubOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lazyb
 * @create 2020/3/3
 * @desc
 **/
@Repository
public interface SubOrderRepository extends JpaRepository<SubOrder, Long> {

    List<SubOrder> findAllByOrderId(Long orderId);

}
