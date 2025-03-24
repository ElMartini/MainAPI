package com.example.mainapi.controller;

import com.example.mainapi.client.OrderClient;
import com.example.mainapi.dto.BasketDTO;

import com.example.mainapi.dto.CreateBasketRequestDTO;
import com.example.mainapi.model.CustomerOrders;
import com.example.mainapi.model.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api/o")
public class OrderController {
    private final OrderClient orderClient;
    private final ProductController productController;

    public OrderController(OrderClient orderClient, ProductController productController) {
        this.orderClient = orderClient;
        this.productController = productController;
    }


    public void createBasket() {
        List<String> tempNames = new ArrayList<>();
        List<Integer> tempQuantity = new ArrayList<>();
        tempNames.add("a");
        tempNames.add("b");
        tempNames.add("c");
        tempQuantity.add(31);
        tempQuantity.add(5);
        tempQuantity.add(54);

        BasketDTO basketDTO = new BasketDTO();
        CreateBasketRequestDTO createBasketRequestDTO = new CreateBasketRequestDTO();
        createBasketRequestDTO.setpNames(tempNames);
        createBasketRequestDTO.setpQuantities(tempQuantity);
        basketDTO.setProducts(productController.createBasket(createBasketRequestDTO));
        basketDTO.setcID("fe604abf-e35d-4eda-b5bd-44e1dfcc225b");

        if (createOrderFromBasket(basketDTO)) System.out.println("Success");

    }


    public CustomerOrders getCustomerOrder(int oNumber) throws ExecutionException, InterruptedException, TimeoutException {
        return orderClient.getSingleCustomerOrder(oNumber);
    }

    public List<Order> consumerOrderToOrderList(CustomerOrders customerOrders) {
        return orderClient.customerOrderToOrderList(customerOrders);
    }

    public boolean createOrderFromBasket(BasketDTO basketDTO) {
        return orderClient.createOrderFromBasket(basketDTO);
    }
    public void showOrder(String oID){
        orderClient.showOrder(oID);
    }


}
