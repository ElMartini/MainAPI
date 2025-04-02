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

        List<Product> products = customerOrderToProductsList(customerOrders);
        boolean areProductsInStockFlag = true;
        for (Product p : products) {

            boolean areProductsInStock = productController.areProdcutsInStock(p.getpName(), p.getpQuantity());
            if (!areProductsInStock) {
                areProductsInStockFlag = false;
            }
        }
        if (!areProductsInStockFlag) {
            return false;
        }


        CIDwithValue ciDwithValue = new CIDwithValue(cID, customerOrders.getoValue());
        if (!customerController.areCreditsInWallet(ciDwithValue)) {
            return false;
        }

        String actionID = createActionID();
        boolean correctID;
        do {
            correctID = checkID(actionID);
            if (!correctID) {
                actionID = createActionID();
            }
        } while (!correctID);
        outBoxAction(customerOrders, products, actionID);
        return true;
    }


    private List<Product> customerOrderToProductsList(CustomerOrders customerOrders){
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


    private boolean checkID(String actionID) {

        String productStatus, customerStatus;
        productStatus = getStatus(actionID, true);
        customerStatus = getStatus(actionID, false);

        if (productStatus == null && customerStatus == null) {
            return true;
        }

        return false;
    }

    public void outBoxAction(CustomerOrders customerOrders, List<Product> products, String actionID) {
        CIDwithValue walletValue = new CIDwithValue(customerOrders.getcID(), -customerOrders.getoValue());

        CompletableFuture<Boolean> productsActions;
        CompletableFuture<Boolean> walletActions;
        boolean productBasic;
        boolean walletBasic;
        try {
            productsActions = changeProductQuantity(products, actionID);
            walletActions = changeWalletValue(walletValue, actionID);
            CompletableFuture.allOf(productsActions, walletActions).join();

            boolean productResult = productsActions.get();
            boolean walletResult = walletActions.get();
            productBasic = !productResult;
            walletBasic = !walletResult;

            while (walletBasic != productBasic) {
                try {
                    if (!productBasic) {
                        boolean rollbackSuccess = productRollback(products, actionID);
                        if (!rollbackSuccess) {
                            System.err.println("Product rollback failed. Exiting rollback.");
                        }
                        productBasic = rollbackSuccess;
                    }
                    if (!walletBasic) {
                        boolean rollbackSuccess = walletRollback(walletValue, actionID);
                        if (!rollbackSuccess) {
                            System.err.println("Wallet rollback failed. Exiting rollback.");
                        }
                        walletBasic = rollbackSuccess;
                    }

                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e);
                }

            }



                if (productBasic && walletBasic) {
                    outBoxAction(customerOrders, products, actionID);
                }



        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
        }
        System.out.println("Everything OK !!!");

    }


    boolean productRollback(List<Product> products, String actionID) throws ExecutionException, InterruptedException {
        System.out.println("Rollback Product Base");

        CompletableFuture<Boolean> productsActionsRollback = changeProductQuantityRollback(products, actionID);

        CompletableFuture.allOf(productsActionsRollback).join();

        boolean rollbackSuccess = productsActionsRollback.get();
        if (!rollbackSuccess) {
            System.err.println("Product rollback failed. Exiting rollback.");
        }
        return rollbackSuccess;
    }

    boolean walletRollback(CIDwithValue ciDwithValue, String actionID) throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> walletActionRollback = changeWalletValueRollback(ciDwithValue, actionID);

        CompletableFuture.allOf(walletActionRollback).join();

        return (walletActionRollback.get());
    }

    @Async
    public CompletableFuture<Boolean> changeProductQuantity(List<Product> products, String actionID) throws InterruptedException {
        System.out.println("Action Product");
        Thread.sleep(5000);


        List<Product> tmpProducts = new ArrayList<>();
        for (Product p : products) {
            tmpProducts.add(new Product(p.getpID(), p.getpName(), -p.getpQuantity(), p.getpPrice()));
        }

        try {
            boolean isCompleted = productController.changeQuantity(tmpProducts, actionID, false);
            System.out.println(isCompleted);
            tmpProducts.clear();
            return CompletableFuture.completedFuture(isCompleted);
        } catch (
                FeignException e) {
            System.err.println("Feign Client Error: " + e.getMessage());
            System.out.println("error");
            return CompletableFuture.completedFuture(false);
        }

    }

    @Async
    public CompletableFuture<Boolean> changeWalletValue(CIDwithValue ciDwithValue, String actionID) throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("Action Wallet");

        try {
            boolean isCompleted = customerController.changeWalletValue(ciDwithValue, actionID);
            System.out.println(isCompleted);
            return CompletableFuture.completedFuture(isCompleted);
        } catch (FeignException e) {
            System.out.println("error");
            System.err.println("Feign Client Error: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    @Async
    public CompletableFuture<Boolean> changeProductQuantityRollback(List<Product> products, String actionID) throws InterruptedException {
        System.out.println("Rollback Product");
        try {
            boolean isCompleted = productController.changeQuantity(products, actionID, true);
            System.out.println(isCompleted);
            return CompletableFuture.completedFuture(isCompleted);
        } catch (
                FeignException e) {
            System.err.println("Feign Client Error: " + e.getMessage());
            return CompletableFuture.completedFuture(false);
        }

    }

    @Async
    public CompletableFuture<Boolean> changeWalletValueRollback(CIDwithValue ciDwithValue, String actionID) {
        System.out.println("Rollback Wallet");

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