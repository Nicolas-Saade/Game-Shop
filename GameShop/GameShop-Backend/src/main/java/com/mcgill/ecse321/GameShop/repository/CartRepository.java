package com.mcgill.ecse321.GameShop.repository;

import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.Cart;

public interface CartRepository extends CrudRepository<Cart, Integer> {
   public Cart findById(int cart_id);
}