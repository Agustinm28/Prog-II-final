package com.prog2final.procesador.repository;

import com.prog2final.procesador.domain.OrderHistory;
import com.prog2final.procesador.domain.enumeration.Estado;
import com.prog2final.procesador.domain.enumeration.Modo;
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
    List<OrderHistory> findAllByCliente(Long cliente);
    List<OrderHistory> findAllByAccionId(Long accionId);
    List<OrderHistory> findAllByAccion(String accion);
    List<OrderHistory> findAllByEstado(Estado estado);
    List<OrderHistory> findAllByModoAndEstadoOrderByFechaOperacion(Modo modo, Estado estado);
    List<OrderHistory> findAllByFechaOperacionBetween(Instant fechaInicial, Instant fechaFinal);
    void deleteAllByEstado(Estado estado);
}
