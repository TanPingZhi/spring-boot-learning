package com.learn.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Integer age;
}
