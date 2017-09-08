package com.vbk.orderbook.web.rest;

import com.vbk.orderbook.OrderbookApp;

import com.vbk.orderbook.domain.Position;
import com.vbk.orderbook.repository.PositionRepository;
import com.vbk.orderbook.service.PositionService;
import com.vbk.orderbook.service.dto.PositionDTO;
import com.vbk.orderbook.service.mapper.PositionMapper;
import com.vbk.orderbook.web.rest.errors.ExceptionTranslator;
import com.vbk.orderbook.service.dto.PositionCriteria;
import com.vbk.orderbook.service.PositionQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.vbk.orderbook.domain.enumeration.OperationType;
/**
 * Test class for the PositionResource REST controller.
 *
 * @see PositionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderbookApp.class)
public class PositionResourceIntTest {

    private static final String DEFAULT_ASSET = "AAAAAAAAAA";
    private static final String UPDATED_ASSET = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BUY_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BUY_AT = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_SELL_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SELL_AT = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_ENTRY_VALUE = 1D;
    private static final Double UPDATED_ENTRY_VALUE = 2D;

    private static final Double DEFAULT_EXIT_VALUE = 1D;
    private static final Double UPDATED_EXIT_VALUE = 2D;

    private static final OperationType DEFAULT_OPERATION_TYPE = OperationType.SHORT;
    private static final OperationType UPDATED_OPERATION_TYPE = OperationType.LONG;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private PositionService positionService;

    @Autowired
    private PositionQueryService positionQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPositionMockMvc;

    private Position position;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PositionResource positionResource = new PositionResource(positionService, positionQueryService);
        this.restPositionMockMvc = MockMvcBuilders.standaloneSetup(positionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Position createEntity(EntityManager em) {
        Position position = new Position()
            .asset(DEFAULT_ASSET)
            .buyAt(DEFAULT_BUY_AT)
            .sellAt(DEFAULT_SELL_AT)
            .entryValue(DEFAULT_ENTRY_VALUE)
            .exitValue(DEFAULT_EXIT_VALUE)
            .operationType(DEFAULT_OPERATION_TYPE);
        return position;
    }

    @Before
    public void initTest() {
        position = createEntity(em);
    }

    @Test
    @Transactional
    public void createPosition() throws Exception {
        int databaseSizeBeforeCreate = positionRepository.findAll().size();

        // Create the Position
        PositionDTO positionDTO = positionMapper.toDto(position);
        restPositionMockMvc.perform(post("/api/positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isCreated());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeCreate + 1);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getAsset()).isEqualTo(DEFAULT_ASSET);
        assertThat(testPosition.getBuyAt()).isEqualTo(DEFAULT_BUY_AT);
        assertThat(testPosition.getSellAt()).isEqualTo(DEFAULT_SELL_AT);
        assertThat(testPosition.getEntryValue()).isEqualTo(DEFAULT_ENTRY_VALUE);
        assertThat(testPosition.getExitValue()).isEqualTo(DEFAULT_EXIT_VALUE);
        assertThat(testPosition.getOperationType()).isEqualTo(DEFAULT_OPERATION_TYPE);
    }

    @Test
    @Transactional
    public void createPositionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = positionRepository.findAll().size();

        // Create the Position with an existing ID
        position.setId(1L);
        PositionDTO positionDTO = positionMapper.toDto(position);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPositionMockMvc.perform(post("/api/positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkAssetIsRequired() throws Exception {
        int databaseSizeBeforeTest = positionRepository.findAll().size();
        // set the field null
        position.setAsset(null);

        // Create the Position, which fails.
        PositionDTO positionDTO = positionMapper.toDto(position);

        restPositionMockMvc.perform(post("/api/positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isBadRequest());

        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOperationTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = positionRepository.findAll().size();
        // set the field null
        position.setOperationType(null);

        // Create the Position, which fails.
        PositionDTO positionDTO = positionMapper.toDto(position);

        restPositionMockMvc.perform(post("/api/positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isBadRequest());

        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPositions() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList
        restPositionMockMvc.perform(get("/api/positions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(position.getId().intValue())))
            .andExpect(jsonPath("$.[*].asset").value(hasItem(DEFAULT_ASSET.toString())))
            .andExpect(jsonPath("$.[*].buyAt").value(hasItem(DEFAULT_BUY_AT.toString())))
            .andExpect(jsonPath("$.[*].sellAt").value(hasItem(DEFAULT_SELL_AT.toString())))
            .andExpect(jsonPath("$.[*].entryValue").value(hasItem(DEFAULT_ENTRY_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].exitValue").value(hasItem(DEFAULT_EXIT_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].operationType").value(hasItem(DEFAULT_OPERATION_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getPosition() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get the position
        restPositionMockMvc.perform(get("/api/positions/{id}", position.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(position.getId().intValue()))
            .andExpect(jsonPath("$.asset").value(DEFAULT_ASSET.toString()))
            .andExpect(jsonPath("$.buyAt").value(DEFAULT_BUY_AT.toString()))
            .andExpect(jsonPath("$.sellAt").value(DEFAULT_SELL_AT.toString()))
            .andExpect(jsonPath("$.entryValue").value(DEFAULT_ENTRY_VALUE.doubleValue()))
            .andExpect(jsonPath("$.exitValue").value(DEFAULT_EXIT_VALUE.doubleValue()))
            .andExpect(jsonPath("$.operationType").value(DEFAULT_OPERATION_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getAllPositionsByAssetIsEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where asset equals to DEFAULT_ASSET
        defaultPositionShouldBeFound("asset.equals=" + DEFAULT_ASSET);

        // Get all the positionList where asset equals to UPDATED_ASSET
        defaultPositionShouldNotBeFound("asset.equals=" + UPDATED_ASSET);
    }

    @Test
    @Transactional
    public void getAllPositionsByAssetIsInShouldWork() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where asset in DEFAULT_ASSET or UPDATED_ASSET
        defaultPositionShouldBeFound("asset.in=" + DEFAULT_ASSET + "," + UPDATED_ASSET);

        // Get all the positionList where asset equals to UPDATED_ASSET
        defaultPositionShouldNotBeFound("asset.in=" + UPDATED_ASSET);
    }

    @Test
    @Transactional
    public void getAllPositionsByAssetIsNullOrNotNull() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where asset is not null
        defaultPositionShouldBeFound("asset.specified=true");

        // Get all the positionList where asset is null
        defaultPositionShouldNotBeFound("asset.specified=false");
    }

    @Test
    @Transactional
    public void getAllPositionsByBuyAtIsEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where buyAt equals to DEFAULT_BUY_AT
        defaultPositionShouldBeFound("buyAt.equals=" + DEFAULT_BUY_AT);

        // Get all the positionList where buyAt equals to UPDATED_BUY_AT
        defaultPositionShouldNotBeFound("buyAt.equals=" + UPDATED_BUY_AT);
    }

    @Test
    @Transactional
    public void getAllPositionsByBuyAtIsInShouldWork() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where buyAt in DEFAULT_BUY_AT or UPDATED_BUY_AT
        defaultPositionShouldBeFound("buyAt.in=" + DEFAULT_BUY_AT + "," + UPDATED_BUY_AT);

        // Get all the positionList where buyAt equals to UPDATED_BUY_AT
        defaultPositionShouldNotBeFound("buyAt.in=" + UPDATED_BUY_AT);
    }

    @Test
    @Transactional
    public void getAllPositionsByBuyAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where buyAt is not null
        defaultPositionShouldBeFound("buyAt.specified=true");

        // Get all the positionList where buyAt is null
        defaultPositionShouldNotBeFound("buyAt.specified=false");
    }

    @Test
    @Transactional
    public void getAllPositionsByBuyAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where buyAt greater than or equals to DEFAULT_BUY_AT
        defaultPositionShouldBeFound("buyAt.greaterOrEqualThan=" + DEFAULT_BUY_AT);

        // Get all the positionList where buyAt greater than or equals to UPDATED_BUY_AT
        defaultPositionShouldNotBeFound("buyAt.greaterOrEqualThan=" + UPDATED_BUY_AT);
    }

    @Test
    @Transactional
    public void getAllPositionsByBuyAtIsLessThanSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where buyAt less than or equals to DEFAULT_BUY_AT
        defaultPositionShouldNotBeFound("buyAt.lessThan=" + DEFAULT_BUY_AT);

        // Get all the positionList where buyAt less than or equals to UPDATED_BUY_AT
        defaultPositionShouldBeFound("buyAt.lessThan=" + UPDATED_BUY_AT);
    }


    @Test
    @Transactional
    public void getAllPositionsBySellAtIsEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where sellAt equals to DEFAULT_SELL_AT
        defaultPositionShouldBeFound("sellAt.equals=" + DEFAULT_SELL_AT);

        // Get all the positionList where sellAt equals to UPDATED_SELL_AT
        defaultPositionShouldNotBeFound("sellAt.equals=" + UPDATED_SELL_AT);
    }

    @Test
    @Transactional
    public void getAllPositionsBySellAtIsInShouldWork() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where sellAt in DEFAULT_SELL_AT or UPDATED_SELL_AT
        defaultPositionShouldBeFound("sellAt.in=" + DEFAULT_SELL_AT + "," + UPDATED_SELL_AT);

        // Get all the positionList where sellAt equals to UPDATED_SELL_AT
        defaultPositionShouldNotBeFound("sellAt.in=" + UPDATED_SELL_AT);
    }

    @Test
    @Transactional
    public void getAllPositionsBySellAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where sellAt is not null
        defaultPositionShouldBeFound("sellAt.specified=true");

        // Get all the positionList where sellAt is null
        defaultPositionShouldNotBeFound("sellAt.specified=false");
    }

    @Test
    @Transactional
    public void getAllPositionsBySellAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where sellAt greater than or equals to DEFAULT_SELL_AT
        defaultPositionShouldBeFound("sellAt.greaterOrEqualThan=" + DEFAULT_SELL_AT);

        // Get all the positionList where sellAt greater than or equals to UPDATED_SELL_AT
        defaultPositionShouldNotBeFound("sellAt.greaterOrEqualThan=" + UPDATED_SELL_AT);
    }

    @Test
    @Transactional
    public void getAllPositionsBySellAtIsLessThanSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where sellAt less than or equals to DEFAULT_SELL_AT
        defaultPositionShouldNotBeFound("sellAt.lessThan=" + DEFAULT_SELL_AT);

        // Get all the positionList where sellAt less than or equals to UPDATED_SELL_AT
        defaultPositionShouldBeFound("sellAt.lessThan=" + UPDATED_SELL_AT);
    }


    @Test
    @Transactional
    public void getAllPositionsByEntryValueIsEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where entryValue equals to DEFAULT_ENTRY_VALUE
        defaultPositionShouldBeFound("entryValue.equals=" + DEFAULT_ENTRY_VALUE);

        // Get all the positionList where entryValue equals to UPDATED_ENTRY_VALUE
        defaultPositionShouldNotBeFound("entryValue.equals=" + UPDATED_ENTRY_VALUE);
    }

    @Test
    @Transactional
    public void getAllPositionsByEntryValueIsInShouldWork() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where entryValue in DEFAULT_ENTRY_VALUE or UPDATED_ENTRY_VALUE
        defaultPositionShouldBeFound("entryValue.in=" + DEFAULT_ENTRY_VALUE + "," + UPDATED_ENTRY_VALUE);

        // Get all the positionList where entryValue equals to UPDATED_ENTRY_VALUE
        defaultPositionShouldNotBeFound("entryValue.in=" + UPDATED_ENTRY_VALUE);
    }

    @Test
    @Transactional
    public void getAllPositionsByEntryValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where entryValue is not null
        defaultPositionShouldBeFound("entryValue.specified=true");

        // Get all the positionList where entryValue is null
        defaultPositionShouldNotBeFound("entryValue.specified=false");
    }

    @Test
    @Transactional
    public void getAllPositionsByExitValueIsEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where exitValue equals to DEFAULT_EXIT_VALUE
        defaultPositionShouldBeFound("exitValue.equals=" + DEFAULT_EXIT_VALUE);

        // Get all the positionList where exitValue equals to UPDATED_EXIT_VALUE
        defaultPositionShouldNotBeFound("exitValue.equals=" + UPDATED_EXIT_VALUE);
    }

    @Test
    @Transactional
    public void getAllPositionsByExitValueIsInShouldWork() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where exitValue in DEFAULT_EXIT_VALUE or UPDATED_EXIT_VALUE
        defaultPositionShouldBeFound("exitValue.in=" + DEFAULT_EXIT_VALUE + "," + UPDATED_EXIT_VALUE);

        // Get all the positionList where exitValue equals to UPDATED_EXIT_VALUE
        defaultPositionShouldNotBeFound("exitValue.in=" + UPDATED_EXIT_VALUE);
    }

    @Test
    @Transactional
    public void getAllPositionsByExitValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where exitValue is not null
        defaultPositionShouldBeFound("exitValue.specified=true");

        // Get all the positionList where exitValue is null
        defaultPositionShouldNotBeFound("exitValue.specified=false");
    }

    @Test
    @Transactional
    public void getAllPositionsByOperationTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where operationType equals to DEFAULT_OPERATION_TYPE
        defaultPositionShouldBeFound("operationType.equals=" + DEFAULT_OPERATION_TYPE);

        // Get all the positionList where operationType equals to UPDATED_OPERATION_TYPE
        defaultPositionShouldNotBeFound("operationType.equals=" + UPDATED_OPERATION_TYPE);
    }

    @Test
    @Transactional
    public void getAllPositionsByOperationTypeIsInShouldWork() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where operationType in DEFAULT_OPERATION_TYPE or UPDATED_OPERATION_TYPE
        defaultPositionShouldBeFound("operationType.in=" + DEFAULT_OPERATION_TYPE + "," + UPDATED_OPERATION_TYPE);

        // Get all the positionList where operationType equals to UPDATED_OPERATION_TYPE
        defaultPositionShouldNotBeFound("operationType.in=" + UPDATED_OPERATION_TYPE);
    }

    @Test
    @Transactional
    public void getAllPositionsByOperationTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);

        // Get all the positionList where operationType is not null
        defaultPositionShouldBeFound("operationType.specified=true");

        // Get all the positionList where operationType is null
        defaultPositionShouldNotBeFound("operationType.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPositionShouldBeFound(String filter) throws Exception {
        restPositionMockMvc.perform(get("/api/positions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(position.getId().intValue())))
            .andExpect(jsonPath("$.[*].asset").value(hasItem(DEFAULT_ASSET.toString())))
            .andExpect(jsonPath("$.[*].buyAt").value(hasItem(DEFAULT_BUY_AT.toString())))
            .andExpect(jsonPath("$.[*].sellAt").value(hasItem(DEFAULT_SELL_AT.toString())))
            .andExpect(jsonPath("$.[*].entryValue").value(hasItem(DEFAULT_ENTRY_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].exitValue").value(hasItem(DEFAULT_EXIT_VALUE.doubleValue())))
            .andExpect(jsonPath("$.[*].operationType").value(hasItem(DEFAULT_OPERATION_TYPE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPositionShouldNotBeFound(String filter) throws Exception {
        restPositionMockMvc.perform(get("/api/positions?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingPosition() throws Exception {
        // Get the position
        restPositionMockMvc.perform(get("/api/positions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePosition() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();

        // Update the position
        Position updatedPosition = positionRepository.findOne(position.getId());
        updatedPosition
            .asset(UPDATED_ASSET)
            .buyAt(UPDATED_BUY_AT)
            .sellAt(UPDATED_SELL_AT)
            .entryValue(UPDATED_ENTRY_VALUE)
            .exitValue(UPDATED_EXIT_VALUE)
            .operationType(UPDATED_OPERATION_TYPE);
        PositionDTO positionDTO = positionMapper.toDto(updatedPosition);

        restPositionMockMvc.perform(put("/api/positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isOk());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate);
        Position testPosition = positionList.get(positionList.size() - 1);
        assertThat(testPosition.getAsset()).isEqualTo(UPDATED_ASSET);
        assertThat(testPosition.getBuyAt()).isEqualTo(UPDATED_BUY_AT);
        assertThat(testPosition.getSellAt()).isEqualTo(UPDATED_SELL_AT);
        assertThat(testPosition.getEntryValue()).isEqualTo(UPDATED_ENTRY_VALUE);
        assertThat(testPosition.getExitValue()).isEqualTo(UPDATED_EXIT_VALUE);
        assertThat(testPosition.getOperationType()).isEqualTo(UPDATED_OPERATION_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingPosition() throws Exception {
        int databaseSizeBeforeUpdate = positionRepository.findAll().size();

        // Create the Position
        PositionDTO positionDTO = positionMapper.toDto(position);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPositionMockMvc.perform(put("/api/positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(positionDTO)))
            .andExpect(status().isCreated());

        // Validate the Position in the database
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePosition() throws Exception {
        // Initialize the database
        positionRepository.saveAndFlush(position);
        int databaseSizeBeforeDelete = positionRepository.findAll().size();

        // Get the position
        restPositionMockMvc.perform(delete("/api/positions/{id}", position.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Position> positionList = positionRepository.findAll();
        assertThat(positionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Position.class);
        Position position1 = new Position();
        position1.setId(1L);
        Position position2 = new Position();
        position2.setId(position1.getId());
        assertThat(position1).isEqualTo(position2);
        position2.setId(2L);
        assertThat(position1).isNotEqualTo(position2);
        position1.setId(null);
        assertThat(position1).isNotEqualTo(position2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PositionDTO.class);
        PositionDTO positionDTO1 = new PositionDTO();
        positionDTO1.setId(1L);
        PositionDTO positionDTO2 = new PositionDTO();
        assertThat(positionDTO1).isNotEqualTo(positionDTO2);
        positionDTO2.setId(positionDTO1.getId());
        assertThat(positionDTO1).isEqualTo(positionDTO2);
        positionDTO2.setId(2L);
        assertThat(positionDTO1).isNotEqualTo(positionDTO2);
        positionDTO1.setId(null);
        assertThat(positionDTO1).isNotEqualTo(positionDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(positionMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(positionMapper.fromId(null)).isNull();
    }
}
