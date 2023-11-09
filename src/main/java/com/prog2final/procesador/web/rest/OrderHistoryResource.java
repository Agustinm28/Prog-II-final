package com.prog2final.procesador.web.rest;

import com.prog2final.procesador.domain.OrderHistory;
import com.prog2final.procesador.repository.OrderHistoryRepository;
import com.prog2final.procesador.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<OrderHistory> createOrderHistory(@RequestBody OrderHistory orderHistory) throws URISyntaxException {
        log.debug("REST request to save OrderHistory : {}", orderHistory);
        if (orderHistory.getId() != null) {
            throw new BadRequestAlertException("A new orderHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
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
        @RequestBody OrderHistory orderHistory
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
        @RequestBody OrderHistory orderHistory
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
                if (orderHistory.getClientId() != null) {
                    existingOrderHistory.setClientId(orderHistory.getClientId());
                }
                if (orderHistory.getStockId() != null) {
                    existingOrderHistory.setStockId(orderHistory.getStockId());
                }
                if (orderHistory.getOperationType() != null) {
                    existingOrderHistory.setOperationType(orderHistory.getOperationType());
                }
                if (orderHistory.getPrice() != null) {
                    existingOrderHistory.setPrice(orderHistory.getPrice());
                }
                if (orderHistory.getAmount() != null) {
                    existingOrderHistory.setAmount(orderHistory.getAmount());
                }
                if (orderHistory.getOperationDate() != null) {
                    existingOrderHistory.setOperationDate(orderHistory.getOperationDate());
                }
                if (orderHistory.getMode() != null) {
                    existingOrderHistory.setMode(orderHistory.getMode());
                }
                if (orderHistory.getState() != null) {
                    existingOrderHistory.setState(orderHistory.getState());
                }
                if (orderHistory.getInfo() != null) {
                    existingOrderHistory.setInfo(orderHistory.getInfo());
                }
                if (orderHistory.getLanguage() != null) {
                    existingOrderHistory.setLanguage(orderHistory.getLanguage());
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
    public List<OrderHistory> getAllOrderHistories() {
        log.debug("REST request to get all OrderHistories");
        return orderHistoryRepository.findAll();
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
