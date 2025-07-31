package com.example.service;

import java.util.Map;

public interface SalesOverviewService {
	
    double getTotalRevenue();
    long getTotalOrders();
    Map<String, Long> getOrderCountPerDay(); // Format: YYYY-MM-DD -> count
    Map<Long, Long> getTopSellingProducts(); // ProductName -> QuantitySold
	long getShippedOrdersCount();
	long getCancelledOrdersCount();
}
