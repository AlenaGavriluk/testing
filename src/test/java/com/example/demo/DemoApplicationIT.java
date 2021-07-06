package com.example.demo;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.controller.ToDoController;
import com.example.demo.model.ToDoEntity;
import com.example.demo.repository.ToDoRepository;
import com.example.demo.service.ToDoService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class DemoApplicationIT {

  @Autowired
  ToDoRepository toDoRepository;
  @Autowired
  ToDoService toDoService;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ToDoController toDoController;

  @Test
  void contextLoads() throws Exception {
    if (toDoController == null) {
      throw new Exception("ToDoController is null");
    }
  }

  @Test
  void whenCompleteAndThenGetAllCompleted_thenReturnValidResponse() throws Exception {
    List<ToDoEntity> testTodos = new ArrayList<>();
    testTodos.add(new ToDoEntity(1l, "todo1"));
    testTodos.add(new ToDoEntity(2l, "todo2"));
    testTodos.add(new ToDoEntity(3l, "todo3"));
    toDoRepository.saveAll(testTodos);

    this.mockMvc.perform(put("/todos/1/complete"));
    this.mockMvc
        .perform(get("/todos/completed").param("print", "false"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].text").value("todo1"))
        .andExpect(jsonPath("$[0].id").isNumber())
        .andExpect(jsonPath("$[0].completedAt").isNotEmpty());

  }

}
