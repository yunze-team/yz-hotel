package com.yzly.api.util;

import com.yzly.api.annotations.Data;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class CommonUtil {

    public final static String VALID_One_MSG = "长度限制为1";
    public final static String VALID_NO_MSG = "长度限制为2";
    public final static String VALID_THREE_MSG = "长度限制为3";
    public final static String VALID_SIX_MSG = "长度限制为3";
    public final static String VALID_ELEVEN_MSG = "长度限制为11";
    public final static String VALID_ACCOUNT_MSG = "长度限制为16";
    public final static String VALID_EIGHTEEN_MSG = "长度限制为18";
    public final static String VALID_Twenty_MSG = "长度限制为20";
    public final static String VALID_TFive_MSG = "长度限制为25";
    public final static String VALID_UUID_MSG = "长度限制为32";
    public final static String VALID_FIVTY_MSG = "长度限制为50";
    public final static String VALID_MAX_MSG = "长度最大限制为128";

    private  final static DateTimeFormatter SDF_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private  final static DateTimeFormatter SDF_TIME = DateTimeFormatter.ofPattern("HHmmssSSS");
    private  final static DateTimeFormatter SDF_DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String dateFormat(LocalDate date){
        return date.format(SDF_DATE);
    }

    public static String timeFormat(LocalTime date){
        return date.format(SDF_TIME);
    }
    public static String dateTimeFormat(LocalDateTime date){
        return date.format(SDF_DATE_TIME);
    }

    public static LocalTime toLocalTime(String time){
        return LocalTime.parse(time, SDF_TIME);
    }

    public static LocalDate toLocalDate(String date){
        return LocalDate.parse(date, SDF_DATE);
    }

    public static LocalDateTime toLocalDateTime(String localDateTime){
        return LocalDateTime.parse(localDateTime, SDF_DATE_TIME);
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","").toLowerCase();
    }

    /**
     * @author chenlei
     * @description //TODO customName->CUSTOM_NAME
     * @date 18:31 2019/12/3
     * @param
     * @return
     **/
    public static String upperCaseToUnderLine(String s){
        char [] chars = s.toCharArray();
        String result = chars[0]+"";
        for (int i=1;i<chars.length;i++){
            char c = chars[i];
            if('A'<=c&&c<='Z'){
                c += 32;
                result+="_"+c;
            }else{
                result+=c;
            }
        }

        return result.toUpperCase();
    }

    /**
     * @author chenlei
     * @description //TODO CUSTOM_NAME->customName
     * @date 16:10 2019/12/5
     * @param
     * @return
     * @exception
     **/
    public static String uppcaseUnderlineToLowerCase(String s){
        char [] chars = s.toLowerCase().toCharArray();
        String result = "";
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if(c =='_'){
                char a = chars[i+1]-=32;
                result += a;
                i = i+1;
            }else{
                result += c;
            }
        }
        return result;
    }

    public static String getElementName(Field field){
        String elementName = null;
        if(field.isAnnotationPresent(Data.class)){
            elementName = field.getAnnotation(Data.class).value();
        }
        if(elementName.equals("")){//
            field.setAccessible(true);
            elementName =upperCaseToUnderLine(field.getName());
        }
        return elementName;
    }

}
