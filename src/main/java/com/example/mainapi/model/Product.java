package com.example.mainapi.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product implements Serializable {
    @JsonProperty("pID")
    private String pID;

    @JsonProperty("pName")
    private String pName;

    @JsonProperty("pQuantity")
    private int pQuantity;

    @JsonProperty("pPrice")
    private double pPrice;


    public Product(String pID, String pName, int pQuantity, double pPrice) {
        this.pID = pID;
        this.pName = pName;
        this.pQuantity = pQuantity;
        this.pPrice = pPrice;
    }
    public Product(){}

    @Override
    public String toString() {
        return "Product{" +
                "pID='" + pID + '\'' +
                ", pName='" + pName + '\'' +
                ", pQuantity=" + pQuantity +
                ", pPrice=" + pPrice +
                '}';
    }

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
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

    public double getpPrice() {
        return pPrice;
    }

    public void setpPrice(double pPrice) {
        this.pPrice = pPrice;
    }
}
