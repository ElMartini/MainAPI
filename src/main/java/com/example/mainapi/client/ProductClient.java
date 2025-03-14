package com.example.mainapi.client;


import com.example.mainapi.configuration.FeignConfig;
import com.example.mainapi.dto.CreateBasketRequestDTO;
import com.example.mainapi.model.ActionStatus;
import com.example.mainapi.model.Product;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "product", url = "http://localhost:8081", configuration = FeignConfig.class)
public interface ProductClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/products/isInStock")
    boolean isProductInStock(@RequestParam String pName, @RequestParam int pQuantity);

    @RequestMapping(method = RequestMethod.GET, value = "/api/products/all")
    @Tag(name = "GET", description = "Get methods")
    List<Product> getProducts();

    @RequestMapping(method = RequestMethod.POST, value = "/api/products/add")
    boolean addProduct(@RequestBody Product product);

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/products/delete/{pName}")
    boolean deleteProduct(@PathVariable String pName);

    @RequestMapping(method = RequestMethod.PUT, value = "/api/products/update")
    boolean updateProduct(@RequestBody Product product);

    @RequestMapping(method = RequestMethod.GET, value = "/api/products/select")
    List<Product> selectProducts(@RequestBody List<String> productNames);

    @RequestMapping(method = RequestMethod.POST, value = "/api/products/changeQuantity")
    boolean changeQuantity(@RequestBody List<Product> products, @RequestParam String actionID);

    @RequestMapping(method = RequestMethod.POST, value = "/api/products/createBasket")
    List<Product> createBasket(@RequestBody CreateBasketRequestDTO createBasketRequestDTO);

    @RequestMapping(method = RequestMethod.PUT, value = "/api/productAction/getSingleAction")
    ActionStatus getSingleAction(@RequestParam String actionID);

}