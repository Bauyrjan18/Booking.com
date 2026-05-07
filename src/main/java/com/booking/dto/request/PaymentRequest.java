package com.booking.dto.request;

import com.booking.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull private Long bookingId;
    @NotNull private PaymentMethod method;
}
