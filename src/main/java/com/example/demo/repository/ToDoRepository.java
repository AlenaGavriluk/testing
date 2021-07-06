package com.example.demo.repository;

import com.example.demo.model.ToDoEntity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoRepository extends JpaRepository<ToDoEntity, Long> {

  List<ToDoEntity> findByCompletedAtIsNotNullOrderByCompletedAt();
}