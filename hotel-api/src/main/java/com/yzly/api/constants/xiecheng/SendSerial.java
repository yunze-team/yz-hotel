package com.yzly.api.constants.xiecheng;

/**
 * <p>
 * 
 * </p>
 *
 * @author chenlei
 * @since 2019-11-27
 * @description　 发送流水，调用esbhttp请求时需要记录的流水信息
 */
public class SendSerial extends Serial{

    private static final long serialVersionUID = 1L;

    /**
     * 流水号
     */
    private String serialNo;
    /**
     * 发送esb请求流水号
     **/
    private String sendSerialNo;

    /**
     * 服务提供方
     */
    private String serviceCode;

    /**
     * 交易码
     */
    private String tranCode;

    /**
     * 服务方流水号
     */
    private String providerSerialNo;


    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getProviderSerialNo() {
        return providerSerialNo;
    }

    public void setProviderSerialNo(String providerSerialNo) {
        this.providerSerialNo = providerSerialNo;
    }

    public String getSendSerialNo() {
        return sendSerialNo;
    }

    public void setSendSerialNo(String sendSerialNo) {
        this.sendSerialNo = sendSerialNo;
    }

    @Override
    public String toString() {
        return "SendSerial{" +
                "serialNo='" + serialNo + '\'' +
                ", sendSerialNo='" + sendSerialNo + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", tranCode='" + tranCode + '\'' +
                ", providerSerialNo='" + providerSerialNo + '\'' +
                '}';
    }
}
