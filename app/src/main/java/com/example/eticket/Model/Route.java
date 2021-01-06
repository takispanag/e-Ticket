package com.example.eticket.Model;

import java.io.Serializable;

public class Route implements Serializable {
    private String date;
    private String routeKey;
    private String sp3Selection;

    public Route(String date, String routeKey, String sp3Selection) {
        this.date = date;
        this.routeKey = routeKey;
        this.sp3Selection = sp3Selection;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public String getSp3Selection() {
        return sp3Selection;
    }

    public void setSp3Selection(String sp3Selection) {
        this.sp3Selection = sp3Selection;
    }

    @Override
    public String toString() {
        return date +" "+ routeKey + " " +sp3Selection;
    }
}
