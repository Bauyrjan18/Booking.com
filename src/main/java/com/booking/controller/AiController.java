package com.booking.controller;

import com.booking.ai.AiRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiRecommendationService aiService;

    @PostMapping("/chat")
    public ResponseEntity<AiRecommendationService.AiResponse> chat(@RequestBody ChatRequest req) {
        return ResponseEntity.ok(aiService.chat(req.message()));
    }

    public record ChatRequest(String message) {}
}
