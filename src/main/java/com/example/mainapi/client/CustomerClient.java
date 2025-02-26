package com.example.mainapi.client;

import com.example.mainapi.dto.CIDwithValue;
import com.example.mainapi.model.Customer;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "customer", url = "http://localhost:8083")
public interface CustomerClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/customers/getAll")
    @Tag(name = "GET", description = "Get methods")
    List<Customer> getCustomers();

    @RequestMapping(method = RequestMethod.GET, value = "/api/customers/getSingle/{cEmail}")
    @Tag(name = "GET", description = "Get methods")
    Customer getSignleCustomer(@PathVariable String cEmail);

    @RequestMapping(method = RequestMethod.POST, value = "/api/customers/add")
    boolean addCustomer(@RequestBody Customer customer);

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/customers/delete/{cEmail}")
    boolean deleteCustomer(@PathVariable String cEmail);

    @RequestMapping(method = RequestMethod.PUT, value = "/api/customers/update")
    boolean updateCustomer(@RequestBody Customer customer);

    @RequestMapping(method = RequestMethod.PUT, value = "/api/customersWallet/update")
    boolean updateWallet(@RequestBody CIDwithValue ciDwithValue);

    @RequestMapping(method = RequestMethod.POST, value = "/api/customersWallet/areCredits")
    boolean areCreditsInWallet(@RequestBody CIDwithValue ciDwithValue);
}

