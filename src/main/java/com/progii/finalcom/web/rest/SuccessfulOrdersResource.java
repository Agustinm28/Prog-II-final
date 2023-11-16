package com.progii.finalcom.web.rest;

import com.progii.finalcom.domain.SuccessfulOrders;
import com.progii.finalcom.repository.SuccessfulOrdersRepository;
import com.progii.finalcom.service.SuccessfulOrdersService;
import com.progii.finalcom.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.progii.finalcom.domain.SuccessfulOrders}.
 */
@RestController
@RequestMapping("/api")
public class SuccessfulOrdersResource {

    private final Logger log = LoggerFactory.getLogger(SuccessfulOrdersResource.class);

    private static final String ENTITY_NAME = "successfulOrders";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SuccessfulOrdersService successfulOrdersService;

    private final SuccessfulOrdersRepository successfulOrdersRepository;

    public SuccessfulOrdersResource(
        SuccessfulOrdersService successfulOrdersService,
        SuccessfulOrdersRepository successfulOrdersRepository
    ) {
        this.successfulOrdersService = successfulOrdersService;
        this.successfulOrdersRepository = successfulOrdersRepository;
    }

    /**
     * {@code POST  /successful-orders} : Create a new successfulOrders.
     *
     * @param successfulOrders the successfulOrders to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new successfulOrders, or with status {@code 400 (Bad Request)} if the successfulOrders has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/successful-orders")
    public ResponseEntity<SuccessfulOrders> createSuccessfulOrders(@RequestBody SuccessfulOrders successfulOrders)
        throws URISyntaxException {
        log.debug("REST request to save SuccessfulOrders : {}", successfulOrders);
        if (successfulOrders.getId() != null) {
            throw new BadRequestAlertException("A new successfulOrders cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SuccessfulOrders result = successfulOrdersService.save(successfulOrders);
        return ResponseEntity
            .created(new URI("/api/successful-orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /successful-orders/:id} : Updates an existing successfulOrders.
     *
     * @param id the id of the successfulOrders to save.
     * @param successfulOrders the successfulOrders to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated successfulOrders,
     * or with status {@code 400 (Bad Request)} if the successfulOrders is not valid,
     * or with status {@code 500 (Internal Server Error)} if the successfulOrders couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/successful-orders/{id}")
    public ResponseEntity<SuccessfulOrders> updateSuccessfulOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SuccessfulOrders successfulOrders
    ) throws URISyntaxException {
        log.debug("REST request to update SuccessfulOrders : {}, {}", id, successfulOrders);
        if (successfulOrders.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, successfulOrders.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!successfulOrdersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SuccessfulOrders result = successfulOrdersService.update(successfulOrders);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, successfulOrders.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /successful-orders/:id} : Partial updates given fields of an existing successfulOrders, field will ignore if it is null
     *
     * @param id the id of the successfulOrders to save.
     * @param successfulOrders the successfulOrders to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated successfulOrders,
     * or with status {@code 400 (Bad Request)} if the successfulOrders is not valid,
     * or with status {@code 404 (Not Found)} if the successfulOrders is not found,
     * or with status {@code 500 (Internal Server Error)} if the successfulOrders couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/successful-orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SuccessfulOrders> partialUpdateSuccessfulOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SuccessfulOrders successfulOrders
    ) throws URISyntaxException {
        log.debug("REST request to partial update SuccessfulOrders partially : {}, {}", id, successfulOrders);
        if (successfulOrders.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, successfulOrders.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!successfulOrdersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SuccessfulOrders> result = successfulOrdersService.partialUpdate(successfulOrders);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, successfulOrders.getId().toString())
        );
    }

    /**
     * {@code GET  /successful-orders} : get all the successfulOrders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of successfulOrders in body.
     */
    @GetMapping("/successful-orders")
    public ResponseEntity<List<SuccessfulOrders>> getAllSuccessfulOrders(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of SuccessfulOrders");
        Page<SuccessfulOrders> page = successfulOrdersService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /successful-orders/:id} : get the "id" successfulOrders.
     *
     * @param id the id of the successfulOrders to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the successfulOrders, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/successful-orders/{id}")
    public ResponseEntity<SuccessfulOrders> getSuccessfulOrders(@PathVariable Long id) {
        log.debug("REST request to get SuccessfulOrders : {}", id);
        Optional<SuccessfulOrders> successfulOrders = successfulOrdersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(successfulOrders);
    }

    /**
     * {@code DELETE  /successful-orders/:id} : delete the "id" successfulOrders.
     *
     * @param id the id of the successfulOrders to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/successful-orders/{id}")
    public ResponseEntity<Void> deleteSuccessfulOrders(@PathVariable Long id) {
        log.debug("REST request to delete SuccessfulOrders : {}", id);
        successfulOrdersService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
