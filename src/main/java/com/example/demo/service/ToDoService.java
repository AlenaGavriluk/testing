package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ToDoResponse;
import com.example.demo.dto.ToDoSaveRequest;
import com.example.demo.dto.mapper.ToDoEntityToResponseMapper;
import com.example.demo.exception.ToDoNotFoundException;
import com.example.demo.model.ToDoEntity;
import com.example.demo.repository.ToDoRepository;

@Service
public class ToDoService {
	
	private ToDoRepository toDoRepository;

	public ToDoService(ToDoRepository toDoRepository) {
		this.toDoRepository = toDoRepository;
	}
	
	public List<ToDoResponse> getAll() {
		return toDoRepository.findAll().stream()
			.map(i -> ToDoEntityToResponseMapper.map(i))
			.collect(Collectors.toList()); 
	}

	public ToDoResponse upsert(ToDoSaveRequest toDoDTO) throws ToDoNotFoundException {
		ToDoEntity todo;
		//update if it has id or create if it hasn't
		if (toDoDTO.id == null) {
			todo = new ToDoEntity(toDoDTO.text);
		} else {
			todo = toDoRepository.findById(toDoDTO.id).orElseThrow(() -> new ToDoNotFoundException(toDoDTO.id));
			todo.setText(toDoDTO.text);
		}
		return ToDoEntityToResponseMapper.map(toDoRepository.save(todo));
	}

	public ToDoResponse completeToDo(Long id) throws ToDoNotFoundException {
		ToDoEntity todo = toDoRepository.findById(id).orElseThrow(() -> new ToDoNotFoundException(id));
		todo.completeNow();
		return ToDoEntityToResponseMapper.map(toDoRepository.save(todo));
	}

	public ToDoResponse getOne(Long id) throws ToDoNotFoundException {
		return  ToDoEntityToResponseMapper.map(
			toDoRepository.findById(id).orElseThrow(() -> new ToDoNotFoundException(id))
		);
	}

	public void deleteOne(Long id) {
		toDoRepository.deleteById(id);
	}

	public List<ToDoResponse> getAllCompleted() {
		return toDoRepository.findByCompletedAtIsNotNullOrderByCompletedAt().stream()
				.map(ToDoEntityToResponseMapper::map)
				.collect(Collectors.toList());
	}

	public List<ToDoResponse> printAllCompleted() {
		List<ToDoEntity> completed = toDoRepository.findByCompletedAtIsNotNullOrderByCompletedAt();
		if (!completed.isEmpty()) {
			System.out.println("You have " + completed.size() + " completed todos:");
			for (int i = 0; i < completed.size(); i++){
				System.out.println(i + 1 + ") " + completed.get(i).getText() +
						" || " + "completed at: " + completed.get(i).getCompletedAt());
			}
		} else {
			System.out.println("You have not any completed todos.");
		}
		return completed.stream()
				.map(ToDoEntityToResponseMapper::map)
				.collect(Collectors.toList());
	}
}
