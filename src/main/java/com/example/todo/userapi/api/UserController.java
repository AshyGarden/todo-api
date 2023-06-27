package com.example.todo.userapi.api;

import com.example.todo.exception.DuplicatedEmailException;
import com.example.todo.exception.NoRegisteredArgumentsException;
import com.example.todo.userapi.dto.UserSignUpResponseDTO;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    //이메일 중복확인요청
    //GET: /api/auth/check?email=zzz@xxx.com
    @GetMapping("/check")
    public ResponseEntity<?> check(String email){
        if(email.trim().equals("")){
            return ResponseEntity.badRequest().body("이메일이 존재하지 않습니다!");
        }

        boolean resultFlag = userService.isDuplicate(email);
        log.info("{} 중복?? - {}",email,resultFlag);

        return  ResponseEntity.ok().body(resultFlag);
    }

    //회원가입 요청처리
    //POST: /api/suth

    @PostMapping
    public ResponseEntity<?> signUp(@Validated @RequestBody UserRequestSignUpDTO dto,
                                    BindingResult result){

        log.info("/api/auth POST : {}",dto);
        if(result.hasErrors()){
            log.warn(result.toString());
            return  ResponseEntity.badRequest().body(result.getFieldError());
        }

        try {
            UserSignUpResponseDTO responseDTO = userService.create(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (NoRegisteredArgumentsException e) {
            log.warn("필수가입정보누락!");
            return  ResponseEntity.badRequest().body(e.getMessage());
        } catch (DuplicatedEmailException e) {
            log.warn("이메일 중복!");
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    
    //로그인 요청 처리
    @PostMapping("/signin")
    public ResponseEntity<?> singIn(@Validated @RequestBody LoginRequestDTO dto){
        try {
            LoginResponseDTO responseDTO = userService.authenticate(dto);
            return  ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
