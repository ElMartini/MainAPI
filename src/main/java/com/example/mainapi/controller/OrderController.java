package com.example.mainapi.controller;

import com.example.mainapi.dto.BasketDTO;
import com.example.mainapi.kafka.KafkaProducer;
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

    private final KafkaProducer kafkaProducer;

    public OrderController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    private CompletableFuture<List<Order>> orderList = new CompletableFuture<>();
    private ConcurrentHashMap<Integer, CompletableFuture<CustomerOrders>> futuresMap = new ConcurrentHashMap<>();

    @RequestMapping("/send")
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
        basketDTO.setpNames(tempNames);
        basketDTO.setpQuantity(tempQuantity);
        basketDTO.setcID("d9cc1797-8ed3-4129-a22b-8c83d28f24be");

        kafkaProducer.sendBasket(basketDTO);
    }

    public List<Order> consumerOrdersToOrderList(CustomerOrders customerOrders) throws ExecutionException, InterruptedException {
        kafkaProducer.customerOrdersToOrderListRequest(customerOrders);

        CompletableFuture.allOf(orderList).join();
        System.out.println(orderList.get());

        return orderList.get();
    }

    public void getOrderList(List<Order> orderList) {
        this.orderList.complete(orderList);
    }

    public CustomerOrders getCustomerOrder(int oNumber) throws ExecutionException, InterruptedException, TimeoutException {
        System.out.println("getCustomerOrder1 - oNumber: " + oNumber);

        CompletableFuture<CustomerOrders> future = new CompletableFuture<>();
        futuresMap.put(oNumber, future);

        System.out.println("Future added to map for oNumber: " + oNumber);

        kafkaProducer.getCustomerOrdersByONumber(oNumber);

        System.out.println("getCustomerOrder2");

        CustomerOrders customerOrders = future.get(10, TimeUnit.SECONDS);
        System.out.println("getCustomerOrder3");

        futuresMap.remove(oNumber);

        return customerOrders;
    }

    public void setCustomerOrdersCompletableFuture(CustomerOrders customerOrders) {
        int oNumber = customerOrders.getoNumber();
        System.out.println("setCustomerOrdersCompletableFuture - oNumber: " + oNumber);

        CompletableFuture<CustomerOrders> future = futuresMap.get(oNumber);
        if (future != null) {
            future.complete(customerOrders);
            System.out.println("Completed future for order number: " + oNumber);
        } else {
            System.out.println("No future found for order number: " + oNumber);
        }
    }


}
