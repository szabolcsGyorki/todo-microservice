package com.codecool.microservice.todo.repository;

import com.codecool.microservice.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
