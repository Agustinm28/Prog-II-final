package com.prog2final.procesador.web.rest;

import com.prog2final.procesador.domain.OrderHistory;
import com.prog2final.procesador.domain.enumeration.Estado;
import com.prog2final.procesador.repository.OrderHistoryRepository;
import com.prog2final.procesador.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.prog2final.procesador.domain.OrderHistory}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class OrderHistoryResource {

    private final Logger log = LoggerFactory.getLogger(OrderHistoryResource.class);

    private static final String ENTITY_NAME = "orderHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderHistoryRepository orderHistoryRepository;

    public OrderHistoryResource(OrderHistoryRepository orderHistoryRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
    }

    /**
     * {@code POST  /order-histories} : Create a new orderHistory.
     *
     * @param orderHistory the orderHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderHistory, or with status {@code 400 (Bad Request)} if the orderHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-histories")
    public ResponseEntity<OrderHistory> createOrderHistory(@Valid @RequestBody OrderHistory orderHistory) throws URISyntaxException {
        log.debug("REST request to save OrderHistory : {}", orderHistory);
        if (orderHistory.getId() != null) {
            throw new BadRequestAlertException("A new orderHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        orderHistory.estado(Estado.PENDIENTE).reportada(false).operacionObservaciones("Esperando procesamiento...");
        OrderHistory result = orderHistoryRepository.save(orderHistory);
        return ResponseEntity
            .created(new URI("/api/order-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /order-histories/:id} : Updates an existing orderHistory.
     *
     * @param id the id of the orderHistory to save.
     * @param orderHistory the orderHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderHistory,
     * or with status {@code 400 (Bad Request)} if the orderHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-histories/{id}")
    public ResponseEntity<OrderHistory> updateOrderHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrderHistory orderHistory
    ) throws URISyntaxException {
        log.debug("REST request to update OrderHistory : {}, {}", id, orderHistory);
        if (orderHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrderHistory result = orderHistoryRepository.save(orderHistory);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderHistory.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /order-histories/:id} : Partial updates given fields of an existing orderHistory, field will ignore if it is null
     *
     * @param id the id of the orderHistory to save.
     * @param orderHistory the orderHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderHistory,
     * or with status {@code 400 (Bad Request)} if the orderHistory is not valid,
     * or with status {@code 404 (Not Found)} if the orderHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/order-histories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderHistory> partialUpdateOrderHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrderHistory orderHistory
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderHistory partially : {}, {}", id, orderHistory);
        if (orderHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderHistory> result = orderHistoryRepository
            .findById(orderHistory.getId())
            .map(existingOrderHistory -> {
                if (orderHistory.getCliente() != null) {
                    existingOrderHistory.setCliente(orderHistory.getCliente());
                }
                if (orderHistory.getAccionId() != null) {
                    existingOrderHistory.setAccionId(orderHistory.getAccionId());
                }
                if (orderHistory.getAccion() != null) {
                    existingOrderHistory.setAccion(orderHistory.getAccion());
                }
                if (orderHistory.getOperacion() != null) {
                    existingOrderHistory.setOperacion(orderHistory.getOperacion());
                }
                if (orderHistory.getCantidad() != null) {
                    existingOrderHistory.setCantidad(orderHistory.getCantidad());
                }
                if (orderHistory.getPrecio() != null) {
                    existingOrderHistory.setPrecio(orderHistory.getPrecio());
                }
                if (orderHistory.getFechaOperacion() != null) {
                    existingOrderHistory.setFechaOperacion(orderHistory.getFechaOperacion());
                }
                if (orderHistory.getModo() != null) {
                    existingOrderHistory.setModo(orderHistory.getModo());
                }
                if (orderHistory.getEstado() != null) {
                    existingOrderHistory.setEstado(orderHistory.getEstado());
                }
                if (orderHistory.getReportada() != null) {
                    existingOrderHistory.setReportada(orderHistory.getReportada());
                }
                if (orderHistory.getOperacionObservaciones() != null) {
                    existingOrderHistory.setOperacionObservaciones(orderHistory.getOperacionObservaciones());
                }
                if (orderHistory.getFechaEjecucion() != null) {
                    existingOrderHistory.setFechaEjecucion(orderHistory.getFechaEjecucion());
                }

                return existingOrderHistory;
            })
            .map(orderHistoryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /order-histories} : get all the orderHistories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderHistories in body.
     */
    @GetMapping("/order-histories")
    @Secured("ROLE_ADMIN")
    public List<OrderHistory> getAllOrderHistoriesWithFilters(
        @RequestParam(name = "estado", required = false) Estado estado,
        @RequestParam(name = "clienteId", required = false) Long clienteId,
        @RequestParam(name = "accionId", required = false) Long accionId,
        @RequestParam(name = "fechaInicio", required = false) Instant fechaInicio,
        @RequestParam(name = "fechaFin", required = false) Instant fechaFin
    ) {
        log.debug("REST request to get all OrderHistories");
        List<OrderHistory> allOrderHistories = orderHistoryRepository.findAllByOrderByFechaOperacionAsc();
        if (estado != null) {
            switch (estado) {
                case EXITOSA:
                    allOrderHistories.removeIf(ord -> !Objects.equals(ord.getEstado(), Estado.EXITOSA));
                    break;
                case FALLIDA:
                    allOrderHistories.removeIf(ord -> !Objects.equals(ord.getEstado(), Estado.FALLIDA));
                    break;
                case PENDIENTE:
                    allOrderHistories.removeIf(ord -> !Objects.equals(ord.getEstado(), Estado.PENDIENTE));
                    break;
            }
        }
        if (clienteId != null) {
            allOrderHistories.removeIf(ord -> !Objects.equals(ord.getCliente(), clienteId));
        }
        if (accionId != null) {
            allOrderHistories.removeIf(ord -> !Objects.equals(ord.getAccionId(), accionId));
        }
        if (fechaInicio != null) {
            allOrderHistories.removeIf(ord -> ord.getFechaOperacion().compareTo(fechaInicio) < 0);
        }
        if (fechaFin != null) {
            allOrderHistories.removeIf(ord -> ord.getFechaOperacion().compareTo(fechaFin) > 0);
        }
        return allOrderHistories;
    }

    /**
     * {@code GET  /order-histories/:id} : get the "id" orderHistory.
     *
     * @param id the id of the orderHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-histories/{id}")
    public ResponseEntity<OrderHistory> getOrderHistory(@PathVariable Long id) {
        log.debug("REST request to get OrderHistory : {}", id);
        Optional<OrderHistory> orderHistory = orderHistoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(orderHistory);
    }

    /**
     * {@code DELETE  /order-histories/:id} : delete the "id" orderHistory.
     *
     * @param id the id of the orderHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-histories/{id}")
    public ResponseEntity<Void> deleteOrderHistory(@PathVariable Long id) {
        log.debug("REST request to delete OrderHistory : {}", id);
        orderHistoryRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
