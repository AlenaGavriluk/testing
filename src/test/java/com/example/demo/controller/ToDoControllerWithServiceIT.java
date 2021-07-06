package com.example.demo.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.model.ToDoEntity;
import com.example.demo.repository.ToDoRepository;
import com.example.demo.service.ToDoService;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ToDoController.class)
@ActiveProfiles(profiles = "test")
@Import(ToDoService.class)
class ToDoControllerWithServiceIT {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ToDoRepository toDoRepository;

  @Test
  void whenGetAll_thenReturnValidResponse() throws Exception {
    String testText = "My to do text";
    when(toDoRepository.findAll()).thenReturn(
        Arrays.asList(
            new ToDoEntity(1l, testText)
        )
    );

    this.mockMvc
        .perform(get("/todos"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].text").value(testText))
        .andExpect(jsonPath("$[0].id").isNumber())
        .andExpect(jsonPath("$[0].completedAt").doesNotExist());
  }

  @Test
  void whenGetAllCompleted_thenReturnValidResponse() throws Exception {
    //mock
    String testText = "My to do text";
    when(toDoRepository.findByCompletedAtIsNotNullOrderByCompletedAt()).thenReturn(
        Arrays.asList(new ToDoEntity(1l, testText).completeNow())
    );
    //validate
    this.mockMvc
        .perform(get("/todos/completed").param("print", "false"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].text").value(testText))
        .andExpect(jsonPath("$[0].id").isNumber())
        .andExpect(jsonPath("$[0].completedAt").isNotEmpty());
  }
}
