package com.example.service;

import com.example.authentication.CurrentUser;
import com.example.model.OrderItem;
import com.example.model.OrderProduct;
import com.example.model.User;
import com.example.repo.OrderRepo; // adjust this to your actual package
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SalesOverviewServiceImpl implements SalesOverviewService {

    private final OrderRepo orderRepo;
    private final CurrentUser currentUser;

    public SalesOverviewServiceImpl(OrderRepo orderRepo, CurrentUser currentUser) {
        this.orderRepo = orderRepo;
        this.currentUser = currentUser;
    }

    private void ensureAdminAccess() {
        User user = currentUser.getUser();
        if (user == null || user.getUserRole() == null || !user.getUserRole().name().equals("ADMIN")) {
            throw new SecurityException("Access denied: Admins only");
        }
    }

    @Override
    public double getTotalRevenue() {
        ensureAdminAccess();
        List<OrderProduct> orders = orderRepo.findAll();
        return orders.stream()
                .mapToDouble(OrderProduct::getTotalPrice)
                .sum();
    }

    @Override
    public long getTotalOrders() {
        ensureAdminAccess();
        return orderRepo.count();
    }

    @Override
    public Map<String, Long> getOrderCountPerDay() {
        ensureAdminAccess();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return orderRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderDate().format(formatter),
                        Collectors.counting()
                ));
    }

    @Override
    public Map<Long, Long> getTopSellingProducts() {
        ensureAdminAccess();
        List<OrderProduct> orders = orderRepo.findAll();

        Map<Long, Long> productSales = new HashMap<>();

        for (OrderProduct order : orders) {
            for (OrderItem item : order.getItems()) {
                Long productId = item.getProduct().getProductId();
                productSales.put(productId, productSales.getOrDefault(productId, 0L) + item.getQuantity());
            }
        }

        return productSales.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (x, y) -> x,
                        LinkedHashMap::new
                ));
    }
    
    public long getShippedOrdersCount() {
    	ensureAdminAccess();
        return orderRepo.findAll().stream()
                .filter(o -> o.getOrderStatus().name().equalsIgnoreCase("SHIPPED"))
                .count();
    }

    public long getCancelledOrdersCount() {
    	System.out.println("hi");
    	ensureAdminAccess();
        return orderRepo.findAll().stream()
                .filter(o -> o.getOrderStatus().name().equalsIgnoreCase("CANCELLED"))
                .count();
    }

}
