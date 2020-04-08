package com.yzly.api.constants;/**
 * @Description:TODO
 * @Auther frank
 * version V1.0
 * @createtime 2019-11-14 17:25
 **/

import com.yzly.api.annotations.*;
import com.yzly.api.util.CommonUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 *@ClassName SysHeader
 *@Description TODO
 *@Auth frank
 *@Date 2019-11-14 17:25
 *@Version 1.0
 **/
@Struct
public class SysHeader {

    @Data("SERVICE_CODE")
    @Fields(length = 11)
    private String serviceCode;
    @Data("SERVICE_SCENE")
    @Fields(length = 2)
    private String serviceScene;
    @Data("MESSAGE_TYPE")
    @Fields(length = 12)
    private String messageType;
    @Data("MESSAGE_CODE")
    @Fields(length = 12)
    private String messageCode;
    @Data("TRAN_CODE")
    @Fields(length = 8)
    private String transCode;
    @Data("TRAN_MODE")
    @Fields(length = 10)
    private String tranMode;
    @Data("SOURCE_TYPE")
    @Fields(length = 2)
    private String sourceType;
    @Data("BRANCH_ID")
    @Fields(length = 6)
    private String branchId;
    @Data("USER_ID")
    @Fields(length = 30)
    private String userId;
    @Data("TRAN_DATE")
    @Fields(length = 8)
    private String tranDate;
    @Data("TRAN_TIMESTAMP")
    @Fields(length = 9)
    private String tranTimestamp;
    @Data("SERVER_ID")
    @Fields(length = 30)
    private String serverId;
    @Data("WS_ID")
    @Fields(length = 30)
    private String wsId;
    @Data("CONSUMER_ID")
    @Fields(length = 3)
    private String ConsumerId;
    @Data("SEQ_NO")
    @Fields(length = 32)
    private String seqNo;
    @Data("SOURCE_CONSUMER_ID")
    @Fields(length = 6)
    private String sourceConsumerId;
    @Data("BUSS_SEQ_NO")
    @Fields(length = 50)
    private String bussSeqNo;
    @Data("SOURCE_BRANCH_NO")
    @Fields(length = 6)
    private String destBranchNo;
    @Data("MODULE_ID")
    @Fields(length = 2)
    private String moduleId;
    @Data("USER_LANG")
    @Fields(length = 20)
    private String userLang;

    public String getUserLang() {
        return userLang;
    }

    public void setUserLang(String userLang) {
        this.userLang = userLang;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceScene() {
        return serviceScene;
    }

    public void setServiceScene(String serviceScene) {
        this.serviceScene = serviceScene;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getTranMode() {
        return tranMode;
    }

    public void setTranMode(String tranMode) {
        this.tranMode = tranMode;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTranDate() {
        return tranDate;
    }

    public void setTranDate(String tranDate) {
        this.tranDate = tranDate;
    }

    public String getTranTimestamp() {
        return tranTimestamp;
    }

    public void setTranTimestamp(String tranTimestamp) {
        this.tranTimestamp = tranTimestamp;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getWsId() {
        return wsId;
    }

    public void setWsId(String wsId) {
        this.wsId = wsId;
    }

    public String getConsumerId() {
        return ConsumerId;
    }

    public void setConsumerId(String consumerId) {
        ConsumerId = consumerId;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getSourceConsumerId() {
        return sourceConsumerId;
    }

    public void setSourceConsumerId(String sourceConsumerId) {
        this.sourceConsumerId = sourceConsumerId;
    }

    public String getBussSeqNo() {
        return bussSeqNo;
    }

    public void setBussSeqNo(String bussSeqNo) {
        this.bussSeqNo = bussSeqNo;
    }

    public String getDestBranchNo() {
        return destBranchNo;
    }

    public void setDestBranchNo(String destBranchNo) {
        this.destBranchNo = destBranchNo;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * 提供生存一个默认值系统请求头对象
     * @return
     */
    public static SysHeader getNewDefaultSysHeader() {
        SysHeader sysHeader = new SysHeader();
        sysHeader.setTranMode("ONLINE");
        sysHeader.setTranDate(CommonUtil.dateFormat(LocalDate.now()));
        sysHeader.setTranTimestamp(CommonUtil.timeFormat(LocalTime.now()));
        sysHeader.setUserLang("CHINESE");
        sysHeader.setServerId("");
        sysHeader.setWsId("");
        return sysHeader;
    }
}
