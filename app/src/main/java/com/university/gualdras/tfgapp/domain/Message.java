package com.university.gualdras.tfgapp.domain;

/**
 * Created by gualdras on 11/10/15.
 */
public class Message {

    private int id, mobileNumber;
    private String msg, dataTime;

    public Message(int number, String msg, String dataTime) {
        this.mobileNumber = number;
        this.msg = msg;
        this.dataTime = dataTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(int number) {
        this.mobileNumber = number;
    }

    public String getMsgContent() {
        return msg;
    }

    public void setMsgContent(String msg) {
        this.msg = msg;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
}
