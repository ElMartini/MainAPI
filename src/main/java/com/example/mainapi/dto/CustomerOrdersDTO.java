package com.example.mainapi.dto;

import com.example.mainapi.model.CustomerOrders;
import com.example.mainapi.model.Order;
import com.example.mainapi.model.Product;
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
public class CustomerOrdersDTO implements Serializable {
    @JsonProperty("action")
    private String action;
    @JsonProperty("customerOrders")
    private CustomerOrders customerOrders;
}
