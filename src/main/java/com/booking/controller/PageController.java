package com.booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/index"})
    public String index() { return "index"; }

    @GetMapping("/hotels")
    public String hotels() { return "hotels"; }

    @GetMapping("/hotel/{id}")
    public String hotelDetail() { return "hotel-detail"; }

    @GetMapping("/search")
    public String search() { return "search"; }

    @GetMapping("/bookings")
    public String bookings() { return "bookings"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String register() { return "register"; }

    @GetMapping("/profile")
    public String profile() { return "profile"; }
}