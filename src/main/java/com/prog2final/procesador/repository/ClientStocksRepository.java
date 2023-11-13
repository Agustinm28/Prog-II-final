package com.prog2final.procesador.repository;

import com.prog2final.procesador.domain.ClientStocks;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClientStocks entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientStocksRepository extends JpaRepository<ClientStocks, Long> {
    Optional<ClientStocks> findOneByClientIdAndStockCodeAndStockAmountGreaterThanEqual(Long clientId, String stockCode, Double stockAmount);
    List<ClientStocks> findAllByClientId(Long clientId);
    List<ClientStocks> findAllByStockCode(String stockCode);
}
