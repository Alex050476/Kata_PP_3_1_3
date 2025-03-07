package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.Userservice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final Userservice service;
    private final Userservice userservice;

    @Autowired
    public AdminController(Userservice service, Userservice userservice) {
        this.service = service;
        this.userservice = userservice;
    }

    @GetMapping
    public String showUsers(Model model) {
        model.addAttribute("users", service.usersList());
        return "list";
    }

    @GetMapping("/ref")
    public String ref(@RequestParam("id") int id, Model model) {
        User user = service.show(id);
        user.setPassword("");
        List<Role> allRoles = service.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "ref";
    }

    @GetMapping("/userAdd")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", userservice.getAllRoles());
        return "userAdd";
    }

    @PostMapping
    public String add(@ModelAttribute("user") @Valid User user,
                      BindingResult result,
                      @RequestParam(value = "roles", required = false) List<Integer> roleIds,
                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", userservice.getAllRoles());
            return "userAdd";
        }
        if (!userservice.isEmailUnique(user.getEmail())) {
            model.addAttribute("emailError", "This email address is already exists!");
            model.addAttribute("allRoles", userservice.getAllRoles());
            return "userAdd";
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            model.addAttribute("passwordError", "Please enter a password!");
            model.addAttribute("allRoles", userservice.getAllRoles());
            return "userAdd";
        }
        if (roleIds != null) {
            Set<Role> selectedRoles = new HashSet<>(userservice.getRolesByIds(roleIds));
            user.setRoleSet(selectedRoles);
        }
        userservice.addUser(user);
        return "redirect:/admin";
    }

    @PatchMapping("/user")
    public String update (@ModelAttribute("user") @Valid User user,
                          BindingResult result,
                          @RequestParam("id") int id,
                          @RequestParam(value = "roles", required = false) List<Integer> roleIds,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", userservice.getAllRoles());
            return "ref";
        }
        if (!userservice.isOtherEmailUnique(user.getEmail(), id)) {
            model.addAttribute("emailError", "Email is already exists!");
            model.addAttribute("allRoles", userservice.getAllRoles());
            return "ref";
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            User existsUser = userservice.show(id);
            user.setPassword(existsUser.getPassword());
        }
        Set<Role> selectedRoles = roleIds != null
                ? service.getRolesByIds(roleIds)
                : new HashSet<>();
        user.setRoleSet(selectedRoles);
        userservice.refactorUser(id, user);
        return "redirect:/admin";
    }

    @DeleteMapping("/user")
    public String delete(@RequestParam("id") int id) {
        userservice.deleteUser(id);
        return "redirect:/admin";
    }
}
