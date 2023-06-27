package com.example.todo.userapi.service;

import com.example.todo.auth.TokenProvider;
import com.example.todo.exception.DuplicatedEmailException;
import com.example.todo.exception.NoRegisteredArgumentsException;
import com.example.todo.userapi.dto.UserSignUpResponseDTO;
import com.example.todo.userapi.dto.request.LoginRequestDTO;
import com.example.todo.userapi.dto.request.UserRequestSignUpDTO;
import com.example.todo.userapi.dto.response.LoginResponseDTO;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor //Autowired를 모두 달지않아도 주입(단 final 붙여줘야함!)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;

    public UserSignUpResponseDTO create(final UserRequestSignUpDTO dto) throws RuntimeException {

        String email = dto.getEmail();
        if(dto == null){
            throw new NoRegisteredArgumentsException("가입 정보가 없습니다.");
        }

        if(userRepository.existsByEmail(email)){ //이메일이 존재하는 경우
            log.warn("이메일이 중복되었습니다. - {}",email);
            throw new DuplicatedEmailException("중복된 이메일 입니다.");
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

    public boolean isDuplicate(String email) { //중복확인을 위한 함수
        return userRepository.existsByEmail(email);
    }

    //회원 인증
    public LoginResponseDTO authenticate(final LoginRequestDTO dto) {

        //이메일을 통해 회원 정보를 조회.
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                        () -> new RuntimeException("가입된 회원이 아닙니다.")
                );

        //비밀번호 검증
        String rawPassword = dto.getPassword(); //사용자가 입력한 비번
        String encodedPassword = user.getPassword(); //DB에 저장된 비번

        if(!encoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        log.info("{}님 로그인 성공!", user.getUserName());

        //로그인 성공 후에 클라이언트에게 뭘 리턴할 것인가?
        // -> JWT를 클라이언트에게 발급 해 줘야 함
        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user, token);
    }
}
