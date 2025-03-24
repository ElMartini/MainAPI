package com.example.mainapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


public class CIDwithValue implements Serializable {
    private String cID;
    private double changeValue;

    public CIDwithValue(String cID, double changeValue) {
        this.cID = cID;
        this.changeValue = changeValue;
    }

    public CIDwithValue() {
    }

    public String getcID() {
        return cID;
    }

    public void setcID(String cID) {
        this.cID = cID;
    }

    public double getChangeValue() {
        return changeValue;
    }

    public void setChangeValue(double changeValue) {
        this.changeValue = changeValue;
    }
}
