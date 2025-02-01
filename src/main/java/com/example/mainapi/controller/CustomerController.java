package com.example.mainapi.controller;

import com.example.mainapi.dto.CIDwithValue;
import com.example.mainapi.dto.CustomerWalletDTO;
import com.example.mainapi.kafka.KafkaProducer;
import com.example.mainapi.model.CustomerOrders;
import com.example.mainapi.model.CustomerWallet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/c")
public class CustomerController {

    private final KafkaProducer kafkaProducer;

    CompletableFuture<Boolean> areCreditInWalletDTO = new CompletableFuture<>();

    public CustomerController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @RequestMapping("/sendWalletRequest")
    public void sendCustomerWalletRequset(CustomerWalletDTO customerWalletDTO) {
        kafkaProducer.sendCustomerWalletRequest(customerWalletDTO);
    }


    public boolean areCreditsInWallet(CIDwithValue ciDwithValue) throws ExecutionException, InterruptedException {

        kafkaProducer.areCreditsInWalletRequest(ciDwithValue);
        CompletableFuture.allOf(areCreditInWalletDTO).join();

        return areCreditInWalletDTO.get();
    }

    public void areCreditInWalletRespone(boolean response) {
        areCreditInWalletDTO.complete(response);
    }
}
