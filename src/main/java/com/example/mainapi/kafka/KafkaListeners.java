package com.example.mainapi.kafka;

import com.example.mainapi.controller.CustomerController;
import com.example.mainapi.controller.MainAction;
import com.example.mainapi.controller.OrderController;
import com.example.mainapi.dto.CustomerWalletDTO;
import com.example.mainapi.model.CustomerOrders;
import com.example.mainapi.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaListeners {
    ObjectMapper objectMapper = new ObjectMapper();

    private KafkaProducer kafkaProducer;
    MainAction mainAction = new MainAction(kafkaProducer);
    CustomerController customerController = new CustomerController(kafkaProducer);
    OrderController orderController = new OrderController(kafkaProducer);

    public KafkaListeners(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @KafkaListener(topics = "customerMainWalletResponse", groupId = "customerMain")
    public void customerWalletResponse(ConsumerRecord<String, Object> data) throws JsonProcessingException {
        CustomerWalletDTO customerWalletDTO = objectMapper.readValue(data.value().toString(), CustomerWalletDTO.class);
//        mainAction.;
    }

    @KafkaListener(topics = "productMainCheckProducts", groupId = "productMain")
    public void areProductsAvailableResponse(ConsumerRecord<String, Object> data) throws JsonProcessingException {
        boolean response = objectMapper.readValue(data.value().toString(), Boolean.class);
    }

    @KafkaListener(topics = "orderMainOrdersToListResponse", groupId = "orderMain")
    public void getOrderList(ConsumerRecord<String, Object> data) throws JsonProcessingException {
        List<Order> orderList = objectMapper.readValue(data.value().toString(), ArrayList.class);
    }

    @KafkaListener(topics = "customerMainCreditsResponse", groupId = "customerMain")
    public void areCreditsInWalletResponse(ConsumerRecord<String, Object> data) throws JsonProcessingException {
        boolean response = objectMapper.readValue(data.value().toString(), Boolean.class);
        customerController.areCreditInWalletRespone(response);
    }
    @KafkaListener(topics = "orderMainCustomerOrdersRespone", groupId = "orderMain")
    public void getCustomerByONumberespone(ConsumerRecord<String, Object> data) throws JsonProcessingException, ExecutionException, InterruptedException {
        System.out.println("getCustomerByONumberespone1");
        CustomerOrders customerOrders = objectMapper.readValue(data.value().toString(), CustomerOrders.class);
        System.out.println("getCustomerByONumberespone2");
        orderController.setCustomerOrdersCompletableFuture(customerOrders);
        System.out.println("getCustomerByONumberespone3");

    }

    @KafkaListener(topics = "test1", groupId = "test")
    public void test1(ConsumerRecord<String, Object>data) throws JsonProcessingException {
        kafkaProducer.receiveProductResponse();

    }
    @KafkaListener(topics = "test2", groupId = "test")
    public void test2(ConsumerRecord<String, Object>data) throws JsonProcessingException {
        kafkaProducer.receiveCustomerResponse();

    }

}
