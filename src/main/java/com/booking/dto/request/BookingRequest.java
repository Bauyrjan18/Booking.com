package com.booking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    @NotNull private Long hotelId;
    @NotNull private Long roomId;
    @NotNull private LocalDate checkIn;
    @NotNull private LocalDate checkOut;
    private String specialRequests;
}