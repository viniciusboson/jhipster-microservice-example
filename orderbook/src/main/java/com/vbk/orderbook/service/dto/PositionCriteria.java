package com.vbk.orderbook.service.dto;

import java.io.Serializable;
import com.vbk.orderbook.domain.enumeration.OperationType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;


import io.github.jhipster.service.filter.LocalDateFilter;



/**
 * Criteria class for the Position entity. This class is used in PositionResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /positions?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PositionCriteria implements Serializable {
    /**
     * Class for filtering OperationType
     */
    public static class OperationTypeFilter extends Filter<OperationType> {
    }

    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter asset;

    private LocalDateFilter buyAt;

    private LocalDateFilter sellAt;

    private DoubleFilter entryValue;

    private DoubleFilter exitValue;

    private OperationTypeFilter operationType;

    public PositionCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getAsset() {
        return asset;
    }

    public void setAsset(StringFilter asset) {
        this.asset = asset;
    }

    public LocalDateFilter getBuyAt() {
        return buyAt;
    }

    public void setBuyAt(LocalDateFilter buyAt) {
        this.buyAt = buyAt;
    }

    public LocalDateFilter getSellAt() {
        return sellAt;
    }

    public void setSellAt(LocalDateFilter sellAt) {
        this.sellAt = sellAt;
    }

    public DoubleFilter getEntryValue() {
        return entryValue;
    }

    public void setEntryValue(DoubleFilter entryValue) {
        this.entryValue = entryValue;
    }

    public DoubleFilter getExitValue() {
        return exitValue;
    }

    public void setExitValue(DoubleFilter exitValue) {
        this.exitValue = exitValue;
    }

    public OperationTypeFilter getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationTypeFilter operationType) {
        this.operationType = operationType;
    }

    @Override
    public String toString() {
        return "PositionCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (asset != null ? "asset=" + asset + ", " : "") +
                (buyAt != null ? "buyAt=" + buyAt + ", " : "") +
                (sellAt != null ? "sellAt=" + sellAt + ", " : "") +
                (entryValue != null ? "entryValue=" + entryValue + ", " : "") +
                (exitValue != null ? "exitValue=" + exitValue + ", " : "") +
                (operationType != null ? "operationType=" + operationType + ", " : "") +
            "}";
    }

}
