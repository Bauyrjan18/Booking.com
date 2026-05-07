package com.booking.service;

import com.booking.dto.request.ReviewRequest;
import com.booking.dto.response.ReviewResponse;
import com.booking.exception.ResourceNotFoundException;
import com.booking.model.*;
import com.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    public List<ReviewResponse> getByHotel(Long hotelId) {
        return reviewRepository.findByHotelIdOrderByCreatedAtDesc(hotelId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ReviewResponse create(ReviewRequest req) {
        User user = getCurrentUser();
        Hotel hotel = hotelRepository.findById(req.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        Review review = Review.builder().user(user).hotel(hotel)
                .rating(req.getRating()).comment(req.getComment()).build();
        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void delete(Long id) {
        if (!reviewRepository.existsById(id)) throw new ResourceNotFoundException("Review not found: " + id);
        reviewRepository.deleteById(id);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ReviewResponse toResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId()).userId(r.getUser().getId()).username(r.getUser().getUsername())
                .hotelId(r.getHotel().getId()).hotelName(r.getHotel().getName())
                .rating(r.getRating()).comment(r.getComment()).createdAt(r.getCreatedAt()).build();
    }
}