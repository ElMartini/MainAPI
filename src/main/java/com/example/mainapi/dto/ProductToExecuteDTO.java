package com.example.mainapi.dto;

import com.example.mainapi.model.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductToExecuteDTO implements Serializable {

    @JsonProperty("products")
    private List<Product> products;

    @JsonProperty("cID")
    private String cID;
}
