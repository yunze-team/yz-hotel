package com.yzly.api.constants.xiecheng;


import com.yzly.api.annotations.*;

/**
 *@ClassName AppHead
 *@Description TODO
 *@Auth frank
 *@Date 2019-11-14 17:25
 *@Version 1.0
 **/
@Struct
public class AppHeader {
    @Fields(length = 12, scale = 0)
    private String pageEnd;

    @Fields(length = 12, scale = 0)
    private String currentNum;

    @Fields(length = 12, scale = 0)
    private String pgUpOrPgDn;

    @Fields(length = 12, scale = 0)
    private String pageStart;

    @Fields(length = 12, scale = 0)
    private String totalNum;

    @Fields(length = 12, scale = 0)
    private String totalFlag;

    public AppHeader() {
    }

    public AppHeader(long currentNum, long totalNum) {
        this.currentNum = (currentNum*totalNum)+"";
        this.pgUpOrPgDn = "1";
        this.totalNum = totalNum+"";
        this.totalFlag = "E";
        this.pageStart = ((currentNum * totalNum) + 1) + "";
        this.pageEnd = ((currentNum + 1) * totalNum) + "";
    }

    public AppHeader(long currentNum, long totalNum, String pgUpOrPgDn) {
        this.currentNum = currentNum+"";
        this.pgUpOrPgDn = pgUpOrPgDn;
        this.totalNum = totalNum+"";
        this.totalFlag = "E";
        this.pageStart = ((currentNum * totalNum) + 1) + "";
        this.pageEnd = ((currentNum + 1) * totalNum) + "";
    }

    public AppHeader(long currentNum, long totalNum, String pgUpOrPgDn, String totalFlag) {
        this.currentNum = currentNum+"";
        this.pgUpOrPgDn = pgUpOrPgDn;
        this.totalNum = totalNum+"";
        this.totalFlag = totalFlag;
        this.pageStart = ((currentNum * totalNum) + 1) + "";
        this.pageEnd = ((currentNum + 1) * totalNum) + "";
    }

    public String getTotalFlag() {
        return totalFlag;
    }

    public void setTotalFlag(String totalFlag) {
        this.totalFlag = totalFlag;
    }

    public String getPageEnd() {
        return pageEnd;
    }

    public void setPageEnd(String pageEnd) {
        this.pageEnd = pageEnd;
    }

    public String getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(String currentNum) {
        this.currentNum = currentNum;
    }

    public String getPgUpOrPgDn() {
        return pgUpOrPgDn;
    }

    public void setPgUpOrPgDn(String pgUpOrPgDn) {
        this.pgUpOrPgDn = pgUpOrPgDn;
    }

    public String getPageStart() {
        return pageStart;
    }

    public void setPageStart(String pageStart) {
        this.pageStart = pageStart;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }
}
