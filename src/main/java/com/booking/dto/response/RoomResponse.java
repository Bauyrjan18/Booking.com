package com.booking.dto.response;

import com.booking.model.RoomType;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoomResponse {
    private Long id;
    private Long hotelId;
    private String name;
    private RoomType type;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private String description;
    private Boolean isAvailable;
}
