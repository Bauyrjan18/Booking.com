package com.booking.dto.response;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private Long userId;
    private String firstName;
}
