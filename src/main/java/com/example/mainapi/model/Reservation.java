package com.example.mainapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Reservation implements Serializable {
    @JsonProperty("rID")
    private String rID;

    @JsonProperty("cID")
    private String cID;

    @JsonProperty("pName")
    private String pName;

    @JsonProperty("pQuantity")
    private int pQuantity;

    public String getrID() {
        return rID;
    }

    public void setrID(String rID) {
        this.rID = rID;
    }

    public String getcID() {
        return cID;
    }

    public void setcID(String cID) {
        this.cID = cID;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public int getpQuantity() {
        return pQuantity;
    }

    public void setpQuantity(int pQuantity) {
        this.pQuantity = pQuantity;
    }
}
