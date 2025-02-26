package com.example.mainapi.controller;

import com.example.mainapi.dto.CIDwithValue;
import com.example.mainapi.model.Customer;
import com.example.mainapi.model.CustomerOrders;
import com.example.mainapi.model.Order;
import com.example.mainapi.model.Product;
import feign.FeignException;
import lombok.SneakyThrows;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/v1")
@Retryable(
        value = {SQLException.class, ConnectException.class},
        maxAttempts = 5,
        backoff = @Backoff(delay = 5000))
public class MainAction {


    private final ProductController productController;
    private final OrderController orderController;
    private final CustomerController customerController;

    public MainAction(ProductController productController, OrderController orderController, CustomerController customerController) {
        this.productController = productController;
        this.orderController = orderController;
        this.customerController = customerController;
    }


    public boolean buyWithOutBox(int oNumber, String cID) throws ExecutionException, InterruptedException, TimeoutException {
        CustomerOrders customerOrders = orderController.getCustomerOrder(oNumber);
        System.out.println(customerOrders);

        List<Product> products = customerOrderToProductsList(customerOrders);
        boolean areProductsInStockFlag = true;
        for (Product p : products) {
            System.out.println(p);

            boolean areProductsInStock = productController.areProdcutsInStock(p.getpName(), p.getpQuantity());
            if (!areProductsInStock) {
                areProductsInStockFlag = false;
                System.out.println(":C");
            }
        }
        if (areProductsInStockFlag) {
            System.out.println("All good");
        } else {
            System.out.println("Thats baaddd");
            return false;
        }

        for (String oID : customerOrders.getoIDs().split(", ")) {
            orderController.showOrder(oID);
        }
        System.out.println("Value: " + customerOrders.getoValue());

        CIDwithValue ciDwithValue = new CIDwithValue(cID, customerOrders.getoValue());
        if (!customerController.areCreditsInWallet(ciDwithValue)) {
            System.out.println("There is no money");
            return false;
        } else System.out.println("$$$");

        outBoxAction(customerOrders, products);

        return true;
    }


    private List<Product> customerOrderToProductsList(CustomerOrders customerOrders) throws ExecutionException, InterruptedException {
        List<Order> orders = orderController.consumerOrderToOrderList(customerOrders);
        List<Product> products = new ArrayList<>();
        for (Order o : orders) {
            Product product = new Product();
            product.setpName(o.getpName());
            product.setpPrice(o.getpPrice());
            product.setpQuantity(o.getpQuantity());
            products.add(product);
        }
        return products;
    }




    private void outBoxAction(CustomerOrders customerOrders, List<Product> products) {

        CIDwithValue walletValue = new CIDwithValue(customerOrders.getcID(), -customerOrders.getoValue());

        CompletableFuture<Boolean> productsActions = null;
        CompletableFuture<Boolean> walletAction = null;
        try {
            productsActions = changeProductQuantity(products);
            walletAction = changeWalletValue(walletValue);
            CompletableFuture.allOf(productsActions, walletAction).join();
        } catch (InterruptedException e) {
            System.out.println(e);
        }



        boolean productBasic = true;
        boolean walletBasic = true;
        try {
            System.out.println("Products: " + productsActions.get());
            System.out.println("Wallet: " + walletAction.get());
            if (productsActions.get())
                productBasic = false;
            if (walletAction.get())
                walletBasic = false;

        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
        }


        while (walletBasic != productBasic) {
            System.out.println("Rollback");
            try {
                Thread.sleep(2000);
                if (walletBasic) {
                    productBasic = productRollback(products);
                } else {
                    walletBasic = walletRollback(walletValue);
                }
                System.out.println("After rollback");
                Thread.sleep(2000);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e);
            }

        }

        if (walletBasic && productBasic) outBoxAction(customerOrders, products);
        else System.out.println("Everythings ok!!!");


    }

    boolean productRollback(List<Product> products) throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> productsActionsRollback = changeProductQuantityRollback(products);

        CompletableFuture.allOf(productsActionsRollback).join();

        return (productsActionsRollback.get());
    }

    boolean walletRollback(CIDwithValue ciDwithValue) throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> walletActionRollback = changeWalletValueRollback(ciDwithValue);

        CompletableFuture.allOf(walletActionRollback).join();

        return (walletActionRollback.get());
    }

    @Async
    public CompletableFuture<Boolean> changeProductQuantity(List<Product> products) throws InterruptedException {
        Thread.sleep(5000);
        try {
            boolean isCompleted = true;
            for (Product p : products) {
                if (!productController.changeQuantity(p.getpName(), -p.getpQuantity())) {
                    isCompleted = false;
                }
            }
            return CompletableFuture.completedFuture(isCompleted);
        } catch (
                FeignException e) {
            System.err.println("Feign Client Error: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }

    }

    @Async
    public CompletableFuture<Boolean> changeWalletValue(CIDwithValue ciDwithValue) throws InterruptedException {
        try {
            return CompletableFuture.completedFuture(customerController.changeWalletValue(ciDwithValue));
        } catch (FeignException e) {
            System.err.println("Feign Client Error: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async
    public CompletableFuture<Boolean> changeProductQuantityRollback(List<Product> products) throws InterruptedException {
        try {
            boolean isCompleted = true;
            for (Product p : products) {
                if (!productController.changeQuantity(p.getpName(), p.getpQuantity())) {
                    isCompleted = false;
                }
            }
            return CompletableFuture.completedFuture(isCompleted);
        } catch (
                FeignException e) {
            System.err.println("Feign Client Error: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }

    }

    @Async
    public CompletableFuture<Boolean> changeWalletValueRollback(CIDwithValue ciDwithValue) {
        ciDwithValue.setChangeValue(-ciDwithValue.getChangeValue());
        try {
            return CompletableFuture.completedFuture(customerController.changeWalletValue(ciDwithValue));
        } catch (FeignException e) {
            System.err.println("Feign Client Error: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    @RequestMapping("/testOutbox")
    public void testOutbox() throws ExecutionException, InterruptedException, TimeoutException {
        buyWithOutBox(88718789, "fe604abf-e35d-4eda-b5bd-44e1dfcc225b");}

    @RequestMapping("/testBasket")
    public void testCreateBasket(){
                orderController.createBasket();
    }

    @RequestMapping("/testAddProduct")
    public void testAddProduct(){
        Product product= new Product();
        product.setpName("TestProduct");
        product.setpPrice(19.99);
        product.setpQuantity(100);

        productController.addProduct(product);
    }

    @RequestMapping("testAddCustomer")
    public void testCreateCustomer(){

        Customer customer = new Customer();
        customer.setcFirstName("Jan");
        customer.setcLastName("Kowalski");
        customer.setcEmail("jan.kowalski@gmail.com");
        customer.setcPassword("haslo123");

        customerController.createNewCustomer(customer);
    }
}
