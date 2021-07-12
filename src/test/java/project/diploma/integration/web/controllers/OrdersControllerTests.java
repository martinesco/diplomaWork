package project.diploma.integration.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.ModelAndView;
import project.diploma.domain.entities.Order;
import project.diploma.domain.models.view.OrderViewModel;
import project.diploma.repository.OrderRepository;
import project.diploma.web.controllers.OrdersController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
//@AutoConfigureMockMvc(secure = false)
//@AutoConfigureMockMvc(addFilters = false)
public class OrdersControllerTests {
    @Autowired
    OrdersController controller;

    @Mock
    Principal principal;

    @MockBean
    OrderRepository mockOrderRepository;
    private ArrayList<Order> orders;

    @Before
    public void setupTest(){
        orders = new ArrayList<>();
        when(mockOrderRepository.findAllOrdersByCustomer_UsernameOrderByFinishedOn(any()))
                .thenReturn(orders);

    }

    @Test
    @WithMockUser
    public void getCustomerOrders_whenCustomerHasNoOrders_empty() {
        orders.clear();
        ModelAndView modelAndView = new ModelAndView();
        when(principal.getName())
                .thenReturn("");

        ModelAndView result = controller.getMyOrders(modelAndView, principal);

        List<OrderViewModel> viewModels = (List<OrderViewModel>) result.getModel().get("orders");
        assertTrue(viewModels.isEmpty());
    }

    @Test
    @WithMockUser
    public void getCustomerOrders_whenAllOrdersAreForCustomer_orders() {
        orders.addAll(List.of(
                new Order()
        ));

        ModelAndView modelAndView = new ModelAndView();
        when(principal.getName())
                .thenReturn("");

        ModelAndView result = controller.getMyOrders(modelAndView, principal);

        List<OrderViewModel> viewModels = (List<OrderViewModel>) result.getModel().get("orders");
        assertEquals(orders.size(), viewModels.size());

    }

    @Test
    @WithMockUser
    public void getCustomerOrders_whenNotAllOrdersAreForCustomer_orders() {
        orders.addAll(List.of(
                new Order()
        ));

        ModelAndView modelAndView = new ModelAndView();
        when(principal.getName())
                .thenReturn("");

        ModelAndView result = controller.getMyOrders(modelAndView, principal);

        List<OrderViewModel> viewModels = (List<OrderViewModel>) result.getModel().get("orders");
        assertEquals(orders.size(), viewModels.size());
    }
}
