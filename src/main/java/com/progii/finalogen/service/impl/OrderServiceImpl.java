package com.progii.finalogen.service.impl;

import com.progii.finalogen.domain.Order;
import com.progii.finalogen.repository.OrderRepository;
import com.progii.finalogen.service.OrderService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Order}.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order save(Order order) {
        log.debug("Request to save Order : {}", order);
        return orderRepository.save(order);
    }

    @Override
    public Order update(Order order) {
        log.debug("Request to update Order : {}", order);
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> partialUpdate(Order order) {
        log.debug("Request to partially update Order : {}", order);

        return orderRepository
            .findById(order.getId())
            .map(existingOrder -> {
                if (order.getCliente() != null) {
                    existingOrder.setCliente(order.getCliente());
                }
                if (order.getAccionId() != null) {
                    existingOrder.setAccionId(order.getAccionId());
                }
                if (order.getAccion() != null) {
                    existingOrder.setAccion(order.getAccion());
                }
                if (order.getOperacion() != null) {
                    existingOrder.setOperacion(order.getOperacion());
                }
                if (order.getPrecio() != null) {
                    existingOrder.setPrecio(order.getPrecio());
                }
                if (order.getCantidad() != null) {
                    existingOrder.setCantidad(order.getCantidad());
                }
                if (order.getFechaOperacion() != null) {
                    existingOrder.setFechaOperacion(order.getFechaOperacion());
                }
                if (order.getModo() != null) {
                    existingOrder.setModo(order.getModo());
                }
                if (order.getEstado() != null) {
                    existingOrder.setEstado(order.getEstado());
                }

                return existingOrder;
            })
            .map(orderRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findOne(Long id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }
}
