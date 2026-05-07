package com.booking.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HotelResponse {
    private Long id;
    private String name;
    private String city;
    private String country;
    private String address;
    private String description;
    private Integer stars;
    private BigDecimal pricePerNight;
    private String imageUrl;
    private String amenities;
    private Boolean isAvailable;
    private Double averageRating;
    private Integer reviewCount;
    private List<RoomResponse> rooms;
}
