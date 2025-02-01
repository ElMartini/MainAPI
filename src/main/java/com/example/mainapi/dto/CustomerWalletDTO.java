package com.example.mainapi.dto;

import com.example.mainapi.model.CustomerWallet;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerWalletDTO implements Serializable {

    @JsonProperty("cID")
    private String cID;
    @JsonProperty("customerWallet")
    private CustomerWallet customerWallet;
}
