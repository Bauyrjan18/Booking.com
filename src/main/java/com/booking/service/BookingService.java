package com.booking.service;

import com.booking.dto.request.BookingRequest;
import com.booking.dto.response.BookingResponse;
import com.booking.exception.*;
import com.booking.model.*;
import com.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public List<BookingResponse> getMyBookings() {
        return bookingRepository.findByUserIdWithDetails(getCurrentUser().getId())
                .stream().map(this::toResponse).toList();
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream().map(this::toResponse).toList();
    }

    public BookingResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public BookingResponse create(BookingRequest req) {
        User user = getCurrentUser();

        Hotel hotel = hotelRepository.findById(req.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        Room room = roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (!room.getIsAvailable()) throw new BookingException("Room is not available");

        LocalDate checkIn = req.getCheckIn();
        LocalDate checkOut = req.getCheckOut();

        if (!checkOut.isAfter(checkIn)) throw new BookingException("Check-out must be after check-in");
        if (checkIn.isBefore(LocalDate.now())) throw new BookingException("Check-in cannot be in the past");

        // Check availability - uses JPA parameterized queries (SQL injection safe)
        List<Room> available = roomRepository.findAvailableRooms(hotel.getId(), checkIn, checkOut);
        if (available.stream().noneMatch(r -> r.getId().equals(room.getId()))) {
            throw new BookingException("Room is already booked for selected dates");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal total = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .user(user).hotel(hotel).room(room)
                .checkIn(checkIn).checkOut(checkOut)
                .totalPrice(total).status(BookingStatus.PENDING)
                .specialRequests(req.getSpecialRequests())
                .build();

        return toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse cancel(Long id) {
        Booking booking = findById(id);
        User current = getCurrentUser();

        boolean isOwner = booking.getUser().getId().equals(current.getId());
        boolean isAdmin = current.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) throw new BookingException("Cannot cancel another user's booking");
        if (booking.getStatus() == BookingStatus.CANCELLED) throw new BookingException("Already cancelled");
        if (booking.getStatus() == BookingStatus.COMPLETED) throw new BookingException("Cannot cancel completed booking");

        booking.setStatus(BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse confirm(Long id) {
        Booking booking = findById(id);
        if (booking.getStatus() != BookingStatus.PENDING) throw new BookingException("Only PENDING bookings can be confirmed");
        booking.setStatus(BookingStatus.CONFIRMED);
        return toResponse(bookingRepository.save(booking));
    }

    private Booking findById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public BookingResponse toResponse(Booking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .userId(b.getUser().getId())
                .username(b.getUser().getUsername())
                .hotelId(b.getHotel().getId())
                .hotelName(b.getHotel().getName())
                .hotelCity(b.getHotel().getCity())
                .hotelImageUrl(b.getHotel().getImageUrl())
                .roomId(b.getRoom().getId())
                .roomName(b.getRoom().getName())
                .checkIn(b.getCheckIn())
                .checkOut(b.getCheckOut())
                .nights(b.getNights())
                .totalPrice(b.getTotalPrice())
                .status(b.getStatus())
                .specialRequests(b.getSpecialRequests())
                .createdAt(b.getCreatedAt())
                .daysUntilCheckIn(b.getDaysUntilCheckIn())
                .daysUntilFree(b.getDaysUntilFree())
                .build();
    }
}