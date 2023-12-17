package com.progii.finalcom.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.progii.finalcom.IntegrationTest;
import com.progii.finalcom.domain.SuccessfulOrders;
import com.progii.finalcom.domain.enumeration.Modo;
import com.progii.finalcom.domain.enumeration.Operacion;
import com.progii.finalcom.repository.SuccessfulOrdersRepository;
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
 * Integration tests for the {@link SuccessfulOrdersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SuccessfulOrdersResourceIT {

    private static final Integer DEFAULT_CLIENTE = 1;
    private static final Integer UPDATED_CLIENTE = 2;

    private static final Integer DEFAULT_ACCION_ID = 1;
    private static final Integer UPDATED_ACCION_ID = 2;

    private static final String DEFAULT_ACCION = "AAAAAAAAAA";
    private static final String UPDATED_ACCION = "BBBBBBBBBB";

    private static final Operacion DEFAULT_OPERACION = Operacion.COMPRA;
    private static final Operacion UPDATED_OPERACION = Operacion.VENTA;

    private static final Float DEFAULT_PRECIO = 1F;
    private static final Float UPDATED_PRECIO = 2F;

    private static final Integer DEFAULT_CANTIDAD = 1;
    private static final Integer UPDATED_CANTIDAD = 2;

    private static final Instant DEFAULT_FECHA_OPERACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_OPERACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Modo DEFAULT_MODO = Modo.INICIODIA;
    private static final Modo UPDATED_MODO = Modo.AHORA;

    private static final Boolean DEFAULT_OPERACION_EXITOSA = false;
    private static final Boolean UPDATED_OPERACION_EXITOSA = true;

    private static final String DEFAULT_OPERACION_OBSERVACIONES = "AAAAAAAAAA";
    private static final String UPDATED_OPERACION_OBSERVACIONES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ESTADO = false;
    private static final Boolean UPDATED_ESTADO = true;

    private static final String ENTITY_API_URL = "/api/successful-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SuccessfulOrdersRepository successfulOrdersRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSuccessfulOrdersMockMvc;

    private SuccessfulOrders successfulOrders;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SuccessfulOrders createEntity(EntityManager em) {
        SuccessfulOrders successfulOrders = new SuccessfulOrders()
            .cliente(DEFAULT_CLIENTE)
            .accionId(DEFAULT_ACCION_ID)
            .accion(DEFAULT_ACCION)
            .operacion(DEFAULT_OPERACION)
            .precio(DEFAULT_PRECIO)
            .cantidad(DEFAULT_CANTIDAD)
            .fechaOperacion(DEFAULT_FECHA_OPERACION)
            .modo(DEFAULT_MODO)
            .operacionExitosa(DEFAULT_OPERACION_EXITOSA)
            .operacionObservaciones(DEFAULT_OPERACION_OBSERVACIONES)
            .estado(DEFAULT_ESTADO);
        return successfulOrders;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SuccessfulOrders createUpdatedEntity(EntityManager em) {
        SuccessfulOrders successfulOrders = new SuccessfulOrders()
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .precio(UPDATED_PRECIO)
            .cantidad(UPDATED_CANTIDAD)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO)
            .operacionExitosa(UPDATED_OPERACION_EXITOSA)
            .operacionObservaciones(UPDATED_OPERACION_OBSERVACIONES)
            .estado(UPDATED_ESTADO);
        return successfulOrders;
    }

    @BeforeEach
    public void initTest() {
        successfulOrders = createEntity(em);
    }

    @Test
    @Transactional
    void createSuccessfulOrders() throws Exception {
        int databaseSizeBeforeCreate = successfulOrdersRepository.findAll().size();
        // Create the SuccessfulOrders
        restSuccessfulOrdersMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(successfulOrders))
            )
            .andExpect(status().isCreated());

        // Validate the SuccessfulOrders in the database
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeCreate + 1);
        SuccessfulOrders testSuccessfulOrders = successfulOrdersList.get(successfulOrdersList.size() - 1);
        assertThat(testSuccessfulOrders.getCliente()).isEqualTo(DEFAULT_CLIENTE);
        assertThat(testSuccessfulOrders.getAccionId()).isEqualTo(DEFAULT_ACCION_ID);
        assertThat(testSuccessfulOrders.getAccion()).isEqualTo(DEFAULT_ACCION);
        assertThat(testSuccessfulOrders.getOperacion()).isEqualTo(DEFAULT_OPERACION);
        assertThat(testSuccessfulOrders.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testSuccessfulOrders.getCantidad()).isEqualTo(DEFAULT_CANTIDAD);
        assertThat(testSuccessfulOrders.getFechaOperacion()).isEqualTo(DEFAULT_FECHA_OPERACION);
        assertThat(testSuccessfulOrders.getModo()).isEqualTo(DEFAULT_MODO);
        assertThat(testSuccessfulOrders.getOperacionExitosa()).isEqualTo(DEFAULT_OPERACION_EXITOSA);
        assertThat(testSuccessfulOrders.getOperacionObservaciones()).isEqualTo(DEFAULT_OPERACION_OBSERVACIONES);
        assertThat(testSuccessfulOrders.getEstado()).isEqualTo(DEFAULT_ESTADO);
    }

    @Test
    @Transactional
    void createSuccessfulOrdersWithExistingId() throws Exception {
        // Create the SuccessfulOrders with an existing ID
        successfulOrders.setId(1L);

        int databaseSizeBeforeCreate = successfulOrdersRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSuccessfulOrdersMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(successfulOrders))
            )
            .andExpect(status().isBadRequest());

        // Validate the SuccessfulOrders in the database
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSuccessfulOrders() throws Exception {
        // Initialize the database
        successfulOrdersRepository.saveAndFlush(successfulOrders);

        // Get all the successfulOrdersList
        restSuccessfulOrdersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(successfulOrders.getId().intValue())))
            .andExpect(jsonPath("$.[*].cliente").value(hasItem(DEFAULT_CLIENTE)))
            .andExpect(jsonPath("$.[*].accionId").value(hasItem(DEFAULT_ACCION_ID)))
            .andExpect(jsonPath("$.[*].accion").value(hasItem(DEFAULT_ACCION)))
            .andExpect(jsonPath("$.[*].operacion").value(hasItem(DEFAULT_OPERACION.toString())))
            .andExpect(jsonPath("$.[*].precio").value(hasItem(DEFAULT_PRECIO.doubleValue())))
            .andExpect(jsonPath("$.[*].cantidad").value(hasItem(DEFAULT_CANTIDAD)))
            .andExpect(jsonPath("$.[*].fechaOperacion").value(hasItem(DEFAULT_FECHA_OPERACION.toString())))
            .andExpect(jsonPath("$.[*].modo").value(hasItem(DEFAULT_MODO.toString())))
            .andExpect(jsonPath("$.[*].operacionExitosa").value(hasItem(DEFAULT_OPERACION_EXITOSA.booleanValue())))
            .andExpect(jsonPath("$.[*].operacionObservaciones").value(hasItem(DEFAULT_OPERACION_OBSERVACIONES)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.booleanValue())));
    }

    @Test
    @Transactional
    void getSuccessfulOrders() throws Exception {
        // Initialize the database
        successfulOrdersRepository.saveAndFlush(successfulOrders);

        // Get the successfulOrders
        restSuccessfulOrdersMockMvc
            .perform(get(ENTITY_API_URL_ID, successfulOrders.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(successfulOrders.getId().intValue()))
            .andExpect(jsonPath("$.cliente").value(DEFAULT_CLIENTE))
            .andExpect(jsonPath("$.accionId").value(DEFAULT_ACCION_ID))
            .andExpect(jsonPath("$.accion").value(DEFAULT_ACCION))
            .andExpect(jsonPath("$.operacion").value(DEFAULT_OPERACION.toString()))
            .andExpect(jsonPath("$.precio").value(DEFAULT_PRECIO.doubleValue()))
            .andExpect(jsonPath("$.cantidad").value(DEFAULT_CANTIDAD))
            .andExpect(jsonPath("$.fechaOperacion").value(DEFAULT_FECHA_OPERACION.toString()))
            .andExpect(jsonPath("$.modo").value(DEFAULT_MODO.toString()))
            .andExpect(jsonPath("$.operacionExitosa").value(DEFAULT_OPERACION_EXITOSA.booleanValue()))
            .andExpect(jsonPath("$.operacionObservaciones").value(DEFAULT_OPERACION_OBSERVACIONES))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingSuccessfulOrders() throws Exception {
        // Get the successfulOrders
        restSuccessfulOrdersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSuccessfulOrders() throws Exception {
        // Initialize the database
        successfulOrdersRepository.saveAndFlush(successfulOrders);

        int databaseSizeBeforeUpdate = successfulOrdersRepository.findAll().size();

        // Update the successfulOrders
        SuccessfulOrders updatedSuccessfulOrders = successfulOrdersRepository.findById(successfulOrders.getId()).get();
        // Disconnect from session so that the updates on updatedSuccessfulOrders are not directly saved in db
        em.detach(updatedSuccessfulOrders);
        updatedSuccessfulOrders
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .precio(UPDATED_PRECIO)
            .cantidad(UPDATED_CANTIDAD)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO)
            .operacionExitosa(UPDATED_OPERACION_EXITOSA)
            .operacionObservaciones(UPDATED_OPERACION_OBSERVACIONES)
            .estado(UPDATED_ESTADO);

        restSuccessfulOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSuccessfulOrders.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSuccessfulOrders))
            )
            .andExpect(status().isOk());

        // Validate the SuccessfulOrders in the database
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeUpdate);
        SuccessfulOrders testSuccessfulOrders = successfulOrdersList.get(successfulOrdersList.size() - 1);
        assertThat(testSuccessfulOrders.getCliente()).isEqualTo(UPDATED_CLIENTE);
        assertThat(testSuccessfulOrders.getAccionId()).isEqualTo(UPDATED_ACCION_ID);
        assertThat(testSuccessfulOrders.getAccion()).isEqualTo(UPDATED_ACCION);
        assertThat(testSuccessfulOrders.getOperacion()).isEqualTo(UPDATED_OPERACION);
        assertThat(testSuccessfulOrders.getPrecio()).isEqualTo(UPDATED_PRECIO);
        assertThat(testSuccessfulOrders.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testSuccessfulOrders.getFechaOperacion()).isEqualTo(UPDATED_FECHA_OPERACION);
        assertThat(testSuccessfulOrders.getModo()).isEqualTo(UPDATED_MODO);
        assertThat(testSuccessfulOrders.getOperacionExitosa()).isEqualTo(UPDATED_OPERACION_EXITOSA);
        assertThat(testSuccessfulOrders.getOperacionObservaciones()).isEqualTo(UPDATED_OPERACION_OBSERVACIONES);
        assertThat(testSuccessfulOrders.getEstado()).isEqualTo(UPDATED_ESTADO);
    }

    @Test
    @Transactional
    void putNonExistingSuccessfulOrders() throws Exception {
        int databaseSizeBeforeUpdate = successfulOrdersRepository.findAll().size();
        successfulOrders.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSuccessfulOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, successfulOrders.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(successfulOrders))
            )
            .andExpect(status().isBadRequest());

        // Validate the SuccessfulOrders in the database
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSuccessfulOrders() throws Exception {
        int databaseSizeBeforeUpdate = successfulOrdersRepository.findAll().size();
        successfulOrders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuccessfulOrdersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(successfulOrders))
            )
            .andExpect(status().isBadRequest());

        // Validate the SuccessfulOrders in the database
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSuccessfulOrders() throws Exception {
        int databaseSizeBeforeUpdate = successfulOrdersRepository.findAll().size();
        successfulOrders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuccessfulOrdersMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(successfulOrders))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SuccessfulOrders in the database
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSuccessfulOrdersWithPatch() throws Exception {
        // Initialize the database
        successfulOrdersRepository.saveAndFlush(successfulOrders);

        int databaseSizeBeforeUpdate = successfulOrdersRepository.findAll().size();

        // Update the successfulOrders using partial update
        SuccessfulOrders partialUpdatedSuccessfulOrders = new SuccessfulOrders();
        partialUpdatedSuccessfulOrders.setId(successfulOrders.getId());

        partialUpdatedSuccessfulOrders
            .cliente(UPDATED_CLIENTE)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO)
            .operacionExitosa(UPDATED_OPERACION_EXITOSA)
            .operacionObservaciones(UPDATED_OPERACION_OBSERVACIONES)
            .estado(UPDATED_ESTADO);

        restSuccessfulOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSuccessfulOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSuccessfulOrders))
            )
            .andExpect(status().isOk());

        // Validate the SuccessfulOrders in the database
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeUpdate);
        SuccessfulOrders testSuccessfulOrders = successfulOrdersList.get(successfulOrdersList.size() - 1);
        assertThat(testSuccessfulOrders.getCliente()).isEqualTo(UPDATED_CLIENTE);
        assertThat(testSuccessfulOrders.getAccionId()).isEqualTo(DEFAULT_ACCION_ID);
        assertThat(testSuccessfulOrders.getAccion()).isEqualTo(UPDATED_ACCION);
        assertThat(testSuccessfulOrders.getOperacion()).isEqualTo(UPDATED_OPERACION);
        assertThat(testSuccessfulOrders.getPrecio()).isEqualTo(DEFAULT_PRECIO);
        assertThat(testSuccessfulOrders.getCantidad()).isEqualTo(DEFAULT_CANTIDAD);
        assertThat(testSuccessfulOrders.getFechaOperacion()).isEqualTo(UPDATED_FECHA_OPERACION);
        assertThat(testSuccessfulOrders.getModo()).isEqualTo(UPDATED_MODO);
        assertThat(testSuccessfulOrders.getOperacionExitosa()).isEqualTo(UPDATED_OPERACION_EXITOSA);
        assertThat(testSuccessfulOrders.getOperacionObservaciones()).isEqualTo(UPDATED_OPERACION_OBSERVACIONES);
        assertThat(testSuccessfulOrders.getEstado()).isEqualTo(UPDATED_ESTADO);
    }

    @Test
    @Transactional
    void fullUpdateSuccessfulOrdersWithPatch() throws Exception {
        // Initialize the database
        successfulOrdersRepository.saveAndFlush(successfulOrders);

        int databaseSizeBeforeUpdate = successfulOrdersRepository.findAll().size();

        // Update the successfulOrders using partial update
        SuccessfulOrders partialUpdatedSuccessfulOrders = new SuccessfulOrders();
        partialUpdatedSuccessfulOrders.setId(successfulOrders.getId());

        partialUpdatedSuccessfulOrders
            .cliente(UPDATED_CLIENTE)
            .accionId(UPDATED_ACCION_ID)
            .accion(UPDATED_ACCION)
            .operacion(UPDATED_OPERACION)
            .precio(UPDATED_PRECIO)
            .cantidad(UPDATED_CANTIDAD)
            .fechaOperacion(UPDATED_FECHA_OPERACION)
            .modo(UPDATED_MODO)
            .operacionExitosa(UPDATED_OPERACION_EXITOSA)
            .operacionObservaciones(UPDATED_OPERACION_OBSERVACIONES)
            .estado(UPDATED_ESTADO);

        restSuccessfulOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSuccessfulOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSuccessfulOrders))
            )
            .andExpect(status().isOk());

        // Validate the SuccessfulOrders in the database
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeUpdate);
        SuccessfulOrders testSuccessfulOrders = successfulOrdersList.get(successfulOrdersList.size() - 1);
        assertThat(testSuccessfulOrders.getCliente()).isEqualTo(UPDATED_CLIENTE);
        assertThat(testSuccessfulOrders.getAccionId()).isEqualTo(UPDATED_ACCION_ID);
        assertThat(testSuccessfulOrders.getAccion()).isEqualTo(UPDATED_ACCION);
        assertThat(testSuccessfulOrders.getOperacion()).isEqualTo(UPDATED_OPERACION);
        assertThat(testSuccessfulOrders.getPrecio()).isEqualTo(UPDATED_PRECIO);
        assertThat(testSuccessfulOrders.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testSuccessfulOrders.getFechaOperacion()).isEqualTo(UPDATED_FECHA_OPERACION);
        assertThat(testSuccessfulOrders.getModo()).isEqualTo(UPDATED_MODO);
        assertThat(testSuccessfulOrders.getOperacionExitosa()).isEqualTo(UPDATED_OPERACION_EXITOSA);
        assertThat(testSuccessfulOrders.getOperacionObservaciones()).isEqualTo(UPDATED_OPERACION_OBSERVACIONES);
        assertThat(testSuccessfulOrders.getEstado()).isEqualTo(UPDATED_ESTADO);
    }

    @Test
    @Transactional
    void patchNonExistingSuccessfulOrders() throws Exception {
        int databaseSizeBeforeUpdate = successfulOrdersRepository.findAll().size();
        successfulOrders.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSuccessfulOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, successfulOrders.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(successfulOrders))
            )
            .andExpect(status().isBadRequest());

        // Validate the SuccessfulOrders in the database
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSuccessfulOrders() throws Exception {
        int databaseSizeBeforeUpdate = successfulOrdersRepository.findAll().size();
        successfulOrders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuccessfulOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(successfulOrders))
            )
            .andExpect(status().isBadRequest());

        // Validate the SuccessfulOrders in the database
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSuccessfulOrders() throws Exception {
        int databaseSizeBeforeUpdate = successfulOrdersRepository.findAll().size();
        successfulOrders.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSuccessfulOrdersMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(successfulOrders))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SuccessfulOrders in the database
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSuccessfulOrders() throws Exception {
        // Initialize the database
        successfulOrdersRepository.saveAndFlush(successfulOrders);

        int databaseSizeBeforeDelete = successfulOrdersRepository.findAll().size();

        // Delete the successfulOrders
        restSuccessfulOrdersMockMvc
            .perform(delete(ENTITY_API_URL_ID, successfulOrders.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SuccessfulOrders> successfulOrdersList = successfulOrdersRepository.findAll();
        assertThat(successfulOrdersList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
