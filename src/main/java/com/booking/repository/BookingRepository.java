package com.booking.repository;

import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Booking> findByHotelIdOrderByCreatedAtDesc(Long hotelId);
    List<Booking> findByStatus(BookingStatus status);

    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.room r
        JOIN FETCH b.hotel h
        WHERE b.user.id = :userId
        ORDER BY b.createdAt DESC
    """)
    List<Booking> findByUserIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.hotel.id = :hotelId AND b.status = 'CONFIRMED'")
    Long countActiveByHotel(@Param("hotelId") Long hotelId);
}