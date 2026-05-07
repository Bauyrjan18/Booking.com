package com.booking.config;

import com.booking.model.*;
import com.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        if (hotelRepository.count() == 0) {
            seedHotels();
        }
        System.out.println("✅ NomadHome  data initialized successfully!");
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin").email("admin@nomad home.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin").lastName("NomadHome")
                    .phone("+7 700 000 0000").country("Kazakhstan")
                    .role(Role.ADMIN).build());
            System.out.println("✓ Admin: admin / admin123");
        }
        if (!userRepository.existsByUsername("user")) {
            userRepository.save(User.builder()
                    .username("user").email("user@NomadHome.com")
                    .password(passwordEncoder.encode("user123"))
                    .firstName("Alex").lastName("Johnson")
                    .phone("+7 777 111 2233").country("Kazakhstan")
                    .role(Role.USER).build());
            System.out.println("✓ User: user / user123");
        }
    }

    private void seedHotels() {
        // Hotel 1 - Almaty 5 stars
        Hotel h1 = hotelRepository.save(Hotel.builder()
                .name("The Ritz-Carlton Almaty").city("Алматы").country("Kazakhstan")
                .address("пр. Достык, 100").stars(5)
                .pricePerNight(new BigDecimal("85000"))
                .description("Роскошный отель в сердце Алматы с видом на горы Тянь-Шань. Ресторан высокой кухни, спа-центр и бассейн с панорамным видом.")
                .imageUrl("https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800")
                .amenities("WiFi,Pool,Spa,Gym,Restaurant,Bar,Parking,Concierge")
                .isAvailable(true).build());
        addRooms(h1, 55000, 85000, 150000);

        // Hotel 2 - Almaty 4 stars
        Hotel h2 = hotelRepository.save(Hotel.builder()
                .name("Hyatt Regency Almaty").city("Алматы").country("Kazakhstan")
                .address("ул. Сатпаева, 29А").stars(4)
                .pricePerNight(new BigDecimal("45000"))
                .description("Современный бизнес-отель в деловом центре Алматы. Конференц-залы, фитнес-центр и несколько ресторанов на выбор.")
                .imageUrl("https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800")
                .amenities("WiFi,Gym,Restaurant,Bar,BusinessCenter,Parking")
                .isAvailable(true).build());
        addRooms(h2, 30000, 45000, 80000);

        // Hotel 3 - Astana 5 stars
        Hotel h3 = hotelRepository.save(Hotel.builder()
                .name("The St. Regis Astana").city("Астана").country("Kazakhstan")
                .address("пр. Туран, 10").stars(5)
                .pricePerNight(new BigDecimal("95000"))
                .description("Иконический отель в столице Казахстана с потрясающим видом на Байтерек. Butler service, спа и гастрономические рестораны.")
                .imageUrl("https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800")
                .amenities("WiFi,Pool,Spa,Gym,Restaurant,Bar,Butler,Valet")
                .isAvailable(true).build());
        addRooms(h3, 65000, 95000, 180000);

        // Hotel 4 - Astana 4 stars
        Hotel h4 = hotelRepository.save(Hotel.builder()
                .name("Hilton Astana").city("Астана").country("Kazakhstan")
                .address("ул. Сыганак, 2").stars(4)
                .pricePerNight(new BigDecimal("38000"))
                .description("Отель в самом центре столицы рядом с ЭКСПО. Современные номера, панорамный бар и собственный спа-центр.")
                .imageUrl("https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=800")
                .amenities("WiFi,Pool,Spa,Restaurant,Bar,BusinessCenter")
                .isAvailable(true).build());
        addRooms(h4, 25000, 38000, 70000);

        // Hotel 5 - Shymkent 3 stars
        Hotel h5 = hotelRepository.save(Hotel.builder()
                .name("Rixos Shymkent").city("Шымкент").country("Kazakhstan")
                .address("ул. Байдибек би, 201").stars(4)
                .pricePerNight(new BigDecimal("28000"))
                .description("Лучший отель южной столицы с великолепным аквапарком и ультра-включено системой обслуживания.")
                .imageUrl("https://images.unsplash.com/photo-1535827841776-24afc1e255ac?w=800")
                .amenities("WiFi,Pool,Waterpark,Spa,Gym,AllInclusive")
                .isAvailable(true).build());
        addRooms(h5, 18000, 28000, 50000);

        // Hotel 6 - Almaty budget
        Hotel h6 = hotelRepository.save(Hotel.builder()
                .name("Holiday Inn Almaty").city("Алматы").country("Kazakhstan")
                .address("ул. Гоголя, 127").stars(3)
                .pricePerNight(new BigDecimal("18000"))
                .description("Комфортный отель для деловых и туристических поездок. Удобное расположение в центре города, вкусные завтраки.")
                .imageUrl("https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=800")
                .amenities("WiFi,Restaurant,Parking,Gym")
                .isAvailable(true).build());
        addRooms(h6, 12000, 18000, 32000);

        // Hotel 7 - Almaty luxury boutique
        Hotel h7 = hotelRepository.save(Hotel.builder()
                .name("Boutique Hotel Kazzhol").city("Алматы").country("Kazakhstan")
                .address("ул. Гоголя, 127/1").stars(5)
                .pricePerNight(new BigDecimal("120000"))
                .description("Эксклюзивный бутик-отель для самых взыскательных гостей. Всего 20 номеров, персональный сервис и коллекция современного искусства.")
                .imageUrl("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800")
                .amenities("WiFi,Spa,Butler,ArtGallery,PrivateDining,Concierge")
                .isAvailable(true).build());
        addRooms(h7, 80000, 120000, 250000);

        // Hotel 8 - Astana budget
        Hotel h8 = hotelRepository.save(Hotel.builder()
                .name("Ramada by Wyndham Astana").city("Астана").country("Kazakhstan")
                .address("ул. Кунаева, 14").stars(3)
                .pricePerNight(new BigDecimal("15000"))
                .description("Доступный отель в удобном расположении рядом с основными достопримечательностями Астаны. Чисто, уютно, по разумной цене.")
                .imageUrl("https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=800")
                .amenities("WiFi,Restaurant,Parking")
                .isAvailable(true).build());
        addRooms(h8, 10000, 15000, 25000);

        System.out.println("✓ 8 hotels with rooms created");
    }

    private void addRooms(Hotel hotel, int singlePrice, int doublePrice, int suitePrice) {
        roomRepository.save(Room.builder().hotel(hotel).name("Standard Single")
                .type(RoomType.SINGLE).capacity(1).pricePerNight(new BigDecimal(singlePrice))
                .description("Уютный одноместный номер со всеми удобствами").isAvailable(true).build());
        roomRepository.save(Room.builder().hotel(hotel).name("Comfort Double")
                .type(RoomType.DOUBLE).capacity(2).pricePerNight(new BigDecimal(doublePrice))
                .description("Просторный двухместный номер с двуспальной кроватью").isAvailable(true).build());
        roomRepository.save(Room.builder().hotel(hotel).name("Luxury Suite")
                .type(RoomType.SUITE).capacity(2).pricePerNight(new BigDecimal(suitePrice))
                .description("Роскошный люкс с гостиной, джакузи и панорамным видом").isAvailable(true).build());
        roomRepository.save(Room.builder().hotel(hotel).name("Family Room")
                .type(RoomType.FAMILY).capacity(4).pricePerNight(new BigDecimal((int)(doublePrice * 1.4)))
                .description("Большой семейный номер с двумя спальными зонами").isAvailable(true).build());
    }
}
