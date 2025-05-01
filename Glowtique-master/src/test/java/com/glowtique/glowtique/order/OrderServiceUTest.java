package com.glowtique.glowtique.order;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.cart.repository.CartItemRepository;
import com.glowtique.glowtique.cart.repository.CartRepository;
import com.glowtique.glowtique.exception.CartNotExisting;
import com.glowtique.glowtique.exception.NotEnoughProductStock;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.model.OrderItem;
import com.glowtique.glowtique.order.model.OrderMethod;
import com.glowtique.glowtique.order.model.OrderStatus;
import com.glowtique.glowtique.order.repository.OrderItemRepository;
import com.glowtique.glowtique.order.repository.OrderRepository;
import com.glowtique.glowtique.order.service.OrderService;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.web.dto.OrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.glowtique.glowtique.user.model.User;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private OrderRequest orderRequest;
    private Cart cart;
    private Product product2;
    private Product product;
    private CartItem cartItem;
    private CartItem cartItem2;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("John");
        user.setLastName("Doe");
        orderRequest = new OrderRequest();
        orderRequest.setFirstName("John");
        orderRequest.setLastName("Doe");
        orderRequest.setOrderMethod(OrderMethod.SPEEDY_POST);
        orderRequest.setTown("Varna");
        orderRequest.setPhoneNumber("0898946872");
        orderRequest.setPostCode("9000");
        orderRequest.setShippingAddress("Chaika 66");

        product = new Product();
        product2 = new Product();
        product.setName("Sauvage Dior");
        product2.setName("Hugo Boss");
        product.setQuantity(3);
        product2.setQuantity(5);
        product.setPrice(BigDecimal.valueOf(250));
        product2.setPrice(BigDecimal.valueOf(600));

        cartItem = new CartItem();
        cartItem.setProduct(product);


        cartItem2 = new CartItem();
        cartItem2.setProduct(product2);

        cart = new Cart();
        cart.setCartItems(new ArrayList<>(List.of(cartItem, cartItem2)));
        cart.setUser(user);
        user.setCart(cart);
    }

    @Test
    void gettingExceptionNotEnoughProductsWhenAddingMoreProductsThanInStock() {

        cartItem.setQuantity(10);

        assertThrows(NotEnoughProductStock.class,
                () -> orderService.createOrder(orderRequest, user, cart));
    }

    @Test
    void buildingCorrectlyOrderItems() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.saveAll(any())).thenReturn(null);

        Order order = orderService.createOrder(orderRequest, user, cart);

        assertEquals(user, order.getUser());
        assertEquals(2, order.getOrderItems().size());
        assertEquals(orderRequest.getOrderMethod(), order.getOrderMethod());
        assertEquals(orderRequest.getTown(), order.getTown());
        assertEquals(orderRequest.getShippingAddress(), order.getShippingAddress());

        verify(orderRepository).save(order);
        verify(orderItemRepository).saveAll(any());

    }

    @Test
    void gettingCurrentOrderCorrectly() {
        Order order = orderService.createOrder(orderRequest, user, cart);
        when(orderRepository.getLastOrderByUserId(user.getId())).thenReturn(Optional.of(order));

        Order result = orderService.getCurrentOrder(user.getId());
        assertEquals(order, result);
    }

    @Test
    void gettingExceptionWhenCurrentOrderIsEmpty() {
        when(orderRepository.getLastOrderByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(CartNotExisting.class, () -> orderService.getCurrentOrder(user.getId()));
    }

    @Test
    void completingOrderSuccessfully() {
        cartItem.setQuantity(1);
        cartItem2.setQuantity(1);
        Order order = orderService.createOrder(orderRequest, user, cart);
        when(orderRepository.getLastOrderByUserId(user.getId())).thenReturn(Optional.of(order));

        orderService.completeOrder(user);

        assertEquals(OrderStatus.ORDER_CONFIRMED, order.getOrderStatus());
        assertEquals(2, product.getQuantity());
        assertEquals(4, product2.getQuantity());

        verify(productRepository).save(product);
    }

    @Test
    void gettingExceptionWhenNotEnoughProductsToFinishOrder() {
        product.setQuantity(1);
        product2.setQuantity(1);
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(3)
                .build();
        OrderItem orderItem2 = OrderItem.builder()
                .product(product2)
                .quantity(9)
                .build();

        Order order = Order.builder()
                .user(user)
                .orderItems(List.of(orderItem, orderItem2))
                .orderStatus(OrderStatus.PENDING)
                .orderDate(LocalDateTime.now().plusDays(1))
                .build();


        when(orderRepository.getLastOrderByUserId(user.getId())).thenReturn(Optional.of(order));

        assertThrows(NotEnoughProductStock.class, () -> orderService.completeOrder(user));
    }
    @Test
    void gettingAllConfirmedOrdersTwoDaysAgo() {
        List<Order> mockedOrders = List.of(orderService.createOrder(orderRequest, user, cart));

        when(orderRepository.getAllOrderByOrderStatusAndOrderDateBefore(
                eq(OrderStatus.ORDER_CONFIRMED), any(LocalDateTime.class)))
                .thenReturn(mockedOrders);

        List<Order> result = orderService.allConfirmedOrdersBeforeTwoDays();

        assertEquals(1, result.size());
        assertEquals(mockedOrders.get(0).getOrderDate(), result.get(0).getOrderDate());
        assertEquals(mockedOrders.get(0).getOrderStatus(), result.get(0).getOrderStatus());

    }
    @Test
    void gettingAllDeliveredOrdersBefore30min() {
        List<Order> mockedOrders = List.of(orderService.createOrder(orderRequest, user, cart));

        when(orderRepository.getAllOrderByOrderStatusAndOrderDateBefore(eq(OrderStatus.DELIVERED), any(LocalDateTime.class))).thenReturn((mockedOrders));

        List<Order> result = orderService.allDeliveredOrdersBefore30min();

        assertEquals(1, result.size());
        assertEquals(mockedOrders.get(0).getOrderDate(), result.get(0).getOrderDate());
        assertEquals(mockedOrders.get(0).getOrderStatus(), result.get(0).getOrderStatus());
    }

}
