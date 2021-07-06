package com.example.demo.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.dto.mapper.ToDoEntityToResponseMapper;
import com.example.demo.model.ToDoEntity;
import com.example.demo.service.ToDoService;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ToDoController.class)
@ActiveProfiles(profiles = "test")
class ToDoControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ToDoService toDoService;

  @Test
  void whenGetAll_thenReturnValidResponse() throws Exception {
    String testText = "My to do text";
    Long testId = 1l;
    when(toDoService.getAll()).thenReturn(
        Arrays.asList(
            ToDoEntityToResponseMapper.map(new ToDoEntity(testId, testText))
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
        .andExpect(jsonPath("$[0].id").value(testId))
        .andExpect(jsonPath("$[0].completedAt").doesNotExist());
  }

  @Test
  void whenGetAllCompletedWithoutPrint_thenCallGetAllCompleted() throws Exception {
    //mock
    String testText = "My to do text";
    when(toDoService.getAllCompleted()).thenReturn(
        Arrays.asList(ToDoEntityToResponseMapper.map(new ToDoEntity(1l, testText).completeNow()))
    );
    //call
    this.mockMvc.perform(get("/todos/completed").param("print", "false"));
    //validate
    verify(toDoService, times(1)).getAllCompleted();
  }

  @Test
  void whenGetAllCompletedWithPrint_thenCallPrintAllCompleted() throws Exception {
    //mock
    String testText = "My to do text";
    when(toDoService.printAllCompleted()).thenReturn(
        Arrays.asList(ToDoEntityToResponseMapper.map(new ToDoEntity(1l, testText).completeNow()))
    );
    //call
    this.mockMvc.perform(get("/todos/completed").param("print", "true"));
    //validate
    verify(toDoService, times(1)).printAllCompleted();
  }

}
