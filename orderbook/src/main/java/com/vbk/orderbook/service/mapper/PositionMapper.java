package com.vbk.orderbook.service.mapper;

import com.vbk.orderbook.domain.*;
import com.vbk.orderbook.service.dto.PositionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Position and its DTO PositionDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PositionMapper extends EntityMapper <PositionDTO, Position> {
    
    
    default Position fromId(Long id) {
        if (id == null) {
            return null;
        }
        Position position = new Position();
        position.setId(id);
        return position;
    }
}
