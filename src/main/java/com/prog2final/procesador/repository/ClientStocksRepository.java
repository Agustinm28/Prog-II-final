package com.prog2final.procesador.repository;

import com.prog2final.procesador.domain.ClientStocks;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClientStocks entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientStocksRepository extends JpaRepository<ClientStocks, Long> {}
