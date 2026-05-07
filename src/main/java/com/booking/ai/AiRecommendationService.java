package com.booking.ai;

import com.booking.dto.response.HotelResponse;
import com.booking.repository.HotelRepository;
import com.booking.repository.ReviewRepository;
import com.booking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {

    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;
    private final HotelService hotelService;

    public AiResponse chat(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return new AiResponse("Привет! Я ваш помощник по бронированию. Спросите меня о лучших отелях, ценах или рекомендациях!", Collections.emptyList());
        }

        String msg = userMessage.toLowerCase().trim();

        // Greeting
        if (containsAny(msg, "привет", "hello", "hi", "здравствуй", "салем", "сәлем")) {
            return new AiResponse(
                    "👋 Привет! Я AI-помощник NomadHome. Могу помочь с:\n" +
                            "• 🏨 Рекомендациями отелей\n" +
                            "• 💰 Поиском лучших цен\n" +
                            "• ⭐ Топ отелями по рейтингу\n" +
                            "• 🌆 Поиском по городу\n\n" +
                            "Что вас интересует?",
                    Collections.emptyList()
            );
        }

        if (containsAny(msg, "лучш", "топ", "рейтинг", "best", "top", "recommend")) {
            return getTopRatedHotels();
        }

        if (containsAny(msg, "дешев", "бюджет", "эконом", "cheap", "budget", "affordable", "недорог")) {
            return getCheapestHotels();
        }

        if (containsAny(msg, "люкс", "luxury", "5 звезд", "5 star", "vip", "премиум")) {
            return getLuxuryHotels();
        }

        List<String> cities = hotelRepository.findAllCities();
        for (String city : cities) {
            if (msg.contains(city.toLowerCase())) {
                return getHotelsByCity(city);
            }
        }

        if (containsAny(msg, "цена", "стоим", "price", "cost", "сколько")) {
            return getPriceInfo();
        }

        if (containsAny(msg, "помог", "help", "что умеешь", "что можешь")) {
            return new AiResponse(
                    "🤖 Я могу помочь вам:\n\n" +
                            "• **Найти лучшие отели** — напишите «топ отели» или «лучшие»\n" +
                            "• **Найти бюджетные варианты** — напишите «дешевые отели»\n" +
                            "• **Найти по городу** — напишите название города (Алматы, Астана, Москва...)\n" +
                            "• **Найти люкс-отели** — напишите «люкс» или «5 звезд»\n" +
                            "• **Узнать о ценах** — напишите «цены» или «стоимость»",
                    Collections.emptyList()
            );
        }

        return new AiResponse(
                "🔍 Не совсем понял ваш запрос. Попробуйте:\n" +
                        "• «лучшие отели» — топ по рейтингу\n" +
                        "• «дешевые отели» — бюджетные варианты\n" +
                        "• «отели в Алматы» — по городу\n" +
                        "• «люкс отели» — премиум класс",
                Collections.emptyList()
        );
    }

    private AiResponse getTopRatedHotels() {
        List<HotelResponse> top = hotelRepository.findAll().stream()
                .map(hotelService::toResponse)
                .filter(h -> h.getAverageRating() != null)
                .sorted(Comparator.comparingDouble(HotelResponse::getAverageRating).reversed())
                .limit(5)
                .collect(Collectors.toList());

        String text = top.isEmpty()
                ? "Пока нет отелей с отзывами. Будьте первым!"
                : "⭐ Топ-" + top.size() + " отелей по рейтингу гостей:";

        return new AiResponse(text, top);
    }

    private AiResponse getCheapestHotels() {
        List<HotelResponse> cheap = hotelRepository.findAll().stream()
                .map(hotelService::toResponse)
                .filter(h -> h.getIsAvailable())
                .sorted(Comparator.comparing(HotelResponse::getPricePerNight))
                .limit(5)
                .collect(Collectors.toList());

        return new AiResponse("💰 Лучшие бюджетные варианты — отличное соотношение цены и качества:", cheap);
    }

    private AiResponse getLuxuryHotels() {
        List<HotelResponse> luxury = hotelRepository.findByStarsGreaterThanEqualAndIsAvailableTrue(4).stream()
                .map(hotelService::toResponse)
                .sorted(Comparator.comparingInt(HotelResponse::getStars).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return new AiResponse("✨ Отели премиум-класса для незабываемого отдыха:", luxury);
    }

    private AiResponse getHotelsByCity(String city) {
        List<HotelResponse> hotels = hotelRepository.findByCityIgnoreCaseAndIsAvailableTrue(city)
                .stream().map(hotelService::toResponse).collect(Collectors.toList());

        String text = hotels.isEmpty()
                ? "😔 В городе " + city + " пока нет доступных отелей. Попробуйте другой город."
                : "🌆 Найдено " + hotels.size() + " отелей в городе " + city + ":";

        return new AiResponse(text, hotels);
    }

    private AiResponse getPriceInfo() {
        var hotels = hotelRepository.findAll().stream()
                .map(hotelService::toResponse)
                .filter(h -> h.getIsAvailable())
                .sorted(Comparator.comparing(HotelResponse::getPricePerNight))
                .collect(Collectors.toList());

        if (hotels.isEmpty()) return new AiResponse("Нет доступных отелей.", Collections.emptyList());

        HotelResponse cheapest = hotels.get(0);
        HotelResponse expensive = hotels.get(hotels.size() - 1);

        String text = String.format(
                "💵 Диапазон цен в нашей системе:\n\n" +
                        "• Минимальная цена: **%.0f ₸/ночь** (%s)\n" +
                        "• Максимальная цена: **%.0f ₸/ночь** (%s)\n\n" +
                        "Вот несколько вариантов на выбор:",
                cheapest.getPricePerNight().doubleValue(), cheapest.getName(),
                expensive.getPricePerNight().doubleValue(), expensive.getName()
        );

        return new AiResponse(text, hotels.stream().limit(4).collect(Collectors.toList()));
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }

    public record AiResponse(String message, List<HotelResponse> hotels) {}
}
