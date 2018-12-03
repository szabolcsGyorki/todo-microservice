package com.codecool.microservice.todo.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "todo_owner")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @OneToMany
    private List<Todo> todoList = new ArrayList<>();

    public User() {}

    public User(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Todo> getTodoList() {
        return todoList;
    }

    public void setTodoList(List<Todo> todoList) {
        this.todoList = todoList;
    }

    public void addTodo(Todo todo) {
        todoList.add(todo);
    }
}
