package com.booking.repository;

import com.booking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelIdAndIsAvailableTrue(Long hotelId);

    @Query("""
        SELECT r FROM Room r
        WHERE r.hotel.id = :hotelId
        AND r.isAvailable = true
        AND r.id NOT IN (
            SELECT b.room.id FROM Booking b
            WHERE b.status != 'CANCELLED'
            AND b.checkIn < :checkOut
            AND b.checkOut > :checkIn
        )
    """)
    List<Room> findAvailableRooms(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}
