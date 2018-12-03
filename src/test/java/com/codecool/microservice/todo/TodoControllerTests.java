package com.codecool.microservice.todo;


import com.codecool.microservice.todo.model.Todo;
import com.codecool.microservice.todo.model.User;
import com.codecool.microservice.todo.repository.TodoRepository;
import com.codecool.microservice.todo.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodoControllerTests {

    private MockMvc mvc;
    private User user;
    private Todo todoOne;
    private Todo todoTwo;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TodoRepository todoRepository;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        user = new User("Joe");
        todoOne = new Todo("first");
        todoTwo = new Todo("second");
    }

    @Test
    public void givenUserWithTodo_whenGetTodos_thenReturnTodoListInJson() throws Exception {
        user.addTodo(todoOne);

        given(userRepository.findByName(anyString())).willReturn(user);

        mvc.perform(get("/todo/Joe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is(todoOne.getTitle())));
    }

    @Test
    public void givenUserWithTodos_whenGetTodos_thenReturnTodosListInJson() throws Exception {
        user.addTodo(todoOne);
        user.addTodo(todoTwo);

        given(userRepository.findByName(anyString())).willReturn(user);

        mvc.perform(get("/todo/Joe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is(todoOne.getTitle())))
                .andExpect(jsonPath("$[1].title", is(todoTwo.getTitle())));
    }

    @Test
    public void whenUserDoesntExist_thenReturnNoTodos() throws Exception {
        given(userRepository.existsByName(anyString())).willReturn(false);
        given(userRepository.findByName(anyString())).willReturn(user);

        mvc.perform(get("/todo/Joe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void whenThereAreNoTodosForUser_thenReturnNoTodos() throws Exception {
        given(userRepository.findByName(anyString())).willReturn(user);

        mvc.perform(get("/todo/Joe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void whenTodoIsNotCompleted_thenReturnWithCompletedFalse() throws Exception {
        user.addTodo(todoOne);

        given(userRepository.findByName(anyString())).willReturn(user);

        mvc.perform(get("/todo/Joe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].completed", is(false)));
    }

    @Test
    public void whenTodoIsCompleted_thenReturnWithCompletedTrue() throws Exception {
        todoOne.setCompleted(true);
        user.addTodo(todoOne);

        given(userRepository.findByName(anyString())).willReturn(user);

        mvc.perform(get("/todo/Joe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].completed", is(true)));
    }

    @Test
    public void whenSetTodoToCompleted_thenReturnWithCompletedTrue() throws Exception {
        given(todoRepository.existsById(anyLong())).willReturn(true);
        given(todoRepository.findById(anyLong())).willReturn(java.util.Optional.ofNullable(todoOne));
        given(userRepository.findByName(anyString())).willReturn(user);
        user.addTodo(todoOne);

        mvc.perform(put("/todo/Joe/1/complete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].completed", is(true)));

    }

    @Test
    public void whenTodoWithIdDoesntExist_thenReturnMissingId() throws Exception {
        given(todoRepository.existsById(anyLong())).willReturn(false);

        mvc.perform(put("/todo/Joe/1/complete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", is("There is no todo with that id!")));

    }

    @Test
    public void whenAddTodo_thenReturnTodosWithTheAdded() throws Exception {
        given(userRepository.existsById(anyLong())).willReturn(true);
        given(userRepository.findByName(anyString())).willReturn(user);

        mvc.perform(post("/todo/Joe/first")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("first")));
    }

    @Test
    public void whenAddTodoWithSpace_thenTodoIsSavedAndReturned() throws Exception {
        given(userRepository.existsById(anyLong())).willReturn(true);
        given(userRepository.findByName(anyString())).willReturn(user);

        mvc.perform(post("/todo/Joe/first todo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("first todo")));
    }

    @Test
    public void whenAddTodoWithSpecials_thenTodoIsSavedAndReturned() throws Exception {
        given(userRepository.existsById(anyLong())).willReturn(true);
        given(userRepository.findByName(anyString())).willReturn(user);
        String testTitle = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:[]@!$&'()*+,=";

        mvc.perform(post("/todo/Joe/" + testTitle)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is(testTitle)));
    }
}
