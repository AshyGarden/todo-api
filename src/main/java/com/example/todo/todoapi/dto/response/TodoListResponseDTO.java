package com.example.todo.todoapi.dto.response;

import com.example.todo.todoapi.entity.Todo;
import lombok.*;

import java.util.List;

@Getter @Setter @ToString
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TodoListResponseDTO {

    private String error; //에러 발생시 에러 메세지를 담을 필드
    private List<TodoDetailResponseDTO> todos;


    public TodoListResponseDTO(Todo todo) {
    }
}
