package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.CustomUserDetails;
import ru.kata.spring.boot_security.demo.service.Userservice;


@Controller
@RequestMapping("/user")
public class UserController {

    private final Userservice service;
    private final Userservice userservice;

    @Autowired
    public UserController(Userservice service, Userservice userservice) {
        this.service = service;
        this.userservice = userservice;
    }

    @GetMapping
    public String goUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getId();
        model.addAttribute("user", userservice.show(userId));
        return "user";
    }
}
