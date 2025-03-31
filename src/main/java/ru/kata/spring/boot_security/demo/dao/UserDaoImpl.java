package ru.kata.spring.boot_security.demo.dao;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import java.util.List;
import java.util.Set;

@Component
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void addUser(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        entityManager.persist(user);
    }

    @Override
    @Transactional
    public void refactorUser(int id, User userUpdate) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setName(userUpdate.getName());
        user.setAge(userUpdate.getAge());
        user.setEmail(userUpdate.getEmail());
        user.setNickname(userUpdate.getNickname());

        if (userUpdate.getPassword() != null && !userUpdate.getPassword().isEmpty()) {
            user.setPassword(userUpdate.getPassword());
        }

        if (userUpdate.getRoleSet() != null) {
            user.setRoleSet(userUpdate.getRoleSet());
        }

        entityManager.merge(user);
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        Query query = entityManager.createQuery("DELETE from User u WHERE u.id= :id");
        query.setParameter("id", id);
        int c = query.executeUpdate();
    }

    @Override
    public List<User> usersList() {
        return entityManager.createQuery("FROM User", User.class).getResultList();
    }

    @Override
    public User show(int id) {
        TypedQuery<User> query = entityManager.createQuery("from User WHERE id= :id", User.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public User findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}