package com.example.todo.todoapi.service;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;




    //할일 목록 조회
    //요청에 따라 데이터 갱신, 삭제 등이 발생한 후
    //최신의 데이터  내용을 클라이언트에게 전달해서 랜더링 하기위해
    //
    public TodoListResponseDTO retrieve() { //삭제후 이 함수를 부르기(화면단에서 다시부르는것 보다 간편)

        List<Todo> entityList = todoRepository.findAll();

        List<TodoDetailResponseDTO> dtoList = entityList.stream()
                // = .map(todo -> new TodoDetailResponseDTO(todo))
                .map(TodoDetailResponseDTO::new)
                .collect(Collectors.toList());

        return TodoListResponseDTO.builder().todos(dtoList).build();
    }
        //할 일 삭제
    public TodoListResponseDTO delete(final String todoId) {
        try {
            todoRepository.deleteById(todoId);
        } catch (Exception e) {
            log.error("ID가 존재하지 않아 실패해습니다. - ID: {}, err: {}"
                    ,todoId ,e.getMessage());
            throw new RuntimeException("ID가 존재하지 않아 실패해습니다");
        }

        return retrieve();
    }

    public TodoListResponseDTO
                create(final TodoCreateRequestDTO requestDTO) throws RuntimeException{

        Todo saveTodo = todoRepository.save(requestDTO.toEntity());
        log.info("할 일 저장 완료! 제목: {}",requestDTO.getTitle());
        return retrieve();

    }

    public TodoListResponseDTO update(final TodoModifyRequestDTO requestDTO) throws RuntimeException{

        Optional<Todo> targetEntity = todoRepository.findById(requestDTO.getId());

        targetEntity.ifPresent(entity -> {
            entity.setDone(requestDTO.isDone()); //client에서 전달한 isDone을 그대로 전달
            todoRepository.save(entity);
        });

        return retrieve();
    }
}