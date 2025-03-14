package com.example.mainapi.controller;

import com.example.mainapi.client.CustomerClient;
import com.example.mainapi.dto.CIDwithValue;
import com.example.mainapi.dto.CustomerWalletDTO;
import com.example.mainapi.model.ActionStatus;
import com.example.mainapi.model.Customer;
import feign.FeignException;
import feign.RetryableException;
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

    public boolean changeWalletValue(CIDwithValue ciDwithValue, String actionID) throws InterruptedException {
        try {
            return customerClient.updateWallet(ciDwithValue, actionID);
        } catch (RetryableException e) {
            System.out.println("Timeout error: " + e.getMessage());
            ActionStatus status = getSingleAction(actionID);
            return timeoutExceptionHandler(status);
        } catch (FeignException e) {
            System.out.println("FeignException: Other Feign error");
            return false;
        }
    }

    public boolean areCreditsInWallet(CIDwithValue ciDwithValue) {
        return customerClient.areCreditsInWallet(ciDwithValue);
    }

    public boolean createNewCustomer(Customer customer) {
        return customerClient.addCustomer(customer);
    }

    public ActionStatus getSingleAction(String actionID) {
        return customerClient.getSingleAction(actionID);
    }

    private boolean timeoutExceptionHandler(ActionStatus actionStatus) {
        if (actionStatus == null) {
            return false;
        } else return actionStatus.getStatus().equals("SUCCESS");
    }
}
