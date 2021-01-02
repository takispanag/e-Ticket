package com.example.eticket.Model;

import android.text.format.DateFormat;

import java.io.Serializable;
import java.util.Date;

public class Route implements Serializable {
    private Date date;
    private String routeKey;
    private String sp3Selection;

    public Route(Date date, String routeKey, String sp3Selection) {
        this.date = date;
        this.routeKey = routeKey;
        this.sp3Selection = sp3Selection;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
        return DateFormat.format("dd-MM-yyyy", new Date(date.getTime())).toString() +" "+ routeKey + " " +sp3Selection;
    }
}
