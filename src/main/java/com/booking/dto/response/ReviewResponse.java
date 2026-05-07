package com.booking.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long hotelId;
    private String hotelName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
