package com.vbk.orderbook.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.vbk.orderbook.domain.Position;
import com.vbk.orderbook.domain.*; // for static metamodels
import com.vbk.orderbook.repository.PositionRepository;
import com.vbk.orderbook.service.dto.PositionCriteria;

import com.vbk.orderbook.service.dto.PositionDTO;
import com.vbk.orderbook.service.mapper.PositionMapper;
import com.vbk.orderbook.domain.enumeration.OperationType;

/**
 * Service for executing complex queries for Position entities in the database.
 * The main input is a {@link PositionCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {%link PositionDTO} or a {@link Page} of {%link PositionDTO} which fullfills the criterias
 */
@Service
@Transactional(readOnly = true)
public class PositionQueryService extends QueryService<Position> {

    private final Logger log = LoggerFactory.getLogger(PositionQueryService.class);


    private final PositionRepository positionRepository;

    private final PositionMapper positionMapper;
    public PositionQueryService(PositionRepository positionRepository, PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
    }

    /**
     * Return a {@link List} of {%link PositionDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PositionDTO> findByCriteria(PositionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Position> specification = createSpecification(criteria);
        return positionMapper.toDto(positionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {%link PositionDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PositionDTO> findByCriteria(PositionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Position> specification = createSpecification(criteria);
        final Page<Position> result = positionRepository.findAll(specification, page);
        return result.map(positionMapper::toDto);
    }

    /**
     * Function to convert PositionCriteria to a {@link Specifications}
     */
    private Specifications<Position> createSpecification(PositionCriteria criteria) {
        Specifications<Position> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Position_.id));
            }
            if (criteria.getAsset() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAsset(), Position_.asset));
            }
            if (criteria.getBuyAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBuyAt(), Position_.buyAt));
            }
            if (criteria.getSellAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSellAt(), Position_.sellAt));
            }
            if (criteria.getEntryValue() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEntryValue(), Position_.entryValue));
            }
            if (criteria.getExitValue() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExitValue(), Position_.exitValue));
            }
            if (criteria.getOperationType() != null) {
                specification = specification.and(buildSpecification(criteria.getOperationType(), Position_.operationType));
            }
        }
        return specification;
    }

}
