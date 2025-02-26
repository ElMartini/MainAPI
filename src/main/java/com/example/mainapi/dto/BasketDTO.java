package com.example.mainapi.dto;

import com.example.mainapi.model.Product;

import java.util.List;

public class BasketDTO {
    private List<Product> products;
    private String cID;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getcID() {
        return cID;
    }

    public void setcID(String cID) {
        this.cID = cID;
    }
}
