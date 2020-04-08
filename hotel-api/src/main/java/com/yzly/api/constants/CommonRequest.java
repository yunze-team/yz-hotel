package com.yzly.api.constants;/**
 * @Description:TODO
 * @Auther frank
 * version V1.0
 * @createtime 2019-11-13 18:02
 **/

import javax.validation.Valid;

/**
 *@ClassName CommonRequest
 *@Description TODO
 *@Auth frank
 *@Date 2019-11-13 18:02
 *@Version 1.0
 **/
public class CommonRequest<M extends Serial, T> {
    @Valid
    private M journal;
    @Valid
    private T data;

    public CommonRequest(@Valid M journal, @Valid T data) {
        this.journal = journal;
        this.data = data;
    }

    public CommonRequest() {
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
        return "CommonRequest{" +
                journal.toString() +'\'' +
                (data == null?"":data.toString()) + '\'' +
                '}';
    }
}
