package com.gulcht.microservices.order.repository;

import com.gulcht.microservices.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepositoy extends JpaRepository<Order, Long> {

}
