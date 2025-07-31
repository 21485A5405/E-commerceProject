package com.example.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.SalesOverviewService;

@RestController
@RequestMapping("/sales")
public class SalesOverviewController {

    private final SalesOverviewService salesOverviewService;

    public SalesOverviewController(SalesOverviewService salesOverviewService) {
        this.salesOverviewService = salesOverviewService;
    }

    @GetMapping("/total-revenue")
    public double getTotalRevenue() {
        return salesOverviewService.getTotalRevenue();
    }

    @GetMapping("/total-orders")
    public long getTotalOrders() {
        return salesOverviewService.getTotalOrders();
    }

    @GetMapping("/orders-per-day")
    public Map<String, Long> getOrderCountPerDay() {
        return salesOverviewService.getOrderCountPerDay();
    }

    @GetMapping("/top-products")
    public Map<Long, Long> getTopSellingProducts() {
        return salesOverviewService.getTopSellingProducts();
    }
    
    @GetMapping("/orders-count/SHIPPED")
    public ResponseEntity<Long> getShippedCount() {
        long count = salesOverviewService.getShippedOrdersCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/orders-count/CANCELLED")
    public ResponseEntity<Long> getCancelledCount() {
        long count = salesOverviewService.getCancelledOrdersCount();
        return ResponseEntity.ok(count);
    }
}

