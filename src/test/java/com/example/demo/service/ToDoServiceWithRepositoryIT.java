package com.example.demo.service;

import com.example.demo.dto.ToDoResponse;
import com.example.demo.model.ToDoEntity;
import com.example.demo.repository.ToDoRepository;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class ToDoServiceWithRepositoryIT {

  @Autowired
  ToDoRepository toDoRepository;
  @Autowired
  ToDoService toDoService;

  @Test
  void whenGetAllCompleted_thenReturnCorrectList() {
    toDoRepository.save(new ToDoEntity("Not completed"));
    toDoRepository.save(new ToDoEntity(1l, "Completed1", ZonedDateTime.now().plusMinutes(10)));
    toDoRepository.save(new ToDoEntity(2l, "Completed2", ZonedDateTime.now().plusMinutes(5)));

    //call
    List<ToDoResponse> result = toDoService.getAllCompleted();

    //validate that only completed return
    assert result.size() == 2;
    //validate the right order
    assert result.get(1).id == 1l;
  }
}
