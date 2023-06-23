package com.example.todo.userapi.api;

import com.example.todo.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
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

}
