package com.example.mainapi.controller;

import com.example.mainapi.client.ProductClient;
import com.example.mainapi.dto.CreateBasketRequestDTO;
import com.example.mainapi.dto.ProductDTO;

import com.example.mainapi.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductClient productClient;

    @Autowired
    public ProductController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @GetMapping("/isProduct")
    public boolean areProdcutsInStock(@RequestParam String pName, @RequestParam int pQuantity) {
        return productClient.isProductInStock(pName, pQuantity);
    }

    @GetMapping("/getAll")
    public void getProducts() {
        List<Product> products = productClient.getProducts();
        for (Product product : products) {
            System.out.println(product);
        }
    }

    @PostMapping("/add")
    public boolean addProduct(@RequestBody Product product) {
        boolean result = productClient.addProduct(product);

        if (result) {
            System.out.println("Success");
        } else System.out.println("Not Success");

        return result;
    }

    public boolean deleteProduct(Product product) {
        return productClient.deleteProduct(product.getpName());
    }

    public boolean changeQuantity(String pName, int pQuantity) {
        return productClient.changeQuantity(pName, pQuantity);
    }
    public List<Product> createBasket(CreateBasketRequestDTO createBasketRequestDTO){
        return productClient.createBasket(createBasketRequestDTO);
    }


}
