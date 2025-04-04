package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.RoleRepository;
import ru.kata.spring.boot_security.demo.models.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements Userservice {

    private final UserDao userDao;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> usersList() {
        return userDao.usersList();
    }

    @Override
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.addUser(user);
    }

    @Override
    public void refactorUser(int id, User userUpdate) {
        if (userUpdate.getPassword() != null) {
            userUpdate.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
        }
        userDao.refactorUser(id, userUpdate);
    }

    @Override
    public void deleteUser(int id) {
        userDao.deleteUser(id);
    }

    @Override
    public User show(int id) {
        return userDao.show(id);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Set<Role> getRolesByIds(List<Integer> roleIds) {
        return new HashSet<>(roleRepository.findAllById(roleIds));
    }

    @Override
    public boolean isEmailUnique(String email) {
        return userDao.findByEmail(email) == null;
    }

    @Override
    public boolean isOtherEmailUnique(String email, int userId) {
        User existsUser = userDao.findByEmail(email);
        return userDao.findByEmail(email) == null || existsUser.getId() == userId;
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }
}
