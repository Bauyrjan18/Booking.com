package com.booking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull private Long hotelId;
    @NotNull @Min(1) @Max(10) private Integer rating;
    @Size(max = 2000) private String comment;
}