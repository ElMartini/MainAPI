package com.example.mainapi.dto;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public class CreateBasketRequestDTO {


    private List<String> pNames;
    private List<Integer> pQuantities;

    public List<String> getpNames() {
        return pNames;
    }

    public void setpNames(List<String> pNames) {
        this.pNames = pNames;
    }

    public List<Integer> getpQuantities() {
        return pQuantities;
    }

    public void setpQuantities(List<Integer> pQuantities) {
        this.pQuantities = pQuantities;
    }
}
