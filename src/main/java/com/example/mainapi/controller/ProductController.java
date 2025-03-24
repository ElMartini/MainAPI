package com.example.mainapi.controller;

import com.example.mainapi.client.ProductClient;
import com.example.mainapi.dto.CreateBasketRequestDTO;

import com.example.mainapi.model.ActionStatus;
import com.example.mainapi.model.Product;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
public class ProductController {

    private final ProductClient productClient;

    public ProductController(ProductClient productClient) {
        this.productClient = productClient;
    }

    public boolean areProdcutsInStock(@RequestParam String pName, @RequestParam int pQuantity) {
        return productClient.isProductInStock(pName, pQuantity);
    }


    public boolean addProduct(Product product) {
        boolean result = productClient.addProduct(product);

        if (result) {
            System.out.println("Success");
        } else System.out.println("Not Success");

        return result;
    }

    public boolean deleteProduct(Product product) {
        return productClient.deleteProduct(product.getpName());
    }

    public boolean changeQuantity(List<Product> products, String actionID) throws InterruptedException {
        try {
            return productClient.changeQuantity(products, actionID);
        } catch (RetryableException e) {
            System.out.println("Timeout error: " + e.getMessage());
            Thread.sleep(3000);
            ActionStatus status = getSingleAction(actionID);
            return timeoutExceptionHandler(status);
        } catch (FeignException e) {
            System.out.println("FeignException: " + e.getMessage());
        }
        return false;
    }

    public List<Product> createBasket(CreateBasketRequestDTO createBasketRequestDTO) {
        return productClient.createBasket(createBasketRequestDTO);
    }

    public ActionStatus getSingleAction(String actionID) {
        return productClient.getSingleAction(actionID);
    }

    private boolean timeoutExceptionHandler(ActionStatus actionStatus) {
        if (actionStatus == null) {
            return false;
        } else return actionStatus.getStatus().equals("SUCCESS");
    }

}