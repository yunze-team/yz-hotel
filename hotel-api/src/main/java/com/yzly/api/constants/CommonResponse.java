package com.yzly.api.constants;

public class CommonResponse<M extends Serial,T> {

    public static final String OK = "0";
    public static final String OK_MESSAGE = "sucessful";
    public static final String ERROR = "100";
    public static final String ERROR_MESSAGE = "there is something wrong whit system";

    private M journal;
    private T data;

    public CommonResponse() {
        super();
    }

    public CommonResponse(T data) {
        this.data = data;
    }

    public CommonResponse(M journal) {
        this.journal = journal;
    }

    public CommonResponse(M journal, T data) {
        this.journal = journal;
        this.data = data;
    }

    public Serial getJournal() {
        return journal;
    }

    public void setJournal(M journal) {
        this.journal = journal;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "journal=" + journal.toString() +
                ", data=" + (data == null?"":data.toString()) +
                '}';
    }
}
