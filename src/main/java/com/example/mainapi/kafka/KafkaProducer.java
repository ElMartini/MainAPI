package com.example.mainapi.kafka;

import com.example.mainapi.dto.*;
import com.example.mainapi.model.CustomerOrders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@Component
@RestController
@RequestMapping("/api")
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;


    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    private  CompletableFuture<String> productResponse = new CompletableFuture<>();
    private  CompletableFuture<String> customerResponse = new CompletableFuture<>();

    @RequestMapping("/send/tr")
    public void test() throws InterruptedException, ExecutionException {
        kafkaTemplate.send("test", "trala");
        kafkaTemplate.send("test3", "trala");
        CompletableFuture.allOf(productResponse, customerResponse).join();
        System.out.println("hehe");
        System.out.println("pro"+productResponse.get());
        System.out.println("cus"+customerResponse.get());
    }

    public void receiveProductResponse() {

        productResponse.complete("hihi");
    }

    public void receiveCustomerResponse() {
        customerResponse.complete("hohoho");
    }


    public void sendToProductService(@RequestBody ProductDTO productDTO) {
        kafkaTemplate.send("kafkaProduct", productDTO);
    }


    public void sendBasket(BasketDTO basketDTO) {
        kafkaTemplate.send("mainProductBasket", basketDTO);
    }

    public void sendCustomerWalletRequest(CustomerWalletDTO customerWalletDTO) {
        kafkaTemplate.send("mainCustomerWalletRequest", customerWalletDTO);
    }

    public void sendExecuteOrderToCustomerService(CIDwithValue ciDwithValue){
        kafkaTemplate.send("mainCustomerExecuteOrder", ciDwithValue);
    }
    public void sendExecuteOrderToProductService(CIDwithValue ciDwithValue){
        kafkaTemplate.send("mainProductExecuteOrder", ciDwithValue);
    }
    public void sendAreProductsAvailableRequest(ProductToExecuteDTO productToExecuteDTO){
        kafkaTemplate. send("mainProductCheckProducts", productToExecuteDTO);
    }

    public void sendProductsActions(ProductDTO productDTO){
        kafkaTemplate.send("kafkaProduct", productDTO);
    }

    public void customerOrdersToOrderListRequest(CustomerOrders customerOrders){
        kafkaTemplate.send("mainOrderOrdersToListRequest", customerOrders);
    }

    public void areCreditsInWalletRequest(CIDwithValue ciDwithValue){
        kafkaTemplate.send("mainCustomerCreditsRequest", ciDwithValue);
    }
    public void getCustomerOrdersByONumber(int oNumber){
        kafkaTemplate.send("mainOrderCustomerOrdersRequest", oNumber);
    }


}
