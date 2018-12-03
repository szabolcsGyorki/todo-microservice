package com.codecool.microservice.todo.repository;

import com.codecool.microservice.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByName(String name);
    Boolean existsByName(String name);
}
