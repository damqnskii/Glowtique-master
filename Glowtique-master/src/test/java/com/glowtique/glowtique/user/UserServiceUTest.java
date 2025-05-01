package com.glowtique.glowtique.user;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.service.CartService;
import com.glowtique.glowtique.exception.AlreadyRegEmailException;
import com.glowtique.glowtique.exception.ExistingPhoneNumber;
import com.glowtique.glowtique.exception.UnchangeableEmailException;
import com.glowtique.glowtique.exception.UserNotExisting;
import com.glowtique.glowtique.notification.service.NotificationService;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.model.OrderStatus;
import com.glowtique.glowtique.user.model.Country;
import com.glowtique.glowtique.user.model.UserGender;
import com.glowtique.glowtique.user.model.UserRole;
import com.glowtique.glowtique.user.repository.UserRepository;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.web.dto.AdminRequest;
import com.glowtique.glowtique.web.dto.EditProfileRequest;
import com.glowtique.glowtique.web.dto.LoginRequest;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CartService cartService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserService userService;

    @Test
    void loginWithNotExistingEmailInTheDatabase() {
        LoginRequest loginRequest = LoginRequest.builder().build();
        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotExisting.class, () -> userService.login(loginRequest));
    }

    @Test
    void loginWithIncorrectPassword() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("damqnskiq@gmail.com")
                .password("asdasd")
                .build();
        User user = new User();
        user.setEmail("damqnskiq@gmail.com");
        user.setPassword("123456");

        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
    }

    @Test
    void loginWithCorrectDetails() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("asd@gmail.com")
                .password("asdasd")
                .build();
        User user = new User();
        user.setEmail("asd@gmail.com");
        user.setPassword("asdasd");

        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        User result = userService.login(loginRequest);

        assertNotNull(result);
        assertEquals("asd@gmail.com", result.getEmail());
    }

    @Test
    void gettingExceptionWithNotExistingId() {
        UUID uuid = UUID.randomUUID();

        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(UserNotExisting.class, () -> userService.getUserById(uuid));
    }

    @Test
    void gettingCorrectUserById() {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertEquals(user, userService.getUserById(user.getId()));
    }

    @Test
    void gettingExceptionWithNotExistingEmail() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("asd@gmail.com");

        User user = new User();
        user.setEmail("asd@gmail.com");

        when(userRepository.findUserByEmail(registerRequest.getEmail())).thenReturn(Optional.of(user));

        assertThrows(AlreadyRegEmailException.class, () -> userService.register(registerRequest));
    }

    @Test
    void gettingExceptionWhenRegistrationPasswordDoesNotMatch() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPassword("asdasd");
        registerRequest.setConfirmPassword("123456");

        assertThrows(RuntimeException.class, () -> userService.register(registerRequest));
    }

    @Test
    void correctRegistrationPasswordMatch() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPassword("asdasd");
        registerRequest.setConfirmPassword("asdasd");

        assertTrue(registerRequest.getPassword().equals(registerRequest.getConfirmPassword()));
    }

    @Test
    void registerNewUserOnlyWithInformationForThem() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        registerRequest.setBirthday(LocalDate.from(LocalDateTime.of(1990, 1, 1, 0, 0)));
        registerRequest.setGender(UserGender.MALE);

        User user = userService.register(registerRequest);

        assertNotNull(user);
        assertEquals(user.getFirstName(), registerRequest.getFirstName());
        assertEquals(user.getEmail(), registerRequest.getEmail());
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getWishlistItems().size()).isEqualTo(0);

        verify(cartService, times(1)).createCart(user);
        verify(userRepository, atLeastOnce()).save(any(User.class));
        verify(notificationService,times(1)).saveNotificationPreference(user.getId(), user.getEmail());
    }

    @Test
    void editUserProfileThrowExceptionAlreadyRegisteredUser() {
        EditProfileRequest editProfileRequest = new EditProfileRequest();
        editProfileRequest.setEmail("test@gmail.com");

        UUID uuid = UUID.randomUUID();

        User user = new User();
        user.setId(uuid);
        user.setEmail(editProfileRequest.getEmail());

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .email(editProfileRequest.getEmail())
                .build();

        when(userRepository.findUserByEmail(editProfileRequest.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(UnchangeableEmailException.class, () -> userService.updateUser(user.getId(), editProfileRequest));
    }

    @Test
    void editUserProfileThrowExceptionWhenPhoneNumberExists() {
        EditProfileRequest editProfileRequest = new EditProfileRequest();
        editProfileRequest.setPhoneNumber("0898946872");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setPhoneNumber(editProfileRequest.getPhoneNumber());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User existingUser = User.builder()
                .id(UUID.randomUUID())
                .phoneNumber(editProfileRequest.getPhoneNumber())
                .build();

        when(userRepository.findUserByPhoneNumber(editProfileRequest.getPhoneNumber())).thenReturn(Optional.of(existingUser));

        assertThrows(ExistingPhoneNumber.class, () -> userService.updateUser(user.getId(), editProfileRequest));
    }

    @Test
    void correctlyUpdateUserProfile() {
        EditProfileRequest edit = new EditProfileRequest();
        edit.setFirstName("John");
        edit.setLastName("Doe");
        edit.setEmail("john.doe@example.com");
        edit.setPhoneNumber("0898946872");
        edit.setCountry(Country.GERMANY);
        edit.setTown("Varna");

        User user = new User();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.updateUser(user.getId(), edit);
        assertEquals(edit.getFirstName(), user.getFirstName());
        assertEquals(edit.getLastName(), user.getLastName());
        assertEquals(edit.getEmail(), user.getEmail());
        assertEquals(edit.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(edit.getCountry(), user.getCountry());
        assertEquals(edit.getTown(), user.getTown());
        assertEquals(Country.GERMANY, user.getCountry());
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }
    @Test
    void correctlyChangeUsersRoles() {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setRole(UserRole.ADMIN);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(UserRole.USER);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.updateRole(adminRequest, user.getId());
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void receivingAllConfirmedOrdersByUser() {
        User user = new User();
        user.setOrders(new HashSet<>());
        Order order1 = new Order();
        order1.setUser(user);
        order1.setOrderStatus(OrderStatus.PENDING);
        order1.setOrderDate(LocalDateTime.now().plusDays(3));
        Order order2 = new Order();
        order2.setUser(user);
        order2.setOrderStatus(OrderStatus.DELIVERED);
        order2.setOrderDate(LocalDateTime.now().plusDays(5));
        Order order3 = new Order();
        order3.setUser(user);
        order3.setOrderStatus(OrderStatus.ORDER_CONFIRMED);
        order3.setOrderDate(LocalDateTime.now().plusDays(88));
        user.setOrders(Set.of(order1, order2, order3));

        List<Order> confirmedOrders = userService.getConfirmedOrders(user);

        assertThat(confirmedOrders.size()).isEqualTo(2);
        assertThat(confirmedOrders.get(0)).isEqualTo(order2);
    }




}
