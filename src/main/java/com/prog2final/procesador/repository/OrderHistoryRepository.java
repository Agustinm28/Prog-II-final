package com.prog2final.procesador.repository;

import com.prog2final.procesador.domain.OrderHistory;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findAllByClientId(Long clientId);
    List<OrderHistory> findAllByStockCode(String stockCode);
    List<OrderHistory> findAllByState(String state);
    List<OrderHistory> findAllByModeAndState(String mode, String state);
    List<OrderHistory> findAllByCreationDateBetween(Instant startOpDate, Instant endOptDate);
    void deleteAllByState(String state);
}
