package com.booking.dto.response;

import com.booking.model.BookingStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long hotelId;
    private String hotelName;
    private String hotelCity;
    private String hotelImageUrl;
    private Long roomId;
    private String roomName;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Long nights;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private String specialRequests;
    private LocalDateTime createdAt;
    private Long daysUntilCheckIn;
    private Long daysUntilFree;
}