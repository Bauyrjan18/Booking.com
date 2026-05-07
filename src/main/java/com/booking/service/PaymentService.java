package com.booking.service;

import com.booking.dto.request.PaymentRequest;
import com.booking.dto.response.PaymentResponse;
import com.booking.model.PaymentMethod;
import com.booking.model.PaymentStatus;
import com.booking.exception.;
import com.booking.model.;
import com.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public PaymentResponse process(PaymentRequest req) {
        Booking booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (booking.getStatus() == BookingStatus.CANCELLED) throw new BookingException("Cannot pay cancelled booking");

        paymentRepository.findByBookingId(booking.getId()).ifPresent(p -> {
            if (p.getStatus() == PaymentStatus.COMPLETED) throw new BookingException("Already paid");
        });

        Payment payment = Payment.builder()
                .booking(booking).amount(booking.getTotalPrice())
                .method(req.getMethod()).status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now()).build();

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Payment saved = paymentRepository.save(payment);
        return PaymentResponse.builder().id(saved.getId()).bookingId(booking.getId())
                .amount(saved.getAmount()).method(saved.getMethod()).status(saved.getStatus())
                .paidAt(saved.getPaidAt()).build();
    }
}