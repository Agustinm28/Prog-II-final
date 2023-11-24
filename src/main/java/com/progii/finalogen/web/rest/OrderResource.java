package com.progii.finalogen.web.rest;

import com.progii.finalogen.aop.logging.ColorLogs;
import com.progii.finalogen.domain.Order;
import com.progii.finalogen.domain.enumeration.Estado;
import com.progii.finalogen.repository.OrderRepository;
import com.progii.finalogen.service.AditionalOrderServices;
import com.progii.finalogen.service.OrderService;
import com.progii.finalogen.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.progii.finalogen.domain.Order}.
 */
@RestController
@RequestMapping("/api")
public class OrderResource {

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);

    private static final String ENTITY_NAME = "order";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderService orderService;

    private final OrderRepository orderRepository;

    private final AditionalOrderServices aditionalOrderServices;

    public OrderResource(OrderService orderService, OrderRepository orderRepository, AditionalOrderServices aditionalOrderServices) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.aditionalOrderServices = aditionalOrderServices;
    }

    /**
     * {@code POST  /orders} : Create a new order.
     *
     * @param order the order to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new order, or with status {@code 400 (Bad Request)} if the order has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ordenes")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) throws URISyntaxException {
        log.info("{}REST request to save Order{}", ColorLogs.BLUE, ColorLogs.RESET);
        if (order.getId() != null) {
            throw new BadRequestAlertException("A new order cannot already have an ID", ENTITY_NAME, "idexists");
        }

        aditionalOrderServices.reviewOrder(order);

        Order result = orderService.save(order);
        return ResponseEntity
            .created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /orders/espejo} : Create a list of new orders.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new order, or with status {@code 400 (Bad Request)} if the order has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ordenes/espejo")
    public ResponseEntity<Map<String, Object>> createOrdersMirror(@RequestBody Map<String, List<Order>> requestBody)
        throws URISyntaxException {
        log.info("{}REST request to save Order list{}", ColorLogs.BLUE, requestBody.get("ordenes"), ColorLogs.RESET);
        List<Order> orders = requestBody.get("ordenes");
        Map<String, Object> responseMap = new HashMap<>();

        if (orders != null) {
            List<Map<String, Object>> orderResponses = new ArrayList<>();

            for (Order order : orders) {
                try {
                    // Create order from list of orders
                    ResponseEntity<Order> orderResponse = createOrder(order);

                    Map<String, Object> orderResponseMap = new HashMap<>();
                    orderResponseMap.put("status", orderResponse.getStatusCodeValue());
                    orderResponseMap.put("body", orderResponse.getBody());

                    orderResponses.add(orderResponseMap);
                } catch (Exception e) {
                    // Handle error and log the exception
                    log.error("Error creating order: {}", e.getMessage());

                    // Add operation to list
                    Map<String, Object> operation = new HashMap<>();
                    operation.put("order", order);
                    operation.put("status", "ERROR: " + e.getMessage());
                    orderResponses.add(operation);
                }
            }
            responseMap.put("orders", orderResponses);
        }
        return ResponseEntity.ok(responseMap);
    }

    /**
     * {@code PUT  /orders/:id} : Updates an existing order.
     *
     * @param id the id of the order to save.
     * @param order the order to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated order,
     * or with status {@code 400 (Bad Request)} if the order is not valid,
     * or with status {@code 500 (Internal Server Error)} if the order couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ordenes/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable(value = "id", required = false) final Long id, @RequestBody Order order)
        throws URISyntaxException {
        log.info("{}REST request to update Order: {}, {}{}", ColorLogs.BLUE, id, order, ColorLogs.RESET);
        if (order.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, order.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Order result = orderService.update(order);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, order.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /orders/:id} : Partial updates given fields of an existing order, field will ignore if it is null
     *
     * @param id the id of the order to save.
     * @param order the order to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated order,
     * or with status {@code 400 (Bad Request)} if the order is not valid,
     * or with status {@code 404 (Not Found)} if the order is not found,
     * or with status {@code 500 (Internal Server Error)} if the order couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ordenes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Order> partialUpdateOrder(@PathVariable(value = "id", required = false) final Long id, @RequestBody Order order)
        throws URISyntaxException {
        log.info("{}REST request to partial update Order partially: {}, {}{}", ColorLogs.BLUE, id, order, ColorLogs.RESET);
        if (order.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, order.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Order> result = orderService.partialUpdate(order);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, order.getId().toString())
        );
    }

    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("/ordenes")
    public ResponseEntity<List<Order>> getAllOrders(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.info("{}REST request to get Orders{}", ColorLogs.BLUE, ColorLogs.RESET);
        Page<Order> page = orderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /orders/:id} : get the "id" order.
     *
     * @param id the id of the order to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the order, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ordenes/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        log.info("{}REST request to get Order: {}{}", ColorLogs.BLUE, id, ColorLogs.RESET);
        Optional<Order> order = orderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(order);
    }

    @GetMapping("/ordenes/procesar")
    public ResponseEntity<List<Map<String, Object>>> processOrders(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.info("{}REST request to process Orders{}", ColorLogs.BLUE, ColorLogs.RESET);
        // Get all orders
        Page<Order> ordersPage = orderRepository.findAll(pageable);
        List<Order> orders = ordersPage.getContent();

        // Filter orders by status and format them to delete id and status fields
        List<Order> pending_orders = aditionalOrderServices.searchByFilter(orders, null, null, null, null, "PENDIENTE");
        List<Map<String, Object>> formated_orders = aditionalOrderServices.formatList(pending_orders);

        // Update orders status to ENVIADO
        for (Order order : pending_orders) {
            order.setEstado(Estado.ENVIADO);
            orderService.update(order);
        }

        return ResponseEntity.ok(formated_orders);
    }

    @GetMapping("/ordenes/buscar")
    public ResponseEntity<List<Order>> findByFilter(
        @RequestParam(required = false) String cliente,
        @RequestParam(required = false) String accion,
        @RequestParam(required = false) String accion_id,
        @RequestParam(required = false) String operacion,
        @RequestParam(required = false) String estado
    ) {
        log.info("{}REST request to get Order by filter{}", ColorLogs.BLUE, ColorLogs.RESET);
        List<Order> orders = orderRepository.findAll();

        List<Order> new_orders = aditionalOrderServices.searchByFilter(orders, cliente, accion, accion_id, operacion, estado);

        return ResponseEntity.ok(new_orders);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" order.
     *
     * @param id the id of the order to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ordenes/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        log.info("{}REST request to cancel Order: {}{}", ColorLogs.BLUE, id, ColorLogs.RESET);

        // Check if the order exists
        Optional<Order> order = orderService.findOne(id);
        if (!order.isPresent()) {
            throw new BadRequestAlertException("Order does not exist", ENTITY_NAME, "ordernotfound");
        }

        orderService.delete(id);

        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body("Success: Order with id " + id + " canceled successfully");
    }

    @DeleteMapping("/ordenes")
    public ResponseEntity<String> deleteOrders() {
        log.info("{}REST request to process Orders{}", ColorLogs.BLUE, ColorLogs.RESET);

        List<Order> orders = orderRepository.findAll();

        // NO SE USA ESTE ENDPOINT, PERO LO DEJO

        for (Order order : orders) {
            orderRepository.delete(order);
        }

        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, "all"))
            .body("Success: All orders procesed successfully");
    }
}
