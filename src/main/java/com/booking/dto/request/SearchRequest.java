package com.booking.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SearchRequest {
    private String city;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guests;
    private Integer minStars;
    private BigDecimal maxPrice;
}
