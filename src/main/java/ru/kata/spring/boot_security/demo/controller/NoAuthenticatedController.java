package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.service.CustomUserDetails;
import ru.kata.spring.boot_security.demo.service.Userservice;

@Controller
@RequestMapping
public class NoAuthenticatedController {

    private final Userservice service;
    private final Userservice userservice;

    public NoAuthenticatedController(Userservice service, Userservice userservice) {
        this.service = service;
        this.userservice = userservice;
    }

    @GetMapping("/")
    public String goIndex(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getId();
        model.addAttribute("user", userservice.show(userId));
        return "index";
    }

    @GetMapping("/index")
    public String goAdmin() {
        return "index";
    }
}
