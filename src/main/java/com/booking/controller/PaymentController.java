package com.booking.controller;

import com.booking.dto.request.PaymentRequest;
import com.booking.dto.response.PaymentResponse;
import com.booking.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> process(@Valid @RequestBody PaymentRequest req) {
        return ResponseEntity.ok(paymentService.process(req));
    }
}