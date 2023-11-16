package com.progii.finalcom.service;

import com.progii.finalcom.domain.SuccessfulOrders;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link SuccessfulOrders}.
 */
public interface SuccessfulOrdersService {
    /**
     * Save a successfulOrders.
     *
     * @param successfulOrders the entity to save.
     * @return the persisted entity.
     */
    SuccessfulOrders save(SuccessfulOrders successfulOrders);

    /**
     * Updates a successfulOrders.
     *
     * @param successfulOrders the entity to update.
     * @return the persisted entity.
     */
    SuccessfulOrders update(SuccessfulOrders successfulOrders);

    /**
     * Partially updates a successfulOrders.
     *
     * @param successfulOrders the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SuccessfulOrders> partialUpdate(SuccessfulOrders successfulOrders);

    /**
     * Get all the successfulOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SuccessfulOrders> findAll(Pageable pageable);

    /**
     * Get the "id" successfulOrders.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SuccessfulOrders> findOne(Long id);

    /**
     * Delete the "id" successfulOrders.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
