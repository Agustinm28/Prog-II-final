package com.prog2final.procesador.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.prog2final.procesador.IntegrationTest;
import com.prog2final.procesador.domain.OrderHistory;
import com.prog2final.procesador.domain.enumeration.Language;
import com.prog2final.procesador.repository.OrderHistoryRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link OrderHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderHistoryResourceIT {

    private static final Long DEFAULT_CLIENT_ID = 1L;
    private static final Long UPDATED_CLIENT_ID = 2L;

    private static final String DEFAULT_STOCK_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STOCK_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_OPERATION_TYPE = false;
    private static final Boolean UPDATED_OPERATION_TYPE = true;

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    private static final Double DEFAULT_AMOUNT = 1D;
    private static final Double UPDATED_AMOUNT = 2D;

    private static final Instant DEFAULT_CREATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXECUTION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXECUTION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MODE = "AAAAAAAAAA";
    private static final String UPDATED_MODE = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_INFO = "AAAAAAAAAA";
    private static final String UPDATED_INFO = "BBBBBBBBBB";

    private static final Language DEFAULT_LANGUAGE = Language.ENGLISH;
    private static final Language UPDATED_LANGUAGE = Language.SPANISH;

    private static final String ENTITY_API_URL = "/api/order-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderHistoryMockMvc;

    private OrderHistory orderHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderHistory createEntity(EntityManager em) {
        OrderHistory orderHistory = new OrderHistory()
            .clientId(DEFAULT_CLIENT_ID)
            .stockCode(DEFAULT_STOCK_CODE)
            .operationType(DEFAULT_OPERATION_TYPE)
            .price(DEFAULT_PRICE)
            .amount(DEFAULT_AMOUNT)
            .creationDate(DEFAULT_CREATION_DATE)
            .executionDate(DEFAULT_EXECUTION_DATE)
            .mode(DEFAULT_MODE)
            .state(DEFAULT_STATE)
            .info(DEFAULT_INFO)
            .language(DEFAULT_LANGUAGE);
        return orderHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderHistory createUpdatedEntity(EntityManager em) {
        OrderHistory orderHistory = new OrderHistory()
            .clientId(UPDATED_CLIENT_ID)
            .stockCode(UPDATED_STOCK_CODE)
            .operationType(UPDATED_OPERATION_TYPE)
            .price(UPDATED_PRICE)
            .amount(UPDATED_AMOUNT)
            .creationDate(UPDATED_CREATION_DATE)
            .executionDate(UPDATED_EXECUTION_DATE)
            .mode(UPDATED_MODE)
            .state(UPDATED_STATE)
            .info(UPDATED_INFO)
            .language(UPDATED_LANGUAGE);
        return orderHistory;
    }

    @BeforeEach
    public void initTest() {
        orderHistory = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderHistory() throws Exception {
        int databaseSizeBeforeCreate = orderHistoryRepository.findAll().size();
        // Create the OrderHistory
        restOrderHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderHistory)))
            .andExpect(status().isCreated());

        // Validate the OrderHistory in the database
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        OrderHistory testOrderHistory = orderHistoryList.get(orderHistoryList.size() - 1);
        assertThat(testOrderHistory.getClientId()).isEqualTo(DEFAULT_CLIENT_ID);
        assertThat(testOrderHistory.getStockCode()).isEqualTo(DEFAULT_STOCK_CODE);
        assertThat(testOrderHistory.getOperationType()).isEqualTo(DEFAULT_OPERATION_TYPE);
        assertThat(testOrderHistory.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testOrderHistory.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testOrderHistory.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testOrderHistory.getExecutionDate()).isEqualTo(DEFAULT_EXECUTION_DATE);
        assertThat(testOrderHistory.getMode()).isEqualTo(DEFAULT_MODE);
        assertThat(testOrderHistory.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testOrderHistory.getInfo()).isEqualTo(DEFAULT_INFO);
        assertThat(testOrderHistory.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    void createOrderHistoryWithExistingId() throws Exception {
        // Create the OrderHistory with an existing ID
        orderHistory.setId(1L);

        int databaseSizeBeforeCreate = orderHistoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderHistory)))
            .andExpect(status().isBadRequest());

        // Validate the OrderHistory in the database
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOrderHistories() throws Exception {
        // Initialize the database
        orderHistoryRepository.saveAndFlush(orderHistory);

        // Get all the orderHistoryList
        restOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].clientId").value(hasItem(DEFAULT_CLIENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].stockCode").value(hasItem(DEFAULT_STOCK_CODE)))
            .andExpect(jsonPath("$.[*].operationType").value(hasItem(DEFAULT_OPERATION_TYPE.booleanValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].executionDate").value(hasItem(DEFAULT_EXECUTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].info").value(hasItem(DEFAULT_INFO)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }

    @Test
    @Transactional
    void getOrderHistory() throws Exception {
        // Initialize the database
        orderHistoryRepository.saveAndFlush(orderHistory);

        // Get the orderHistory
        restOrderHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, orderHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderHistory.getId().intValue()))
            .andExpect(jsonPath("$.clientId").value(DEFAULT_CLIENT_ID.intValue()))
            .andExpect(jsonPath("$.stockCode").value(DEFAULT_STOCK_CODE))
            .andExpect(jsonPath("$.operationType").value(DEFAULT_OPERATION_TYPE.booleanValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.executionDate").value(DEFAULT_EXECUTION_DATE.toString()))
            .andExpect(jsonPath("$.mode").value(DEFAULT_MODE))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE))
            .andExpect(jsonPath("$.info").value(DEFAULT_INFO))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingOrderHistory() throws Exception {
        // Get the orderHistory
        restOrderHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderHistory() throws Exception {
        // Initialize the database
        orderHistoryRepository.saveAndFlush(orderHistory);

        int databaseSizeBeforeUpdate = orderHistoryRepository.findAll().size();

        // Update the orderHistory
        OrderHistory updatedOrderHistory = orderHistoryRepository.findById(orderHistory.getId()).get();
        // Disconnect from session so that the updates on updatedOrderHistory are not directly saved in db
        em.detach(updatedOrderHistory);
        updatedOrderHistory
            .clientId(UPDATED_CLIENT_ID)
            .stockCode(UPDATED_STOCK_CODE)
            .operationType(UPDATED_OPERATION_TYPE)
            .price(UPDATED_PRICE)
            .amount(UPDATED_AMOUNT)
            .creationDate(UPDATED_CREATION_DATE)
            .executionDate(UPDATED_EXECUTION_DATE)
            .mode(UPDATED_MODE)
            .state(UPDATED_STATE)
            .info(UPDATED_INFO)
            .language(UPDATED_LANGUAGE);

        restOrderHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrderHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrderHistory))
            )
            .andExpect(status().isOk());

        // Validate the OrderHistory in the database
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeUpdate);
        OrderHistory testOrderHistory = orderHistoryList.get(orderHistoryList.size() - 1);
        assertThat(testOrderHistory.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testOrderHistory.getStockCode()).isEqualTo(UPDATED_STOCK_CODE);
        assertThat(testOrderHistory.getOperationType()).isEqualTo(UPDATED_OPERATION_TYPE);
        assertThat(testOrderHistory.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testOrderHistory.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testOrderHistory.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testOrderHistory.getExecutionDate()).isEqualTo(UPDATED_EXECUTION_DATE);
        assertThat(testOrderHistory.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testOrderHistory.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testOrderHistory.getInfo()).isEqualTo(UPDATED_INFO);
        assertThat(testOrderHistory.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void putNonExistingOrderHistory() throws Exception {
        int databaseSizeBeforeUpdate = orderHistoryRepository.findAll().size();
        orderHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderHistory in the database
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderHistory() throws Exception {
        int databaseSizeBeforeUpdate = orderHistoryRepository.findAll().size();
        orderHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderHistory in the database
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderHistory() throws Exception {
        int databaseSizeBeforeUpdate = orderHistoryRepository.findAll().size();
        orderHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderHistory in the database
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderHistoryWithPatch() throws Exception {
        // Initialize the database
        orderHistoryRepository.saveAndFlush(orderHistory);

        int databaseSizeBeforeUpdate = orderHistoryRepository.findAll().size();

        // Update the orderHistory using partial update
        OrderHistory partialUpdatedOrderHistory = new OrderHistory();
        partialUpdatedOrderHistory.setId(orderHistory.getId());

        partialUpdatedOrderHistory
            .clientId(UPDATED_CLIENT_ID)
            .stockCode(UPDATED_STOCK_CODE)
            .operationType(UPDATED_OPERATION_TYPE)
            .amount(UPDATED_AMOUNT)
            .mode(UPDATED_MODE)
            .state(UPDATED_STATE);

        restOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderHistory))
            )
            .andExpect(status().isOk());

        // Validate the OrderHistory in the database
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeUpdate);
        OrderHistory testOrderHistory = orderHistoryList.get(orderHistoryList.size() - 1);
        assertThat(testOrderHistory.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testOrderHistory.getStockCode()).isEqualTo(UPDATED_STOCK_CODE);
        assertThat(testOrderHistory.getOperationType()).isEqualTo(UPDATED_OPERATION_TYPE);
        assertThat(testOrderHistory.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testOrderHistory.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testOrderHistory.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testOrderHistory.getExecutionDate()).isEqualTo(DEFAULT_EXECUTION_DATE);
        assertThat(testOrderHistory.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testOrderHistory.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testOrderHistory.getInfo()).isEqualTo(DEFAULT_INFO);
        assertThat(testOrderHistory.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    void fullUpdateOrderHistoryWithPatch() throws Exception {
        // Initialize the database
        orderHistoryRepository.saveAndFlush(orderHistory);

        int databaseSizeBeforeUpdate = orderHistoryRepository.findAll().size();

        // Update the orderHistory using partial update
        OrderHistory partialUpdatedOrderHistory = new OrderHistory();
        partialUpdatedOrderHistory.setId(orderHistory.getId());

        partialUpdatedOrderHistory
            .clientId(UPDATED_CLIENT_ID)
            .stockCode(UPDATED_STOCK_CODE)
            .operationType(UPDATED_OPERATION_TYPE)
            .price(UPDATED_PRICE)
            .amount(UPDATED_AMOUNT)
            .creationDate(UPDATED_CREATION_DATE)
            .executionDate(UPDATED_EXECUTION_DATE)
            .mode(UPDATED_MODE)
            .state(UPDATED_STATE)
            .info(UPDATED_INFO)
            .language(UPDATED_LANGUAGE);

        restOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderHistory))
            )
            .andExpect(status().isOk());

        // Validate the OrderHistory in the database
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeUpdate);
        OrderHistory testOrderHistory = orderHistoryList.get(orderHistoryList.size() - 1);
        assertThat(testOrderHistory.getClientId()).isEqualTo(UPDATED_CLIENT_ID);
        assertThat(testOrderHistory.getStockCode()).isEqualTo(UPDATED_STOCK_CODE);
        assertThat(testOrderHistory.getOperationType()).isEqualTo(UPDATED_OPERATION_TYPE);
        assertThat(testOrderHistory.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testOrderHistory.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testOrderHistory.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testOrderHistory.getExecutionDate()).isEqualTo(UPDATED_EXECUTION_DATE);
        assertThat(testOrderHistory.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testOrderHistory.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testOrderHistory.getInfo()).isEqualTo(UPDATED_INFO);
        assertThat(testOrderHistory.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void patchNonExistingOrderHistory() throws Exception {
        int databaseSizeBeforeUpdate = orderHistoryRepository.findAll().size();
        orderHistory.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderHistory in the database
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderHistory() throws Exception {
        int databaseSizeBeforeUpdate = orderHistoryRepository.findAll().size();
        orderHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderHistory in the database
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderHistory() throws Exception {
        int databaseSizeBeforeUpdate = orderHistoryRepository.findAll().size();
        orderHistory.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orderHistory))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderHistory in the database
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderHistory() throws Exception {
        // Initialize the database
        orderHistoryRepository.saveAndFlush(orderHistory);

        int databaseSizeBeforeDelete = orderHistoryRepository.findAll().size();

        // Delete the orderHistory
        restOrderHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
