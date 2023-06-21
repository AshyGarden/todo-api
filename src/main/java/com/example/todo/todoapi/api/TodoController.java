package com.example.todo.todoapi.api;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    //할일 등록 요청
    @PostMapping
    public ResponseEntity<?> createTodo(
            @Validated @RequestBody TodoCreateRequestDTO requestDTO, BindingResult result){

//        if(requestDTO ==null){
//            return ResponseEntity.badRequest().body("등록 게시물 정보를 전달해 주세요!");
//        }

        if(result.hasErrors()){ //입력값 검증에 걸림
            log.warn(("invalid DTO ERROR! - {}"), result.getFieldError());
            return  ResponseEntity.badRequest().body(result.getFieldError());
        }

        try {
           TodoListResponseDTO responseDTO = todoService.create(requestDTO);
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
           log.error(e.getMessage());
            return  ResponseEntity.internalServerError().body("Server down... Caused by: " + e.getMessage());
        }

    }

    //할일 삭제 요청
    @DeleteMapping("/{id}")
    public  ResponseEntity<?> deleteTodo(@PathVariable("id") String todoId){

        log.info("/api/todos/{} DELETE request", todoId);

        if(todoId == null || todoId.trim().equals("")){
            return ResponseEntity.badRequest()
                    .body(TodoListResponseDTO.builder().error("ID를 전달해 주세요!"));
        }

        TodoListResponseDTO responseDTO = null;
        try {
            responseDTO = todoService.delete(todoId);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
           return ResponseEntity.internalServerError()
                   .body(TodoListResponseDTO.builder().error(e.getMessage()));
        }
    }

    //할일 목록 요청
    @GetMapping
    public ResponseEntity<?> retrieveTodoList(Todo todos){

        log.info("/api/todos GET request");
        TodoListResponseDTO responseDTO = todoService.retrieve();

        return ResponseEntity.ok().body(responseDTO);
    }

    //할일 수정 요청
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<?> updateTodo(
            @Validated @RequestBody TodoModifyRequestDTO requestDTO, BindingResult result, HttpServletRequest request){

        if(result.hasErrors()){
            return  ResponseEntity.badRequest().body(result.getFieldError());
        }

        log.info("/api/todos {} request", request.getMethod());
        log.info("modifying dto: {}",requestDTO);

        try {
            TodoListResponseDTO responseDTO = todoService.update(requestDTO);
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body((TodoListResponseDTO.builder().error(e.getMessage())));
        }
    }


}