package com.yzly.core.repository.dotw;

import com.yzly.core.domain.dotw.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by toby on 2019/11/21.
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Currency findByCode(String code);

    Currency findByName(String name);

}
