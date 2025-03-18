package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;
import java.util.Set;

public interface Userservice {
    List<User> usersList();
    void addUser(User user);
    void refactorUser(int id, User userUpdate);
    void deleteUser(int id);
    User show(int id);
    List<Role> getAllRoles();
    Set<Role> getRolesByIds(List<Integer> roleIds);
    boolean isEmailUnique(String email);
    boolean isOtherEmailUnique(String email, int userId);
    public String encodePassword(String password);
    User findByEmail(String email);
}
