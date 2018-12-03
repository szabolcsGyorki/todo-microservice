package com.codecool.microservice.todo;

import com.codecool.microservice.todo.model.Todo;
import com.codecool.microservice.todo.model.User;
import com.codecool.microservice.todo.repository.TodoRepository;
import com.codecool.microservice.todo.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TodoApplicationTests {

    private User user;
    private Todo todoOne;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Before
    public void init() {
        user = new User("Hank");
        todoOne = new Todo("first");
    }

    @Test
    public void whenFindUserByName_thenReturnUser() {
        userRepository.save(user);
        userRepository.flush();

        User found = userRepository.findByName(user.getName());
        assertThat(found.getName()).isEqualTo(user.getName());
    }

    @Test
    public void whenFindTodoById_thanReturnTodos() {
        todoRepository.save(todoOne);
        todoRepository.flush();

        Todo foundFirst = todoRepository.findById(todoOne.getId()).get();
        assertThat(foundFirst.getTitle()).isEqualTo(todoOne.getTitle());
    }

    @Test
    public void whenFindUserByNameAndHasTodo_thenReturnUserWithTodo() {
        user.addTodo(todoOne);
        todoRepository.save(todoOne);
        userRepository.save(user);

        User found = userRepository.findByName(user.getName());
        List<Todo> foundTodos = found.getTodoList();
        assertThat(foundTodos.get(0).getTitle()).isEqualTo(todoOne.getTitle());
    }

    @Test
    public void whenFindUserByNameAndHasTodos_thenReturnUserWithTodos() {
        List<String> todoTitles = new ArrayList<>();
        todoTitles.add(todoOne.getTitle());
        todoTitles.add("second");
        Todo todoTwo = new Todo(todoTitles.get(1));
        user.addTodo(todoOne);
        user.addTodo(todoTwo);

        todoRepository.save(todoOne);
        todoRepository.save(todoTwo);
        userRepository.save(user);

        User found = userRepository.findByName(user.getName());
        List<Todo> foundTodos = found.getTodoList();
        List<String> foundTodoTitles = new ArrayList<>();
        foundTodos.forEach(todo -> foundTodoTitles.add(todo.getTitle()));

        assertThat(foundTodoTitles).containsAll(todoTitles);
    }

    @Test
    public void whenTodoIsSetToCompleted_thenPersistTheChange() {
        user.addTodo(todoOne);
        todoRepository.save(todoOne);
        userRepository.save(user);

        todoOne.setCompleted(true);
        todoRepository.flush();

        User found = userRepository.findByName(user.getName());
        List<Todo> foundTodos = found.getTodoList();

        assertThat(foundTodos.get(0).isCompleted()).isTrue();
    }
}
