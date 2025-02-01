package com.example.mainapi.controller;

import com.example.mainapi.dto.*;
import com.example.mainapi.kafka.KafkaProducer;
import com.example.mainapi.model.CustomerOrders;
import com.example.mainapi.model.Order;
import com.example.mainapi.model.Product;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1/buy")
public class MainAction {

    private final KafkaProducer kafkaProducer;
    private final OrderController orderController;
    private final CustomerController customerController;
    CompletableFuture<ExecuteOrderResult> customerResponse = new CompletableFuture<>();
    CompletableFuture<ExecuteOrderResult> productRespone = new CompletableFuture<>();

    public MainAction(KafkaProducer kafkaProducer){
        this.kafkaProducer = kafkaProducer;
        this.orderController = new OrderController(kafkaProducer);
        this.customerController = new CustomerController(kafkaProducer);
    }


    public void buyWithOutBox(int oNumber) throws ExecutionException, InterruptedException, TimeoutException {
        System.out.println("Control1");
//        CustomerOrders customerOrders =
                orderController.getCustomerOrder(oNumber);
        System.out.println("Control2");

//        System.out.println("Have I ProductList?");
//        List<Product> products = customerOrderToProductsList(customerOrders);
//        System.out.println("ProductList: " + products);
//
//        System.out.println("Are enough credits in customer wallet?");
//        CIDwithValue ciDwithValue = new CIDwithValue(customerOrders.getcID(), customerOrders.getoValue());
//        boolean areCredits = customerController.areCreditsInWallet(ciDwithValue);
//        System.out.println("Are? " + areCredits);

    }


    private List<Product> customerOrderToProductsList(CustomerOrders customerOrders) throws ExecutionException, InterruptedException {
        List<Order> orders = orderController.consumerOrdersToOrderList(customerOrders);
        List<Product> products = new ArrayList<>();
        for (Order o : orders) {
            Product product = new Product();
            product.setPName(o.getpName());
            product.setPPrice(o.getpPrice());
            product.setPQuantity(o.getpQuantity());
            products.add(product);
        }
        return products;
    }

    @RequestMapping("/test")
    public void test() throws ExecutionException, InterruptedException, TimeoutException {
        buyWithOutBox(82714239);
    }


}
