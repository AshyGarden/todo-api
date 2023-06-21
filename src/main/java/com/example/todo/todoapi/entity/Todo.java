package com.example.todo.todoapi.entity;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter @ToString
@EqualsAndHashCode(of = "todoId")
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity @Table(name = "tbl_todo")
public class Todo {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String todoId;

    @Column(nullable = false, length = 30)
    private String title; //제목

    private  boolean done; //일정 완료 여부

    @CreationTimestamp
    private LocalDateTime createDate; //등록시간

}
