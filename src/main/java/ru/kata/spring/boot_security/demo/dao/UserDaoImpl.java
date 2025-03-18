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

    @Transactional
    @Override
    public void addUser(User user) {
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
        user.setNickname(userUpdate.getNickname());
        user.setAge(userUpdate.getAge());
        user.setEmail(userUpdate.getEmail());
        if (userUpdate.getPassword() != null && !userUpdate.getPassword().trim().isEmpty()) {
            user.setPassword(userUpdate.getPassword());
        }
        user.getRoleSet().clear();
        Set<Role> newRoles = userUpdate.getRoleSet();
        if (newRoles != null) {
            for (Role role : newRoles) {
                Role managedRole = entityManager.find(Role.class, role.getId());
                if (managedRole != null) {
                    user.getRoleSet().add(managedRole);
                }
            }
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