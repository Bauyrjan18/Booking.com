package com.booking.dto.response;

import com.booking.model.Role;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String country;
    private Role role;
    private LocalDateTime createdAt;
}
