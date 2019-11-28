package com.yzly.api.util.meit;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpRequestBase;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BaseAuthorizationUtils {

    public static void generateAuthAndDateHeader(HttpRequestBase request, String partnerId, String secret) {
        Date sysdate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("EEE\', \'dd\' \'MMM\' \'yyyy\' \'HH:mm:ss\' \'z", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = df.format(sysdate);
        String string_to_sign = request.getMethod().toUpperCase() + " " + request.getURI().getPath() + "\n" + date;
        String sig = secret;
        String encoding = "";

        try {
            byte[] authorization = getSignature(string_to_sign.getBytes(), sig.getBytes());
            encoding = new String(Base64.encodeBase64(authorization));
        } catch (Exception var10) {
            return;
        }

        String authorization1 = "MWS " + partnerId + ":" + encoding;
        request.addHeader("Authorization", authorization1);
        request.addHeader("Date", date);
    }

    public static byte[] getSignature(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException {
        SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data);
        return rawHmac;
    }
}
