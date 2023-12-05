package com.progii.finalogen.repository;

import com.progii.finalogen.domain.Order;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAll(Specification<Order> specification);
}
