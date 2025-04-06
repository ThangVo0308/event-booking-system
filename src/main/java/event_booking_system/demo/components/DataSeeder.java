package event_booking_system.demo.components;

import com.github.javafaker.Faker;
import event_booking_system.demo.entities.*;
import event_booking_system.demo.enums.*;
import event_booking_system.demo.repositories.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataSeeder {

    RoleRepository roleRepository;
    UserRepository userRepository;
    EventRepository eventRepository;
    TicketRepository ticketRepository;
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    PaymentRepository paymentRepository;
    CheckInRepository checkinRepository;
    MediaRepository mediaRepository;
    PasswordEncoder passwordEncoder;

    Faker faker = new Faker();

    @PostConstruct
    @Transactional
    public void seed() {
        seedRoles();
        seedUsers();
        seedEvents();
        seedTickets();
        seedOrders();
        seedOrderItems();
        seedPayments();
        seedCheckins();
        seedMedia();
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            Role user = Role.builder().name("USER").build();
            Role organizer = Role.builder().name("ORGANIZER").build();
            Role admin= Role.builder().name("ADMIN").build();
            roleRepository.saveAll(List.of(user, organizer, admin));
        }
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            List<User> users = new ArrayList<>();
            List<Role> roles = roleRepository.findAll();
            IntStream.range(0, 5).forEach(index -> {
                User user = User.builder()
                        .username(faker.name().username())
                        .password(passwordEncoder.encode("123456"))
                        .phone(faker.phoneNumber().subscriberNumber(10))
                        .birthdate(LocalDate.now().minusYears(22))
                        .gender(getRandomEnum(Gender.class))
                        .email(faker.internet().emailAddress())
                        .role(roles.get(faker.number().numberBetween(0, roles.size())))
                        .status(getRandomEnum(UserStatus.class))
                        .isActivated(true)
                        .build();
                users.add(user);
            });
            userRepository.saveAll(users);
        }
    }

    private void seedEvents() {
        if (eventRepository.count() == 0) {
            List<User> users = userRepository.findAll();
            List<Event> events = new ArrayList<>();
            IntStream.range(0, 50).forEach(index -> {
                User creator = users.get(faker.number().numberBetween(0, users.size()));
                Date startTime = faker.date().future(30, java.util.concurrent.TimeUnit.DAYS);
                Date endTime = new Date(startTime.getTime() + faker.number().numberBetween(1, 12) * 60 * 60 * 1000); // 1-12 giờ sau
                Event event = Event.builder()
                        .event_name(faker.team().name() + " Event")
                        .description(faker.lorem().sentence(10))
                        .location(faker.address().fullAddress())
                        .startTime(startTime)
                        .endTime(endTime)
                        .user(creator)
                        .build();
                events.add(event);
            });
            eventRepository.saveAll(events);
        }
    }

    private void seedTickets() {
        if (ticketRepository.count() == 0) {
            List<Event> events = eventRepository.findAll();
            List<Ticket> tickets = new ArrayList<>();
            events.forEach(event -> {
                int ticketCount = faker.number().numberBetween(1, 5);
                IntStream.range(0, ticketCount).forEach(index -> {
                    Ticket ticket = Ticket.builder()
                            .type(getRandomEnum(TicketType.class))
                            .price(faker.number().randomDouble(2, 10, 100))
                            .total_quantity(faker.number().numberBetween(10, 100))
                            .available_quantity(faker.number().numberBetween(5, 100))
                            .event(event)
                            .build();
                    tickets.add(ticket);
                });
            });
            ticketRepository.saveAll(tickets);
        }
    }

    private void seedOrders() {
        if (orderRepository.count() == 0) {
            List<User> users = userRepository.findAll();
            List<Ticket> tickets = ticketRepository.findAll();
            List<Order> orders = new ArrayList<>();
            IntStream.range(0, 5).forEach(index -> {
                User user = users.get(faker.number().numberBetween(0, users.size()));
                Ticket ticket = tickets.get(faker.number().numberBetween(0, tickets.size()));
                int quantity = faker.number().numberBetween(1, Math.min(5, ticket.getAvailable_quantity()));
                Order order = Order.builder()
                        .user(user)
                        .ticket(ticket)
                        .quantity(quantity)
                        .status(getRandomEnum(OrderStatus.class))
                        .build();
                orders.add(order);
            });
            orderRepository.saveAll(orders);
        }
    }

    private void seedOrderItems() {
        if (orderItemRepository.count() == 0) {
            List<Order> orders = orderRepository.findAll();
            List<Event> events = eventRepository.findAll();
            List<OrderItem> orderItems = new ArrayList<>();
            orders.forEach(order -> {
                int itemCount = faker.number().numberBetween(1, 3);
                IntStream.range(0, itemCount).forEach(index -> {
                    Event event = events.get(faker.number().numberBetween(0, events.size()));
                    OrderItem orderItem = OrderItem.builder()
                            .orderTime(faker.date().past(30, java.util.concurrent.TimeUnit.DAYS))
                            .price(faker.number().randomDouble(2, 5, 50))
                            .order(order)
                            .event(event)
                            .build();
                    orderItems.add(orderItem);
                });
            });
            orderItemRepository.saveAll(orderItems);
        }
    }

    private void seedPayments() {
        if (paymentRepository.count() == 0) {
            List<Order> orders = orderRepository.findAll();

            Map<Order, List<OrderItem>> orderItemsMap = orderItemRepository.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(OrderItem::getOrder));

            List<Payment> payments = new ArrayList<>();

            orders.forEach(order -> {
                List<OrderItem> orderItems = orderItemsMap.getOrDefault(order, Collections.emptyList());
                Double price = orderItems
                        .stream()
                        .mapToDouble(OrderItem::getPrice)
                        .sum();

                Payment payment = Payment.builder()
                        .order(order)
                        .price(price)
                        .status(getRandomEnum(PaymentStatus.class))
                        .build();
                payments.add(payment);
            });
            paymentRepository.saveAll(payments);
        }
    }

    private void seedCheckins() {
        if (checkinRepository.count() == 0) {
            List<Order> orders = orderRepository.findAll();
            List<Checkin> checkins = new ArrayList<>();
            orders.forEach(order -> {
                Checkin checkin = Checkin.builder()
                        .order(order)
                        .checkinTime(faker.date().past(10, java.util.concurrent.TimeUnit.DAYS))
                        .qrCode(faker.code().isbn10())
                        .status(getRandomEnum(CheckinStatus.class))
                        .build();
                checkins.add(checkin);
            });
            checkinRepository.saveAll(checkins);
        }
    }

    private void seedMedia() {
        if (mediaRepository.count() == 0) {
            List<Event> events = eventRepository.findAll();
            List<Media> medias = new ArrayList<>();
            events.forEach(event -> {
                int mediaCount = faker.number().numberBetween(1, 3);
                IntStream.range(0, mediaCount).forEach(index -> {
                    Media media = Media.builder()
                            .event(event)
                            .type(getRandomEnum(MediaType.class))
                            .build();
                    medias.add(media);
                });
            });
            mediaRepository.saveAll(medias);
        }
    }

    // Helper method to get random enum value
    private <T extends Enum<T>> T getRandomEnum(Class<T> clazz) {
        int x = new Random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}