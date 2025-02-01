package com.example.mainapi.controller;

import com.example.mainapi.dto.ProductDTO;
import com.example.mainapi.dto.ProductToExecuteDTO;
import com.example.mainapi.kafka.KafkaProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/p")
public class ProductController {
    final KafkaProducer kafkaProducer;
    CompletableFuture<Boolean> areProductsAvailabe = new CompletableFuture<>();

    public ProductController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processProduct(@RequestBody ProductDTO productDTO) {
        kafkaProducer.sendProductsActions(productDTO);
        return ResponseEntity.ok("Action processed: ");
    }

    public boolean areProductsAvailable(ProductToExecuteDTO productToExecuteDTO) throws ExecutionException, InterruptedException {
        kafkaProducer.sendAreProductsAvailableRequest(productToExecuteDTO);
        CompletableFuture.allOf(areProductsAvailabe).join();

        return areProductsAvailabe.get();
    }

    public void setAreProductsAvailabeResponse(boolean response){
        areProductsAvailabe.complete(response);
    }



}
