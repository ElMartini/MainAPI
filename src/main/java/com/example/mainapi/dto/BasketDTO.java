package com.example.mainapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasketDTO {
    @JsonProperty("pNames")
    private List<String> pNames;
    @JsonProperty("pQuantity")
    private List<Integer> pQuantity;

    @JsonProperty("cID")
    private String cID;

    public String getcID() {
        return cID;
    }

    public void setcID(String cID) {
        this.cID = cID;
    }

    public List<String> getpNames() {
        return pNames;
    }

    public void setpNames(List<String> pNames) {
        this.pNames = pNames;
    }

    public List<Integer> getpQuantity() {
        return pQuantity;
    }

    public void setpQuantity(List<Integer> pQuantity) {
        this.pQuantity = pQuantity;
    }
}
