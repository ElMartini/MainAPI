package com.example.mainapi.controller;

import com.example.mainapi.client.CustomerClient;
import com.example.mainapi.dto.CIDwithValue;
import com.example.mainapi.dto.CustomerWalletDTO;

import com.example.mainapi.model.Customer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/c")
public class CustomerController {

    private final CustomerClient customerClient;

    public CustomerController(CustomerClient customerClient) {
        this.customerClient = customerClient;
    }

    @RequestMapping("/sendWalletRequest")
    public void sendCustomerWalletRequset(CustomerWalletDTO customerWalletDTO) {
    }
    public boolean changeWalletValue(CIDwithValue ciDwithValue) {
        return customerClient.updateWallet(ciDwithValue);
    }

    public boolean areCreditsInWallet(CIDwithValue ciDwithValue) {
        return customerClient.areCreditsInWallet(ciDwithValue);
    }

    public boolean createNewCustomer(Customer customer){
        return  customerClient.addCustomer(customer);
    }
}
