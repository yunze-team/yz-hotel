package com.yzly.api.constants.xiecheng;


import com.yzly.api.annotations.XMLData;

public class Auth {
    @XMLData("ID")
    private String ID;
    @XMLData("MessagePassword")
    private String MessagePassword;
    @XMLData("Type")
    private String Type;
    @XMLData("Code")
    private String Code;
    @XMLData("CodeContext")
    private String CodeContext;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMessagePassword() {
        return MessagePassword;
    }

    public void setMessagePassword(String messagePassword) {
        MessagePassword = messagePassword;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getCodeContext() {
        return CodeContext;
    }

    public void setCodeContext(String codeContext) {
        CodeContext = codeContext;
    }
}
