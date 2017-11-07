package com.cxy.hans.Bean;

/**
 * Created by hasee on 2017/11/6.
 */
public class HansBean {
    private int value;
    private String time;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public HansBean(int value, String time) {
        this.value = value;
        this.time = time;
    }
}
