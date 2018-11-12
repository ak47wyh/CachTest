package com.example.demo;

import java.io.Serializable;

public class Cost implements Serializable {

    private String uscnt;
    private String usCost;
    private String desc;

    public String getUscnt() {
        return uscnt;
    }

    public void setUscnt(String uscnt) {
        this.uscnt = uscnt;
    }

    public String getUsCost() {
        return usCost;
    }

    public void setUsCost(String usCost) {
        this.usCost = usCost;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Cost{" +
                "uscnt=" + uscnt +
                ", usCost=" + usCost +
                ", desc='" + desc + '\'' +
                '}';
    }
}
