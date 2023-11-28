package com.prog2final.procesador.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.prog2final.procesador.IntegrationTest;
import com.prog2final.procesador.domain.OrderHistory;
import com.prog2final.procesador.domain.enumeration.Estado;
import com.prog2final.procesador.domain.enumeration.Modo;
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

    private static final Long DEFAULT_CLIENTE = 1L;
    private static final Long UPDATED_CLIENTE = 2L;

    private static final Long DEFAULT_ACCION_ID = 1L;
    private static final Long UPDATED_ACCION_ID = 2L;

    private static final String DEFAULT_ACCION = "AAAAAAAAAA";
    private static final String UPDATED_ACCION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_OPERACION = false;
    private static final Boolean UPDATED_OPERACION = true;

    private static final Double DEFAULT_CANTIDAD = 1D;
    private static final Double UPDATED_CANTIDAD = 2D;

    private static final Double DEFAULT_PRECIO = 1D;
    private static final Double UPDATED_PRECIO = 2D;

    private static final Instant DEFAULT_FECHA_OPERACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_OPERACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Modo DEFAULT_MODO = Modo.PRINCIPIODIA;
    private static final Modo UPDATED_MODO = Modo.AHORA;

    private static final Estado DEFAULT_ESTADO = Estado.PENDIENTE;
    private static final Estado UPDATED_ESTADO = Estado.EXITOSA;

    private static final String DEFAULT_OPERACION_OBSERVACIONES = "AAAAAAAAAA";
    private static final String UPDATED_OPERACION_OBSERVACIONES = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_EJECUCION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_EJECUCION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

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
            .cliente(DEFAULT_CLIENTE)
            .accionId(DEFAULT_ACCION_ID)
            .accion(DEFAULT_ACCION)
            .operacion(DEFAULT_OPERACION)
            .cantidad(DEFAULT_CANTIDAD)
            .precio(DEFAULT_PRECIO)
            .fechaOperacion(DEFAULT_FECHA_OPERACION)
            .modo(DEFAULT_MODO)
            .estado(DEFAULT_ESTADO)
            .operacionObservaciones(DEFAULT_OPERACION_OBSERVACIONES)
            .fechaEjecucion(DEFAULT_FECHA_EJECUCION);
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
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .cantidad(UPDATED_CANTIDAD)
            .precio(UPDATED_PRECIO)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO)
            .estado(UPDATED_ESTADO)
            .operacionObservaciones(UPDATED_OPERACION_OBSERVACIONES)
            .fechaEjecucion(UPDATED_FECHA_EJECUCION);
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
        assertThat(testOrderHistory.getCliente()).isEqualTo(DEFAULT_CLIENTE);
        assertThat(testOrderHistory.getAccionId()).isEqualTo(DEFAULT_ACCION_ID);
        assertThat(testOrderHistory.getAccion()).isEqualTo(DEFAULT_ACCION);
        assertThat(testOrderHistory.getOperacion()).isEqualTo(DEFAULT_OPERACION);
        assertThat(testOrderHistory.getCantidad()).isEqualTo(DEFAULT_CANTIDAD);
        assertThat(testOrderHistory.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testOrderHistory.getFechaOperacion()).isEqualTo(DEFAULT_FECHA_OPERACION);
        assertThat(testOrderHistory.getModo()).isEqualTo(DEFAULT_MODO);
        assertThat(testOrderHistory.getEstado()).isEqualTo(DEFAULT_ESTADO);
        assertThat(testOrderHistory.getOperacionObservaciones()).isEqualTo(DEFAULT_OPERACION_OBSERVACIONES);
        assertThat(testOrderHistory.getFechaEjecucion()).isEqualTo(DEFAULT_FECHA_EJECUCION);
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
    void checkClienteIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderHistoryRepository.findAll().size();
        // set the field null
        orderHistory.setCliente(null);

        // Create the OrderHistory, which fails.

        restOrderHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderHistory)))
            .andExpect(status().isBadRequest());

        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAccionIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderHistoryRepository.findAll().size();
        // set the field null
        orderHistory.setAccionId(null);

        // Create the OrderHistory, which fails.

        restOrderHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderHistory)))
            .andExpect(status().isBadRequest());

        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAccionIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderHistoryRepository.findAll().size();
        // set the field null
        orderHistory.setAccion(null);

        // Create the OrderHistory, which fails.

        restOrderHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderHistory)))
            .andExpect(status().isBadRequest());

        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOperacionIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderHistoryRepository.findAll().size();
        // set the field null
        orderHistory.setOperacion(null);

        // Create the OrderHistory, which fails.

        restOrderHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderHistory)))
            .andExpect(status().isBadRequest());

        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModoIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderHistoryRepository.findAll().size();
        // set the field null
        orderHistory.setModo(null);

        // Create the OrderHistory, which fails.

        restOrderHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderHistory)))
            .andExpect(status().isBadRequest());

        List<OrderHistory> orderHistoryList = orderHistoryRepository.findAll();
        assertThat(orderHistoryList).hasSize(databaseSizeBeforeTest);
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
            .andExpect(jsonPath("$.[*].cliente").value(hasItem(DEFAULT_CLIENTE.intValue())))
            .andExpect(jsonPath("$.[*].accionId").value(hasItem(DEFAULT_ACCION_ID.intValue())))
            .andExpect(jsonPath("$.[*].accion").value(hasItem(DEFAULT_ACCION)))
            .andExpect(jsonPath("$.[*].operacion").value(hasItem(DEFAULT_OPERACION.booleanValue())))
            .andExpect(jsonPath("$.[*].cantidad").value(hasItem(DEFAULT_CANTIDAD.doubleValue())))
            .andExpect(jsonPath("$.[*].precio").value(hasItem(DEFAULT_PRECIO.doubleValue())))
            .andExpect(jsonPath("$.[*].fechaOperacion").value(hasItem(DEFAULT_FECHA_OPERACION.toString())))
            .andExpect(jsonPath("$.[*].modo").value(hasItem(DEFAULT_MODO.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].operacionObservaciones").value(hasItem(DEFAULT_OPERACION_OBSERVACIONES)))
            .andExpect(jsonPath("$.[*].fechaEjecucion").value(hasItem(DEFAULT_FECHA_EJECUCION.toString())));
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
            .andExpect(jsonPath("$.cliente").value(DEFAULT_CLIENTE.intValue()))
            .andExpect(jsonPath("$.accionId").value(DEFAULT_ACCION_ID.intValue()))
            .andExpect(jsonPath("$.accion").value(DEFAULT_ACCION))
            .andExpect(jsonPath("$.operacion").value(DEFAULT_OPERACION.booleanValue()))
            .andExpect(jsonPath("$.cantidad").value(DEFAULT_CANTIDAD.doubleValue()))
            .andExpect(jsonPath("$.precio").value(DEFAULT_PRECIO.doubleValue()))
            .andExpect(jsonPath("$.fechaOperacion").value(DEFAULT_FECHA_OPERACION.toString()))
            .andExpect(jsonPath("$.modo").value(DEFAULT_MODO.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.operacionObservaciones").value(DEFAULT_OPERACION_OBSERVACIONES))
            .andExpect(jsonPath("$.fechaEjecucion").value(DEFAULT_FECHA_EJECUCION.toString()));
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
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .cantidad(UPDATED_CANTIDAD)
            .precio(UPDATED_PRECIO)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO)
            .estado(UPDATED_ESTADO)
            .operacionObservaciones(UPDATED_OPERACION_OBSERVACIONES)
            .fechaEjecucion(UPDATED_FECHA_EJECUCION);

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
        assertThat(testOrderHistory.getCliente()).isEqualTo(UPDATED_CLIENTE);
        assertThat(testOrderHistory.getAccionId()).isEqualTo(UPDATED_ACCION_ID);
        assertThat(testOrderHistory.getAccion()).isEqualTo(UPDATED_ACCION);
        assertThat(testOrderHistory.getOperacion()).isEqualTo(UPDATED_OPERACION);
        assertThat(testOrderHistory.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testOrderHistory.getPrecio()).isEqualTo(UPDATED_PRECIO);
        assertThat(testOrderHistory.getFechaOperacion()).isEqualTo(UPDATED_FECHA_OPERACION);
        assertThat(testOrderHistory.getModo()).isEqualTo(UPDATED_MODO);
        assertThat(testOrderHistory.getEstado()).isEqualTo(UPDATED_ESTADO);
        assertThat(testOrderHistory.getOperacionObservaciones()).isEqualTo(UPDATED_OPERACION_OBSERVACIONES);
        assertThat(testOrderHistory.getFechaEjecucion()).isEqualTo(UPDATED_FECHA_EJECUCION);
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
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .cantidad(UPDATED_CANTIDAD)
            .modo(UPDATED_MODO)
            .estado(UPDATED_ESTADO);

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
        assertThat(testOrderHistory.getCliente()).isEqualTo(UPDATED_CLIENTE);
        assertThat(testOrderHistory.getAccionId()).isEqualTo(UPDATED_ACCION_ID);
        assertThat(testOrderHistory.getAccion()).isEqualTo(UPDATED_ACCION);
        assertThat(testOrderHistory.getOperacion()).isEqualTo(DEFAULT_OPERACION);
        assertThat(testOrderHistory.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testOrderHistory.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testOrderHistory.getFechaOperacion()).isEqualTo(DEFAULT_FECHA_OPERACION);
        assertThat(testOrderHistory.getModo()).isEqualTo(UPDATED_MODO);
        assertThat(testOrderHistory.getEstado()).isEqualTo(UPDATED_ESTADO);
        assertThat(testOrderHistory.getOperacionObservaciones()).isEqualTo(DEFAULT_OPERACION_OBSERVACIONES);
        assertThat(testOrderHistory.getFechaEjecucion()).isEqualTo(DEFAULT_FECHA_EJECUCION);
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
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .cantidad(UPDATED_CANTIDAD)
            .precio(UPDATED_PRECIO)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO)
            .estado(UPDATED_ESTADO)
            .operacionObservaciones(UPDATED_OPERACION_OBSERVACIONES)
            .fechaEjecucion(UPDATED_FECHA_EJECUCION);

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
        assertThat(testOrderHistory.getCliente()).isEqualTo(UPDATED_CLIENTE);
        assertThat(testOrderHistory.getAccionId()).isEqualTo(UPDATED_ACCION_ID);
        assertThat(testOrderHistory.getAccion()).isEqualTo(UPDATED_ACCION);
        assertThat(testOrderHistory.getOperacion()).isEqualTo(UPDATED_OPERACION);
        assertThat(testOrderHistory.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testOrderHistory.getPrecio()).isEqualTo(UPDATED_PRECIO);
        assertThat(testOrderHistory.getFechaOperacion()).isEqualTo(UPDATED_FECHA_OPERACION);
        assertThat(testOrderHistory.getModo()).isEqualTo(UPDATED_MODO);
        assertThat(testOrderHistory.getEstado()).isEqualTo(UPDATED_ESTADO);
        assertThat(testOrderHistory.getOperacionObservaciones()).isEqualTo(UPDATED_OPERACION_OBSERVACIONES);
        assertThat(testOrderHistory.getFechaEjecucion()).isEqualTo(UPDATED_FECHA_EJECUCION);
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
