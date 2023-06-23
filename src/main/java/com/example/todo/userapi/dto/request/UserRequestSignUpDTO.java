package com.example.todo.userapi.dto.request;

import com.example.todo.userapi.entity.User;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter @ToString
@EqualsAndHashCode(of="email")
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserRequestSignUpDTO {

    //uuid를 받는 id의경우 값이 필요 없다.

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min =  8, max = 20)
    private String password;

    @NotBlank
    @Size(min =  2, max = 5)
    private String userName;

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .userName(this.userName)
                .build();
    }
}
