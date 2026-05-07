package com.booking.service;

import com.booking.dto.request.HotelRequest;
import com.booking.dto.request.SearchRequest;
import com.booking.dto.response.*;
import com.booking.exception.ResourceNotFoundException;
import com.booking.model.Hotel;
import com.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;
    private final RoomRepository roomRepository;

    public List<HotelResponse> getAllHotels() {
        return hotelRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<HotelResponse> search(SearchRequest req) {
        LocalDate checkIn = req.getCheckIn() != null ? req.getCheckIn() : LocalDate.now();
        LocalDate checkOut = req.getCheckOut() != null ? req.getCheckOut() : LocalDate.now().plusDays(1);
        return hotelRepository.searchAvailable(req.getCity(), checkIn, checkOut, req.getMinStars(), req.getMaxPrice())
                .stream().map(this::toResponse).toList();
    }

    public HotelResponse getById(Long id) {
        return toResponseWithRooms(findById(id));
    }

    @Transactional
    public HotelResponse create(HotelRequest req) {
        Hotel hotel = Hotel.builder()
                .name(req.getName()).city(req.getCity()).country(req.getCountry())
                .address(req.getAddress()).description(req.getDescription())
                .stars(req.getStars()).pricePerNight(req.getPricePerNight())
                .imageUrl(req.getImageUrl()).amenities(req.getAmenities())
                .isAvailable(req.getIsAvailable()).build();
        return toResponse(hotelRepository.save(hotel));
    }

    @Transactional
    public HotelResponse update(Long id, HotelRequest req) {
        Hotel hotel = findById(id);
        hotel.setName(req.getName()); hotel.setCity(req.getCity()); hotel.setCountry(req.getCountry());
        hotel.setAddress(req.getAddress()); hotel.setDescription(req.getDescription());
        hotel.setStars(req.getStars()); hotel.setPricePerNight(req.getPricePerNight());
        hotel.setImageUrl(req.getImageUrl()); hotel.setAmenities(req.getAmenities());
        hotel.setIsAvailable(req.getIsAvailable());
        return toResponse(hotelRepository.save(hotel));
    }

    @Transactional
    public void delete(Long id) {
        if (!hotelRepository.existsById(id)) throw new ResourceNotFoundException("Hotel not found: " + id);
        hotelRepository.deleteById(id);
    }

    public List<String> getCities() {
        return hotelRepository.findAllCities();
    }

    private Hotel findById(Long id) {
        return hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + id));
    }

    public HotelResponse toResponse(Hotel h) {
        Double avg = reviewRepository.findAverageRatingByHotelId(h.getId());
        int reviewCount = reviewRepository.findByHotelIdOrderByCreatedAtDesc(h.getId()).size();
        return HotelResponse.builder()
                .id(h.getId()).name(h.getName()).city(h.getCity()).country(h.getCountry())
                .address(h.getAddress()).description(h.getDescription()).stars(h.getStars())
                .pricePerNight(h.getPricePerNight()).imageUrl(h.getImageUrl()).amenities(h.getAmenities())
                .isAvailable(h.getIsAvailable()).averageRating(avg).reviewCount(reviewCount).build();
    }

    private HotelResponse toResponseWithRooms(Hotel h) {
        HotelResponse resp = toResponse(h);
        List<RoomResponse> rooms = roomRepository.findByHotelIdAndIsAvailableTrue(h.getId()).stream()
                .map(r -> RoomResponse.builder().id(r.getId()).hotelId(h.getId()).name(r.getName())
                        .type(r.getType()).capacity(r.getCapacity()).pricePerNight(r.getPricePerNight())
                        .description(r.getDescription()).isAvailable(r.getIsAvailable()).build())
                .toList();
        resp.setRooms(rooms);
        return resp;
    }
}
