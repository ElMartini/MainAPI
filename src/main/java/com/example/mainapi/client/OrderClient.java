package com.example.mainapi.client;


import com.example.mainapi.configuration.FeignConfig;
import com.example.mainapi.dto.BasketDTO;
import com.example.mainapi.model.CustomerOrders;
import com.example.mainapi.model.Order;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "order", url = "http://localhost:8082", configuration = FeignConfig.class)
public interface OrderClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/orders/all")
    @Tag(name = "GET", description = "Get methods")
    List<Order> getOrder();

    @RequestMapping(method = RequestMethod.POST, value = "/api/orders/add")
    boolean addOrder(@RequestBody Order order);


    @RequestMapping(method = RequestMethod.GET, value = "/api/customerOrders/all")
    List<CustomerOrders> getCustomerOrders();

    @RequestMapping(method = RequestMethod.POST, value = "/api/customerOrders/add")
    boolean addCustomerOrders(@RequestBody CustomerOrders customerOrders);

    @RequestMapping(method = RequestMethod.GET, value = "/api/customerOrders/getSingleOrder")
    CustomerOrders getSingleCustomerOrder(@RequestParam int oNumber);

    @RequestMapping(method = RequestMethod.POST, value = "/api/customerOrders/toOrderList")
    List<Order> customerOrderToOrderList(@RequestBody CustomerOrders customerOrders);

    @RequestMapping(method = RequestMethod.POST, value = "/api/customerOrders/createOrderFromBasket")
    boolean createOrderFromBasket(@RequestBody BasketDTO basketDTO);

    @RequestMapping(method = RequestMethod.GET, value = "/api/orders/showOrder")
    boolean showOrder(@RequestParam String oID);


}
