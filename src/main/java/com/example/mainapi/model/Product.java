package com.example.mainapi.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
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

    @Override
    public String toString() {
        return "Product{" +
                "pID='" + pID + '\'' +
                ", pName='" + pName + '\'' +
                ", pQuantity=" + pQuantity +
                ", pPrice=" + pPrice +
                '}';
    }
}
