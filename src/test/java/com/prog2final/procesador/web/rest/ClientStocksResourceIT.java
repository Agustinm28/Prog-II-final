package com.prog2final.procesador.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.prog2final.procesador.IntegrationTest;
import com.prog2final.procesador.domain.ClientStocks;
import com.prog2final.procesador.repository.ClientStocksRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClientStocksResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientStocksResourceIT {

    private static final Long DEFAULT_CLIENT_ID = 1L;
    private static final Long UPDATED_CLIENT_ID = 2L;

    private static final String DEFAULT_STOCK_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STOCK_CODE = "BBBBBBBBBB";

    private static final Double DEFAULT_STOCK_AMOUNT = 1D;
    private static final Double UPDATED_STOCK_AMOUNT = 2D;

    private static final String ENTITY_API_URL = "/api/client-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClientStocksRepository clientStocksRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientStocksMockMvc;

    private ClientStocks clientStocks;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientStocks createEntity(EntityManager em) {
        ClientStocks clientStocks = new ClientStocks()
            .clientId(DEFAULT_CLIENT_ID)
            .stockCode(DEFAULT_STOCK_CODE)
            .stockAmount(DEFAULT_STOCK_AMOUNT);
        return clientStocks;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientStocks createUpdatedEntity(EntityManager em) {
        ClientStocks clientStocks = new ClientStocks()
            .clientId(UPDATED_CLIENT_ID)
            .stockCode(UPDATED_STOCK_CODE)
            .stockAmount(UPDATED_STOCK_AMOUNT);
        return clientStocks;
    }

    @BeforeEach
    public void initTest() {
        clientStocks = createEntity(em);
    }

    @Test
    @Transactional
    void createClientStocks() throws Exception {
        int databaseSizeBeforeCreate = clientStocksRepository.findAll().size();
        // Create the ClientStocks
        restClientStocksMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientStocks)))
            .andExpect(status().isCreated());

        // Validate the ClientStocks in the database
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeCreate + 1);
        ClientStocks testClientStocks = clientStocksList.get(clientStocksList.size() - 1);
        assertThat(testClientStocks.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
        assertThat(testClientStocks.getStockCode()).isEqualTo(DEFAULT_STOCK_CODE);
        assertThat(testClientStocks.getStockAmount()).isEqualTo(DEFAULT_STOCK_AMOUNT);
    }

    @Test
    @Transactional
    void createClientStocksWithExistingId() throws Exception {
        // Create the ClientStocks with an existing ID
        clientStocks.setId(1L);

        int databaseSizeBeforeCreate = clientStocksRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientStocksMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientStocks)))
            .andExpect(status().isBadRequest());

        // Validate the ClientStocks in the database
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClientStocks() throws Exception {
        // Initialize the database
        clientStocksRepository.saveAndFlush(clientStocks);

        // Get all the clientStocksList
        restClientStocksMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientStocks.getId().intValue())))
            .andExpect(jsonPath("$.[*].clientId").value(hasItem(DEFAULT_CLIENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].stockCode").value(hasItem(DEFAULT_STOCK_CODE)))
            .andExpect(jsonPath("$.[*].stockAmount").value(hasItem(DEFAULT_STOCK_AMOUNT.doubleValue())));
    }

    @Test
    @Transactional
    void getClientStocks() throws Exception {
        // Initialize the database
        clientStocksRepository.saveAndFlush(clientStocks);

        // Get the clientStocks
        restClientStocksMockMvc
            .perform(get(ENTITY_API_URL_ID, clientStocks.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientStocks.getId().intValue()))
            .andExpect(jsonPath("$.clientId").value(DEFAULT_CLIENT_ID.intValue()))
            .andExpect(jsonPath("$.stockCode").value(DEFAULT_STOCK_CODE))
            .andExpect(jsonPath("$.stockAmount").value(DEFAULT_STOCK_AMOUNT.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingClientStocks() throws Exception {
        // Get the clientStocks
        restClientStocksMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClientStocks() throws Exception {
        // Initialize the database
        clientStocksRepository.saveAndFlush(clientStocks);

        int databaseSizeBeforeUpdate = clientStocksRepository.findAll().size();

        // Update the clientStocks
        ClientStocks updatedClientStocks = clientStocksRepository.findById(clientStocks.getId()).get();
        // Disconnect from session so that the updates on updatedClientStocks are not directly saved in db
        em.detach(updatedClientStocks);
        updatedClientStocks.clientId(UPDATED_CLIENT_ID).stockCode(UPDATED_STOCK_CODE).stockAmount(UPDATED_STOCK_AMOUNT);

        restClientStocksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClientStocks.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedClientStocks))
            )
            .andExpect(status().isOk());

        // Validate the ClientStocks in the database
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeUpdate);
        ClientStocks testClientStocks = clientStocksList.get(clientStocksList.size() - 1);
        assertThat(testClientStocks.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testClientStocks.getStockCode()).isEqualTo(UPDATED_STOCK_CODE);
        assertThat(testClientStocks.getStockAmount()).isEqualTo(UPDATED_STOCK_AMOUNT);
    }

    @Test
    @Transactional
    void putNonExistingClientStocks() throws Exception {
        int databaseSizeBeforeUpdate = clientStocksRepository.findAll().size();
        clientStocks.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientStocksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientStocks.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientStocks))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientStocks in the database
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientStocks() throws Exception {
        int databaseSizeBeforeUpdate = clientStocksRepository.findAll().size();
        clientStocks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientStocksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientStocks))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientStocks in the database
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientStocks() throws Exception {
        int databaseSizeBeforeUpdate = clientStocksRepository.findAll().size();
        clientStocks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientStocksMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientStocks)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientStocks in the database
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientStocksWithPatch() throws Exception {
        // Initialize the database
        clientStocksRepository.saveAndFlush(clientStocks);

        int databaseSizeBeforeUpdate = clientStocksRepository.findAll().size();

        // Update the clientStocks using partial update
        ClientStocks partialUpdatedClientStocks = new ClientStocks();
        partialUpdatedClientStocks.setId(clientStocks.getId());

        partialUpdatedClientStocks.stockAmount(UPDATED_STOCK_AMOUNT);

        restClientStocksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientStocks.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientStocks))
            )
            .andExpect(status().isOk());

        // Validate the ClientStocks in the database
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeUpdate);
        ClientStocks testClientStocks = clientStocksList.get(clientStocksList.size() - 1);
        assertThat(testClientStocks.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
        assertThat(testClientStocks.getStockCode()).isEqualTo(DEFAULT_STOCK_CODE);
        assertThat(testClientStocks.getStockAmount()).isEqualTo(UPDATED_STOCK_AMOUNT);
    }

    @Test
    @Transactional
    void fullUpdateClientStocksWithPatch() throws Exception {
        // Initialize the database
        clientStocksRepository.saveAndFlush(clientStocks);

        int databaseSizeBeforeUpdate = clientStocksRepository.findAll().size();

        // Update the clientStocks using partial update
        ClientStocks partialUpdatedClientStocks = new ClientStocks();
        partialUpdatedClientStocks.setId(clientStocks.getId());

        partialUpdatedClientStocks.clientId(UPDATED_CLIENT_ID).stockCode(UPDATED_STOCK_CODE).stockAmount(UPDATED_STOCK_AMOUNT);

        restClientStocksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientStocks.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientStocks))
            )
            .andExpect(status().isOk());

        // Validate the ClientStocks in the database
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeUpdate);
        ClientStocks testClientStocks = clientStocksList.get(clientStocksList.size() - 1);
        assertThat(testClientStocks.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testClientStocks.getStockCode()).isEqualTo(UPDATED_STOCK_CODE);
        assertThat(testClientStocks.getStockAmount()).isEqualTo(UPDATED_STOCK_AMOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingClientStocks() throws Exception {
        int databaseSizeBeforeUpdate = clientStocksRepository.findAll().size();
        clientStocks.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientStocksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientStocks.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientStocks))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientStocks in the database
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientStocks() throws Exception {
        int databaseSizeBeforeUpdate = clientStocksRepository.findAll().size();
        clientStocks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientStocksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientStocks))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientStocks in the database
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientStocks() throws Exception {
        int databaseSizeBeforeUpdate = clientStocksRepository.findAll().size();
        clientStocks.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientStocksMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(clientStocks))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientStocks in the database
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientStocks() throws Exception {
        // Initialize the database
        clientStocksRepository.saveAndFlush(clientStocks);

        int databaseSizeBeforeDelete = clientStocksRepository.findAll().size();

        // Delete the clientStocks
        restClientStocksMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientStocks.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClientStocks> clientStocksList = clientStocksRepository.findAll();
        assertThat(clientStocksList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
