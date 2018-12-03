package com.codecool.microservice.todo.controller;

import com.codecool.microservice.todo.model.Todo;
import com.codecool.microservice.todo.model.User;
import com.codecool.microservice.todo.repository.TodoRepository;
import com.codecool.microservice.todo.repository.UserRepository;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.PUT})
@RestController
public class TodoController {

    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    @Autowired
    public TodoController(UserRepository userRepository, TodoRepository todoRepository) {
        this.userRepository = userRepository;
        this.todoRepository = todoRepository;
    }

    @GetMapping("/todo/{name}")
    public String getAllTodos(@PathVariable("name") String name) {
        if (!userRepository.existsByName(name)) {
            userRepository.save(new User(name));
            userRepository.flush();
        }
        User user = userRepository.findByName(name);
        Gson gson = new Gson();
        if (user.getTodoList().size() == 0) {
            return gson.toJson(new String[0]);
        }
        return gson.toJson(user.getTodoList());
    }

    @PutMapping("/todo/{name}/{id}/complete")
    public String completeTodo(@PathVariable("name") String name, @PathVariable("id") Long id) {
        Gson gson = new Gson();
        if (!todoRepository.existsById(id)) {
            return gson.toJson(Arrays.asList("There is no todo with that id!"));
        }
        Todo todo = todoRepository.findById(id).get();
        todo.setCompleted(true);
        todoRepository.flush();

        User user = userRepository.findByName(name);
        if (null == user || user.getTodoList().size() == 0) {
            return gson.toJson(new String[0]);
        }
        return gson.toJson(user.getTodoList());
    }

    @PostMapping("/todo/{name}/{title}")
    public String addTodo(@PathVariable("name") String name, @PathVariable("title") String title) {
        if (!userRepository.existsByName(name)) {
            userRepository.save(new User(name));
            userRepository.flush();
        }
        User user = userRepository.findByName(name);
        Gson gson = new Gson();
        title = StringEscapeUtils.unescapeHtml3(title);
        Todo todo = new Todo(title);
        todoRepository.save(todo);
        user.addTodo(todo);
        userRepository.flush();

        return gson.toJson(user.getTodoList());
    }
}
