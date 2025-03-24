package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.CustomUserDetails;
import ru.kata.spring.boot_security.demo.service.Userservice;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final Userservice service;
    private final Userservice userservice;

    @Autowired
    public AdminController(Userservice service, Userservice userservice) {
        this.service = service;
        this.userservice = userservice;
    }

    @GetMapping
    public String showUsers(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getId();
        model.addAttribute("currentUser", userservice.show(userId));
        model.addAttribute("users", service.usersList());
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", userservice.getAllRoles());
        return "admin";
    }

    @GetMapping("/ref")
    public String ref(@RequestParam("id") int id, Model model) {
        User user = service.show(id);
        user.setPassword("");
        List<Role> allRoles = service.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "admin";
    }

    @PostMapping
    public String add(@ModelAttribute("user") @Valid User user,
                      BindingResult result,
                      @RequestParam(value = "roles", required = false) List<Integer> roleIds,
                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", userservice.getAllRoles());
            return "admin"; // Возвращаем на ту же страницу с ошибками
        }
        if (!userservice.isEmailUnique(user.getEmail())) {
            model.addAttribute("emailError", "This email address is already exists!");
            model.addAttribute("allRoles", userservice.getAllRoles());
            return "admin"; // Возвращаем на ту же страницу с ошибкой
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            model.addAttribute("passwordError", "Please enter a password!");
            model.addAttribute("allRoles", userservice.getAllRoles());
            return "admin"; // Возвращаем на ту же страницу с ошибкой
        }
        if (roleIds != null) {
            Set<Role> selectedRoles = new HashSet<>(userservice.getRolesByIds(roleIds));
            user.setRoleSet(selectedRoles);
        }
        userservice.addUser(user);
        return "redirect:/admin"; // Перенаправляем на ту же страницу после успешного добавления
    }

    @PatchMapping("/user")
    public String update(@ModelAttribute("user") @Valid User user,
                         BindingResult result,
                         @RequestParam("id") int id,
                         @RequestParam(value = "roles", required = false) List<Integer> roleIds,
                         Model model) {

        logger.info("Updating user with ID: {}", id);
        logger.info("User data: {}", user);
        logger.info("Roles: {}", roleIds);

        // Логика обновления пользователя
        if (result.hasErrors()) {
            model.addAttribute("allRoles", userservice.getAllRoles());
            model.addAttribute("error", true);
        }

        // Обновление полей пользователя
        User existingUser = userservice.show(id);
        existingUser.setName(user.getName());
        existingUser.setAge(user.getAge());
        existingUser.setEmail(user.getEmail());
        existingUser.setNickname(user.getNickname());

        //Обновление пароля
        if (user.getPassword() != null || !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }

        // Обновление ролей
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> selectedRoles = new HashSet<>(userservice.getRolesByIds(roleIds));
            existingUser.setRoleSet(selectedRoles);

        } else {
            logger.warn("Roles are null or empty for user with ID: {}", id); // Логирование, если роли не переданы
            existingUser.setRoleSet(new HashSet<>()); // Устанавливаем пустой набор ролей
        }
        userservice.refactorUser(id, existingUser);
        return "redirect:/admin";
    }

    @DeleteMapping("/user")
    public String delete(@RequestParam("id") int id) {
        userservice.deleteUser(id);
        return "redirect:/admin";
    }
}