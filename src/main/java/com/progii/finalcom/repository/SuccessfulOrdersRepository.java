package com.progii.finalcom.repository;

import com.progii.finalcom.domain.SuccessfulOrders;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SuccessfulOrders entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SuccessfulOrdersRepository extends JpaRepository<SuccessfulOrders, Long> {
    public List<SuccessfulOrders> findByEstadoFalse();
}
