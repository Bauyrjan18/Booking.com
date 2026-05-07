package com.booking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class HotelRequest {
    @NotBlank private String name;
    @NotBlank private String city;
    @NotBlank private String country;
    @NotBlank private String address;
    private String description;
    @NotNull @Min(1) @Max(5) private Integer stars;
    @NotNull @DecimalMin("0.01") private BigDecimal pricePerNight;
    private String imageUrl;
    private String amenities;
    @NotNull private Boolean isAvailable;
}
