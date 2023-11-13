package com.prog2final.procesador.web.rest;

import com.prog2final.procesador.domain.ClientStocks;
import com.prog2final.procesador.repository.ClientStocksRepository;
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
 * REST controller for managing {@link com.prog2final.procesador.domain.ClientStocks}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ClientStocksResource {

    private final Logger log = LoggerFactory.getLogger(ClientStocksResource.class);

    private static final String ENTITY_NAME = "clientStocks";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientStocksRepository clientStocksRepository;

    public ClientStocksResource(ClientStocksRepository clientStocksRepository) {
        this.clientStocksRepository = clientStocksRepository;
    }

    /**
     * {@code POST  /client-stocks} : Create a new clientStocks.
     *
     * @param clientStocks the clientStocks to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientStocks, or with status {@code 400 (Bad Request)} if the clientStocks has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/client-stocks")
    public ResponseEntity<ClientStocks> createClientStocks(@RequestBody ClientStocks clientStocks) throws URISyntaxException {
        log.debug("REST request to save ClientStocks : {}", clientStocks);
        if (clientStocks.getId() != null) {
            throw new BadRequestAlertException("A new clientStocks cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClientStocks result = clientStocksRepository.save(clientStocks);
        return ResponseEntity
            .created(new URI("/api/client-stocks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /client-stocks/:id} : Updates an existing clientStocks.
     *
     * @param id the id of the clientStocks to save.
     * @param clientStocks the clientStocks to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientStocks,
     * or with status {@code 400 (Bad Request)} if the clientStocks is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientStocks couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/client-stocks/{id}")
    public ResponseEntity<ClientStocks> updateClientStocks(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClientStocks clientStocks
    ) throws URISyntaxException {
        log.debug("REST request to update ClientStocks : {}, {}", id, clientStocks);
        if (clientStocks.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientStocks.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientStocksRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ClientStocks result = clientStocksRepository.save(clientStocks);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientStocks.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /client-stocks/:id} : Partial updates given fields of an existing clientStocks, field will ignore if it is null
     *
     * @param id the id of the clientStocks to save.
     * @param clientStocks the clientStocks to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientStocks,
     * or with status {@code 400 (Bad Request)} if the clientStocks is not valid,
     * or with status {@code 404 (Not Found)} if the clientStocks is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientStocks couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/client-stocks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientStocks> partialUpdateClientStocks(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ClientStocks clientStocks
    ) throws URISyntaxException {
        log.debug("REST request to partial update ClientStocks partially : {}, {}", id, clientStocks);
        if (clientStocks.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientStocks.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientStocksRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientStocks> result = clientStocksRepository
            .findById(clientStocks.getId())
            .map(existingClientStocks -> {
                if (clientStocks.getClientId() != null) {
                    existingClientStocks.setClientId(clientStocks.getClientId());
                }
                if (clientStocks.getStockCode() != null) {
                    existingClientStocks.setStockCode(clientStocks.getStockCode());
                }
                if (clientStocks.getStockAmount() != null) {
                    existingClientStocks.setStockAmount(clientStocks.getStockAmount());
                }

                return existingClientStocks;
            })
            .map(clientStocksRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clientStocks.getId().toString())
        );
    }

    /**
     * {@code GET  /client-stocks} : get all the clientStocks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientStocks in body.
     */
    @GetMapping("/client-stocks")
    public List<ClientStocks> getAllClientStocks() {
        log.debug("REST request to get all ClientStocks");
        return clientStocksRepository.findAll();
    }

    /**
     * {@code GET  /client-stocks/:id} : get the "id" clientStocks.
     *
     * @param id the id of the clientStocks to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientStocks, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/client-stocks/{id}")
    public ResponseEntity<ClientStocks> getClientStocks(@PathVariable Long id) {
        log.debug("REST request to get ClientStocks : {}", id);
        Optional<ClientStocks> clientStocks = clientStocksRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(clientStocks);
    }

    /**
     * {@code DELETE  /client-stocks/:id} : delete the "id" clientStocks.
     *
     * @param id the id of the clientStocks to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/client-stocks/{id}")
    public ResponseEntity<Void> deleteClientStocks(@PathVariable Long id) {
        log.debug("REST request to delete ClientStocks : {}", id);
        clientStocksRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
