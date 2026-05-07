package com.booking.controller;

import com.booking.dto.request.ReviewRequest;
import com.booking.dto.response.ReviewResponse;
import com.booking.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ReviewResponse>> getByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getByHotel(hotelId));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}