package com.yzly.api.util.meit.international;

import com.yzly.core.enums.meit.ResultEnum;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lazyb
 * @create 2019/12/17
 * @desc
 **/
public class AuthValidatorUtil {

    public ResultEnum validate(HttpServletRequest request, String secret) {
        if (request == null) {
            return ResultEnum.PARAM_ERROR_BA;//(false, 12, "美团认证校验失败")
        }
        String uri = request.getRequestURI();
        String authorizationByHeader = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorizationByHeader)) {
            return ResultEnum.PARAM_ERROR_BA_AUTH;//(false, 121, "美团认证校验失败,Authorization错误")
        }
        int partnerIdInHeader = getPartnerIdInHeader(authorizationByHeader);
        if (partnerIdInHeader == 0) {
            return ResultEnum.PARAM_ERROR_BA_PARTNER_ID;//(false, 123, "美团认证校验失败,partnerId错误")
        }
        String dateByHeader = request.getHeader("Date");
        if (StringUtils.isEmpty(dateByHeader)) {
            return ResultEnum.PARAM_ERROR_BA_DATE;//(false, 122, "美团认证校验失败,Date 错误")
        }
        //now-10min<=date<=now+10min
        DateTime date = headerFormat2Datetime(dateByHeader);
        if (DateTime.now().minusMinutes(10).isAfter(date) ||
                DateTime.now().plusMinutes(10).isBefore(date)) {
            return ResultEnum.PARAM_ERROR_BA_DATE;//(false, 122, "美团认证校验失败,Date 错误")
        }
        String encode;
        try {
            encode = generateSignature(request.getMethod().toUpperCase(), uri,
                    dateByHeader, secret);
        } catch (Exception e) {
            return ResultEnum.PARAM_ERROR_BA;//(false, 12, "美团认证校验失败")
        }
        String authorization = "MWS " + partnerIdInHeader + ":" + encode;
        if (!authorizationByHeader.equals(authorization)) {
            return ResultEnum.PARAM_ERROR_BA_AUTH;//(false, 121, "美团认证校验失败,Authorization错误")
        }
        return ResultEnum.SUCCESS;//(true, 0, "请求成功")

    }

    private String generateSignature(String method, String reqUrl, String date, String secret) {
        StringBuilder sign = new StringBuilder();
        sign.append(method);
        sign.append(" ");
        sign.append(reqUrl);
        sign.append("\n");
        sign.append(date);
        byte[] sha1 = hmac_sha1(sign.toString(), secret);
        String signature;
        try {
            signature = new String(Base64.encodeBase64(sha1), "UTF-8");
            return signature;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private byte[] hmac_sha1(String value, String key) {
        try {
            byte[] keyBytes = key.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes,
                    "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            return mac.doFinal(value.getBytes());
        } catch (Exception e) {
            return null;
        }
    }

    private static DateTime headerFormat2Datetime(String dateByHeader) {
        DateTimeFormatter HEADER_FORMAT = DateTimeFormat.forPattern("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'z").
                withLocale(Locale.US).withZone(DateTimeZone.forID("Etc/GMT"));
        DateTimeZone CST = DateTimeZone.forID("Asia/Shanghai");
        DateTime dateTime;
        try {
            dateTime = HEADER_FORMAT.parseDateTime(dateByHeader);
        } catch (IllegalArgumentException e) {
            dateTime = HEADER_FORMAT.parseDateTime(dateByHeader.replaceAll("'", " "));
        }
        return dateTime.withZone(CST);
    }

    private static int getPartnerIdInHeader(String authorization) {
        Pattern p = Pattern.compile("^" + "MWS" + "\\s(\\d+):\\S+$");
        Matcher matcher = p.matcher(authorization);
        if (matcher.matches()) {
            return Integer.valueOf(matcher.group(1));
        }
        return 0;
    }

}
