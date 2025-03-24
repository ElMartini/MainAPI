package com.example.mainapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@RequestParam(value = "message", required = false) String message, Model model) {
        if (message != null) {
            model.addAttribute("message", message);
        }
        return "index";
    }
}
