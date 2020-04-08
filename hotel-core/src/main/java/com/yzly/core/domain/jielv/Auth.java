package com.yzly.core.domain.jielv;

import lombok.Data;

@Data
public class Auth {
    private String ID;
    private String MessagePassword;
    private String Type;
    private String Code;
    private String CodeContext;

}
