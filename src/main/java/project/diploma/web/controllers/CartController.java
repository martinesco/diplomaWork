package project.diploma.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import project.diploma.domain.models.service.OrderProductServiceModel;
import project.diploma.domain.models.service.OrderServiceModel;
import project.diploma.domain.models.service.ProductServiceModel;
import project.diploma.domain.models.view.OrderProductViewModel;
import project.diploma.domain.models.view.ProductDetailsViewModel;
import project.diploma.domain.models.view.ShoppingCartItem;
import project.diploma.service.OrderService;
import project.diploma.service.ProductService;
import project.diploma.service.UserService;
import project.diploma.web.annotations.PageTitle;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController extends BaseController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @Autowired
    public CartController(ProductService productService, UserService userService, OrderService orderService, ModelMapper modelMapper) {
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }


    @PostMapping("/add-product")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView addToCartConfirm(String id, int quantity, HttpSession session) {
        ProductDetailsViewModel product = this.modelMapper
                .map(this.productService.findProductById(id), ProductDetailsViewModel.class);

        OrderProductViewModel orderProductViewModel = new OrderProductViewModel();
        orderProductViewModel.setProduct(product);
        if (product.getDiscountedPrice() != null){
            orderProductViewModel.setPrice(product.getDiscountedPrice());
        }else {
            orderProductViewModel.setPrice(product.getPrice());  // product.getPrice()
        }

        ShoppingCartItem cartItem = new ShoppingCartItem();
        cartItem.setProduct(orderProductViewModel);
        cartItem.setQuantity(quantity);

        List<ShoppingCartItem> cart = this.retrieveCart(session);
        this.addItemToCart(cartItem, cart);

        return super.redirect("/home");
    }

    @GetMapping("/details")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("??????????????")
    public ModelAndView cartDetails(ModelAndView modelAndView, HttpSession session) {
        List<ShoppingCartItem> cart = this.retrieveCart(session);
        modelAndView.addObject("totalPrice", this.calcTotal(cart));

        return super.view("cart/cart-details", modelAndView);
    }

    @DeleteMapping("/remove-product")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView removeFromCartConfirm(String id, HttpSession session) {
        this.removeItemFromCart(id, this.retrieveCart(session));

        return super.redirect("/cart/details");
    }

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView checkoutConfirm(HttpSession session, Principal principal) {
        List<ShoppingCartItem> cart = this.retrieveCart(session);

        OrderServiceModel orderServiceModel = this.prepareOrder(cart, principal.getName());
        this.orderService.createOrder(orderServiceModel);
        return super.redirect("/home");
    }

    private List<ShoppingCartItem> retrieveCart(HttpSession session) {
        this.initCart(session);

        return (List<ShoppingCartItem>) session.getAttribute("shopping-cart");
    }

    private void initCart(HttpSession session) {
        if (session.getAttribute("shopping-cart") == null) {
            session.setAttribute("shopping-cart", new LinkedList<>());
        }
    }

    private void addItemToCart(ShoppingCartItem item, List<ShoppingCartItem> cart) {
        for (ShoppingCartItem shoppingCartItem : cart) {
            if (shoppingCartItem.getProduct().getProduct().getId().equals(item.getProduct().getProduct().getId())) {
                shoppingCartItem.setQuantity(shoppingCartItem.getQuantity() + item.getQuantity());
                return;
            }
        }

        cart.add(item);
    }

    private void removeItemFromCart(String id, List<ShoppingCartItem> cart) {
        cart.removeIf(ci -> ci.getProduct().getProduct().getId().equals(id));
    }

    private BigDecimal calcTotal(List<ShoppingCartItem> cart) {
        BigDecimal result = new BigDecimal(0);
        for (ShoppingCartItem item : cart) {
            result = result.add(item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        return result;
    }

    private OrderServiceModel prepareOrder(List<ShoppingCartItem> cart, String customer) {
        OrderServiceModel orderServiceModel = new OrderServiceModel();
        orderServiceModel.setCustomer(this.userService.findUserByUserName(customer));
        List<OrderProductServiceModel> products = new ArrayList<>();
        for (ShoppingCartItem item : cart) {
            OrderProductServiceModel productServiceModel = this.modelMapper.map(item.getProduct(), OrderProductServiceModel.class);

            for (int i = 0; i < item.getQuantity(); i++) {
                products.add(productServiceModel);
            }
        }

        orderServiceModel.setProducts(products);
        orderServiceModel.setTotalPrice(this.calcTotal(cart));

        return orderServiceModel;
    }
}