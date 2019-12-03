package com.yzly.core.domain.dotw.vo;

import lombok.Data;

/**
 * @author lazyb
 * @create 2019/12/3
 * @desc
 **/
@Data
public class Passenger {

    private String salutationCode;
    private String firstName;
    private String lastName;

    public Passenger(String salutationCode, String firstName, String lastName) {
        this.salutationCode = salutationCode;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
