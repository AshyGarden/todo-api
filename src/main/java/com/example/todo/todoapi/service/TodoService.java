package com.example.todo.todoapi.service;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    //할일 목록 조회
    //요청에 따라 데이터 갱신, 수정, 삭제 등이 발생한 후
    //최신의 데이터  내용을 클라이언트에게 전달해서 랜더링 하기위해
    //목록 리턴 메서드를 서비스에서 처리.
    public TodoListResponseDTO retrieve(String userId) { //삭제후 이 함수를 부르기(화면단에서 다시부르는것 보다 간편)

        //ctrl+alt+n
        //로그인한 유저의 정보 데이터베이스에서 조회
        User user = getUser(userId);

        List<Todo> entityList = todoRepository.findAllByUser(user);

        List<TodoDetailResponseDTO> dtoList = entityList.stream()
                // = .map(todo -> new TodoDetailResponseDTO(todo))
                .map(TodoDetailResponseDTO::new)
                .collect(Collectors.toList());

        return TodoListResponseDTO.builder().todos(dtoList).build();
    }

    private User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
        return user;
    }
        //할 일 삭제
    public TodoListResponseDTO delete(final String todoId, String userId) {
        try {
            todoRepository.deleteById(todoId);
        } catch (Exception e) {
            log.error("ID가 존재하지 않아 실패해습니다. - ID: {}, err: {}"
                    ,todoId ,e.getMessage());
            throw new RuntimeException("ID가 존재하지 않아 실패해습니다");
        }

        return retrieve(userId);
    }

    public TodoListResponseDTO
                create(final TodoCreateRequestDTO requestDTO
                       , final String userId
    ) throws RuntimeException{

        Todo todo = requestDTO.toEntity(getUser(userId));

        todoRepository.save(todo);
        log.info("할 일 저장 완료! 제목: {}",requestDTO.getTitle());
        return retrieve(userId);
    }

    public TodoListResponseDTO update(final TodoModifyRequestDTO requestDTO, String userId) throws RuntimeException{

        Optional<Todo> targetEntity = todoRepository.findById(requestDTO.getId());

        targetEntity.ifPresent(entity -> {
            entity.setDone(requestDTO.isDone()); //client에서 전달한 isDone을 그대로 전달
            todoRepository.save(entity);
        });

        return retrieve(userId);
    }
}
