package com.edutech.courses.controller.response;

import com.edutech.courses.dto.RoleDto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private RoleDto role;
    private Integer status;
}
