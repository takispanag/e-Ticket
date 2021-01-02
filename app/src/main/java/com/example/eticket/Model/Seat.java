package com.example.eticket.Model;

import java.io.Serializable;

public class Seat implements Serializable {
    private String id;
    private boolean isTaken;

    public Seat(String id, boolean isTaken) {
        this.id = id;
        this.isTaken = isTaken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}
