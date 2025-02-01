package com.example.mainapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CIDwithValue implements Serializable {
    @JsonProperty("cID")
    private String cID;
    @JsonProperty("changeValue")
    private double changeValue;

    public CIDwithValue(String cID, double changeValue) {
        this.cID = cID;
        this.changeValue = changeValue;
    }

    public CIDwithValue() {
    }
}
