package com.mcgill.ecse321.GameShop.repository;

import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.Order;

public interface OrderRepository extends CrudRepository<Order, String> {
   public Order findByTrackingNumber(String trackingNumber);
}