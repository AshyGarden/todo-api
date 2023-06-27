package com.example.todo.filter;

import com.example.todo.auth.TokenProvider;
import com.example.todo.auth.TokenUserInfo;
import com.example.todo.userapi.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.WebResourceRoot;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.crypto.Cipher.SECRET_KEY;

//클라이언트가 전송한 토큰을 검사하는 필터
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private  final TokenProvider tokenProvider;

    @Override //필터가 해야할일을 기술( overriding이기 때문에 변수는 그대로)
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = parseBearerToken(request);
            log.info("Jwt Token Filter is Running! token - {}",token);

            //토큰 위조검사 및 인증 완료 처리
            if(token != null) {

                // 토큰 서명 위조 검사와 토큰을 파싱해서 클레임을 얻어내는 작업
                TokenUserInfo userInfo
                        = tokenProvider.validateAndGetTokenUserInfo(token);

                //인가 정보 리스트
                List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
                authorityList.add(new SimpleGrantedAuthority(userInfo.getRole().toString()));

                // 인증 완료 처리
                // - 스프링 시큐리티에게 인증정보를 전달해서
                // 전역적으로 앱에서 인증정보를 활용할 수 있게 설정
                AbstractAuthenticationToken auth
                        = new UsernamePasswordAuthenticationToken(
                        userInfo, // 컨트롤러에서 활용할 유저 정보
                        null, // 인증된 사용자의 비밀번호 - 보통 Null(비번 탈취 가능성 존재)
                        authorityList // 인가 정보 (권한 정보)
                );

                //인증완료 처리시 클라이언트의 요청정보 세팅
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //Spring Security Container에 인증정보 객체등록
                SecurityContextHolder.getContext().setAuthentication(auth);

            }
        } catch (Exception e) {
           e.printStackTrace();
           log.error("토큰이 위조되었습니다.");
        }

        //필터체인에 내가 만든 필터 실행명령
        filterChain.doFilter(request, response);

    }

    private String parseBearerToken(HttpServletRequest request) {
        //요청 헤더에서 토큰 가져오기
        //HTTP REQUEST HEADER
        // --Content-type : application/json
        // --Authorization : Bearer 12345678901234567(토큰값)
        String bearerToken = request.getHeader("Authorization");

        //요청 헤더에서 가져온 토큰은 순수 토큰값이 아닌 Bearer가 붙은 값으로 이것을 제거해줘야함.
        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7); //6글자+공백 = 7글자
        }
        return null; //토큰이 없는경우(로그인이 되지 않은 경우)
    }

}
