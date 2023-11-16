package com.progii.finalcom.service.impl;

import com.progii.finalcom.domain.SuccessfulOrders;
import com.progii.finalcom.repository.SuccessfulOrdersRepository;
import com.progii.finalcom.service.SuccessfulOrdersService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SuccessfulOrders}.
 */
@Service
@Transactional
public class SuccessfulOrdersServiceImpl implements SuccessfulOrdersService {

    private final Logger log = LoggerFactory.getLogger(SuccessfulOrdersServiceImpl.class);

    private final SuccessfulOrdersRepository successfulOrdersRepository;

    public SuccessfulOrdersServiceImpl(SuccessfulOrdersRepository successfulOrdersRepository) {
        this.successfulOrdersRepository = successfulOrdersRepository;
    }

    @Override
    public SuccessfulOrders save(SuccessfulOrders successfulOrders) {
        log.debug("Request to save SuccessfulOrders : {}", successfulOrders);
        return successfulOrdersRepository.save(successfulOrders);
    }

    @Override
    public SuccessfulOrders update(SuccessfulOrders successfulOrders) {
        log.debug("Request to update SuccessfulOrders : {}", successfulOrders);
        return successfulOrdersRepository.save(successfulOrders);
    }

    @Override
    public Optional<SuccessfulOrders> partialUpdate(SuccessfulOrders successfulOrders) {
        log.debug("Request to partially update SuccessfulOrders : {}", successfulOrders);

        return successfulOrdersRepository
            .findById(successfulOrders.getId())
            .map(existingSuccessfulOrders -> {
                if (successfulOrders.getCliente() != null) {
                    existingSuccessfulOrders.setCliente(successfulOrders.getCliente());
                }
                if (successfulOrders.getAccionId() != null) {
                    existingSuccessfulOrders.setAccionId(successfulOrders.getAccionId());
                }
                if (successfulOrders.getAccion() != null) {
                    existingSuccessfulOrders.setAccion(successfulOrders.getAccion());
                }
                if (successfulOrders.getOperacion() != null) {
                    existingSuccessfulOrders.setOperacion(successfulOrders.getOperacion());
                }
                if (successfulOrders.getPrecio() != null) {
                    existingSuccessfulOrders.setPrecio(successfulOrders.getPrecio());
                }
                if (successfulOrders.getCantidad() != null) {
                    existingSuccessfulOrders.setCantidad(successfulOrders.getCantidad());
                }
                if (successfulOrders.getFechaOperacion() != null) {
                    existingSuccessfulOrders.setFechaOperacion(successfulOrders.getFechaOperacion());
                }
                if (successfulOrders.getModo() != null) {
                    existingSuccessfulOrders.setModo(successfulOrders.getModo());
                }
                if (successfulOrders.getOperacionExitosa() != null) {
                    existingSuccessfulOrders.setOperacionExitosa(successfulOrders.getOperacionExitosa());
                }
                if (successfulOrders.getOperacionObservaciones() != null) {
                    existingSuccessfulOrders.setOperacionObservaciones(successfulOrders.getOperacionObservaciones());
                }

                return existingSuccessfulOrders;
            })
            .map(successfulOrdersRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SuccessfulOrders> findAll(Pageable pageable) {
        log.debug("Request to get all SuccessfulOrders");
        return successfulOrdersRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SuccessfulOrders> findOne(Long id) {
        log.debug("Request to get SuccessfulOrders : {}", id);
        return successfulOrdersRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SuccessfulOrders : {}", id);
        successfulOrdersRepository.deleteById(id);
    }
}
