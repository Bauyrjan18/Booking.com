package com.booking.repository;

import com.booking.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCityIgnoreCaseAndIsAvailableTrue(String city);

    List<Hotel> findByCountryIgnoreCaseAndIsAvailableTrue(String country);

    List<Hotel> findByStarsGreaterThanEqualAndIsAvailableTrue(Integer stars);

    List<Hotel> findByPricePerNightBetweenAndIsAvailableTrue(BigDecimal min, BigDecimal max);

    @Query("""
        SELECT DISTINCT h FROM Hotel h
        WHERE h.isAvailable = true
        AND (:city IS NULL OR LOWER(h.city) = LOWER(:city))
        AND (:minStars IS NULL OR h.stars >= :minStars)
        AND (:maxPrice IS NULL OR h.pricePerNight <= :maxPrice)
        AND h.id NOT IN (
            SELECT b.hotel.id FROM Booking b
            WHERE b.status != 'CANCELLED'
            AND b.checkIn < :checkOut
            AND b.checkOut > :checkIn
        )
    """)
    List<Hotel> searchAvailable(
            @Param("city") String city,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("minStars") Integer minStars,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hotel.id = :hotelId")
    Double findAverageRating(@Param("hotelId") Long hotelId);

    @Query("SELECT DISTINCT h.city FROM Hotel h WHERE h.isAvailable = true ORDER BY h.city")
    List<String> findAllCities();
}
