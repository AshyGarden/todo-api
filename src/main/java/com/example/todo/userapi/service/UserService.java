package com.example.todo.userapi.service;

import com.example.todo.userapi.dto.UserSignUpResponseDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private  final UserRepository userRepository;
    private  final PasswordEncoder encoder;

    public UserSignUpResponseDTO create(final UserRequestSignUpDTO dto){

        String email = dto.getEmail();
        if(dto == null){
            throw new RuntimeException("가입 정보가 없습니다.");
        }

        if(userRepository.existsByEmail(email)){ //이메일이 존재하는 경우
            log.warn("이메일이 중복되었습니다. - {}",email);
            throw new RuntimeException("중복된 이메일 입니다.");
        }

        //패스워드 인코딩
        String encoded = encoder.encode(dto.getPassword());
        dto.setPassword(encoded);

        //유저 엔티티로 변환
        User user = dto.toEntity();
        User saved = userRepository.save(user);

        log.info("회원가입이 정상처리되었습니다! - saved user- {}",saved);

        return new UserSignUpResponseDTO(saved);
    }

    public boolean isDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }
}