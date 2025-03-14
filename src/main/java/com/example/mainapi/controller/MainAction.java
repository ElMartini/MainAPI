package com.example.mainapi.controller;

import com.example.mainapi.dto.CIDwithValue;
import com.example.mainapi.model.*;
import feign.FeignException;
import lombok.SneakyThrows;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.*;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Controller
@RequestMapping("/api/v1")
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

        String actionID = createActionID();
        System.out.println(actionID);

        outBoxOperations(customerOrders, products, actionID);

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


    private void outBoxOperations(CustomerOrders customerOrders, List<Product> products, String actionID) {

        String productStatus, customerStatus;
        productStatus = getStatus(actionID, true);
        customerStatus = getStatus(actionID, false);

        if (productStatus == null && customerStatus == null) {
            outBoxAction(customerOrders, products, actionID);
        }


    }

    public void outBoxAction(CustomerOrders customerOrders, List<Product> products, String actionID) {
        CIDwithValue walletValue = new CIDwithValue(customerOrders.getcID(), -customerOrders.getoValue());

        CompletableFuture<Boolean> productsActions;
        CompletableFuture<Boolean> walletAction;
        boolean productBasic;
        boolean walletBasic;
        try {
            productsActions = changeProductQuantity(products, actionID);
            walletAction = changeWalletValue(walletValue,actionID);
            CompletableFuture.allOf(productsActions, walletAction).join();

            boolean productResult = productsActions.get();
            boolean walletResult = walletAction.get();
            System.out.println("Products: " + productResult);
            System.out.println("Wallet: " + walletResult);
            productBasic = !productResult;
            walletBasic = !walletResult;

            while (walletBasic != productBasic) {
                System.out.println("Rollback");
                try {
                    if (!productBasic) {
                        productBasic = productRollback(products, actionID);
                    }
                    if (!walletBasic) {
                        walletBasic = walletRollback(walletValue,actionID);
                    }

                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e);
                }

            }


            if (productBasic) {
                outBoxAction(customerOrders, products, actionID);
            }



        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
        }
        System.out.println("Everything OK !!!");

    }


    boolean productRollback(List<Product> products, String actionID) throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> productsActionsRollback = changeProductQuantityRollback(products, actionID);

        CompletableFuture.allOf(productsActionsRollback).join();

        return (productsActionsRollback.get());
    }

    boolean walletRollback(CIDwithValue ciDwithValue, String actionID) throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> walletActionRollback = changeWalletValueRollback(ciDwithValue,actionID);

        CompletableFuture.allOf(walletActionRollback).join();

        return (walletActionRollback.get());
    }

    @Async
    public CompletableFuture<Boolean> changeProductQuantity(List<Product> products, String actionID) throws InterruptedException {
        Thread.sleep(5000);
        for (Product p : products) {
            p.setpQuantity(-p.getpQuantity());
        }
        try {
            boolean isCompleted = productController.changeQuantity(products, actionID);

            return CompletableFuture.completedFuture(isCompleted);
        } catch (
                FeignException e) {
            System.err.println("Feign Client Error: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }

    }

    @Async
    public CompletableFuture<Boolean> changeWalletValue(CIDwithValue ciDwithValue, String actionID) throws InterruptedException {
        Thread.sleep(5000);
        try {
            return CompletableFuture.completedFuture(customerController.changeWalletValue(ciDwithValue, actionID));
        } catch (FeignException e) {
            System.err.println("Feign Client Error: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async
    public CompletableFuture<Boolean> changeProductQuantityRollback(List<Product> products, String actionID) throws InterruptedException {
        try {
            boolean isCompleted = productController.changeQuantity(products, actionID);

            return CompletableFuture.completedFuture(isCompleted);
        } catch (
                FeignException e) {
            System.err.println("Feign Client Error: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }

    }

    @Async
    public CompletableFuture<Boolean> changeWalletValueRollback(CIDwithValue ciDwithValue, String actionID) {
        ciDwithValue.setChangeValue(-ciDwithValue.getChangeValue());
        try {
            return CompletableFuture.completedFuture(customerController.changeWalletValue(ciDwithValue, actionID));
        } catch (FeignException e) {
            System.err.println("Feign Client Error: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String createActionID() {
        String actionID;
        String productStatus;
        String customerStatus;
        do {
            actionID = UUID.randomUUID().toString();
            productStatus = getStatus(actionID, true);
            customerStatus = getStatus(actionID, false);
        } while (productStatus != null && customerStatus != null);

        return actionID;
    }

    private String getStatus(String actionID, boolean checkProduct) {
        ActionStatus actionStatus;

        if (checkProduct) {
            actionStatus = productController.getSingleAction(actionID);
        } else {
            actionStatus = customerController.getSingleAction(actionID);
        }

        if (actionStatus != null) return actionStatus.getStatus();
        else return null;
    }

    @PostMapping("/testOutbox")
    public String testOutbox(RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException, TimeoutException {
        buyWithOutBox(88718789, "fe604abf-e35d-4eda-b5bd-44e1dfcc225b");
        redirectAttributes.addFlashAttribute("message", "Outbox Test Completed!");
        return "redirect:/";
    }

    @PostMapping("/testBasket")
    public String testCreateBasket(RedirectAttributes redirectAttributes) {
        orderController.createBasket();
        redirectAttributes.addFlashAttribute("message", "Basket Created!");
        return "redirect:/";
    }

    @PostMapping("/testAddProduct")
    public String testAddProduct(RedirectAttributes redirectAttributes) {
        Product product = new Product();
        product.setpName("TestProduct");
        product.setpPrice(19.99);
        product.setpQuantity(100);

        productController.addProduct(product);
        redirectAttributes.addFlashAttribute("message", "Product Added!");
        return "redirect:/";
    }

    @PostMapping("/testAddCustomer")
    public String testCreateCustomer(RedirectAttributes redirectAttributes) {
        Customer customer = new Customer();
        customer.setcFirstName("Jan");
        customer.setcLastName("Kowalski");
        customer.setcEmail("jan.kowalski@gmail.com");
        customer.setcPassword("haslo123");

        customerController.createNewCustomer(customer);
        redirectAttributes.addFlashAttribute("message", "Customer Added!");
        return "redirect:/";
    }

    @PostMapping("/testTimeOut")
    public String timeOutTest(RedirectAttributes redirectAttributes) throws InterruptedException {
        CIDwithValue ciDwithValue = new CIDwithValue();
        ciDwithValue.setChangeValue(-1000);
        ciDwithValue.setcID("fe604abf-e35d-4eda-b5bd-44e1dfcc225b");
        String actionID = "fe604abf-e35d-4eda-b5bd-44e1dfcc225b";

        System.out.println(customerController.changeWalletValue(ciDwithValue, actionID));
        redirectAttributes.addFlashAttribute("message", "Timeout Test Completed!");
        return "redirect:/";
    }

}