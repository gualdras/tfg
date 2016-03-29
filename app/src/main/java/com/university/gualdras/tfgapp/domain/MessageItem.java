package com.university.gualdras.tfgapp.domain;

/**
 * Created by gualdras on 11/10/15.
 */
public class MessageItem {

    private int id, from;
    private String msg, dateTime;

    public MessageItem(int number, String msg, String dataTime) {
        this.from = number;
        this.msg = msg;
        this.dateTime = dataTime;
    }

    public MessageItem(String msg, String dateTime){
        this.msg= msg;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int number) {
        this.from = number;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
