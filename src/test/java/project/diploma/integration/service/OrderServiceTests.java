package project.diploma.integration.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import project.diploma.domain.entities.Order;
import project.diploma.domain.models.service.OrderServiceModel;
import project.diploma.repository.OrderRepository;
import project.diploma.repository.ProductRepository;
import project.diploma.service.OrderService;
import project.diploma.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderServiceTests {
    @Autowired
    OrderService service;

    @MockBean
    OrderRepository mockOrderRepository;

    @MockBean
    UserService mockUserService;

    @MockBean
    ProductRepository mockProductRepository;

    private List<Order> orders;

    @Before
    public void setupTest() {
        orders = new ArrayList<>();
        when(mockOrderRepository.findAll())
                .thenReturn(orders);
    }

    @Test
    public void findAllOrders_whenNoOrders_returnEmptyOrders() {
        orders.clear();
        List<OrderServiceModel> result = service.findAllOrders();
        assertTrue(result.isEmpty());
    }

}
