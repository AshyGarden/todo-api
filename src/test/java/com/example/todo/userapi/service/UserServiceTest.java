package com.example.todo.userapi.service;

import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Rollback(false)
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    @DisplayName("중복된 email로 회원가입을 시도하면 RuntimeException이 발생 해야한다.")
    void validateEmailTest() {
        //given
        String email = "abc1234@abc.com";

        //when
        UserRequestSignUpDTO dto = UserRequestSignUpDTO.builder()
                .email(email)
                .password("asdf")
                .userName("qwer")
                .build();

        //then

        //param1: 어떤 에러가 발생할 지 에러 클래스 적용
        //param2 :에러가 발생하는 상황을 전달.
        assertThrows(RuntimeException.class,
                () -> { userService.create(dto); } );
    }

}
