package ru.kata.spring.boot_security.demo.models;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(String role);
}