package com.vbk.orderbook.service.dto;


import java.time.LocalDate;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.vbk.orderbook.domain.enumeration.OperationType;

/**
 * A DTO for the Position entity.
 */
public class PositionDTO implements Serializable {

    private Long id;

    @NotNull
    private String asset;

    private LocalDate buyAt;

    private LocalDate sellAt;

    private Double entryValue;

    private Double exitValue;

    @NotNull
    private OperationType operationType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public LocalDate getBuyAt() {
        return buyAt;
    }

    public void setBuyAt(LocalDate buyAt) {
        this.buyAt = buyAt;
    }

    public LocalDate getSellAt() {
        return sellAt;
    }

    public void setSellAt(LocalDate sellAt) {
        this.sellAt = sellAt;
    }

    public Double getEntryValue() {
        return entryValue;
    }

    public void setEntryValue(Double entryValue) {
        this.entryValue = entryValue;
    }

    public Double getExitValue() {
        return exitValue;
    }

    public void setExitValue(Double exitValue) {
        this.exitValue = exitValue;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PositionDTO positionDTO = (PositionDTO) o;
        if(positionDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), positionDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PositionDTO{" +
            "id=" + getId() +
            ", asset='" + getAsset() + "'" +
            ", buyAt='" + getBuyAt() + "'" +
            ", sellAt='" + getSellAt() + "'" +
            ", entryValue='" + getEntryValue() + "'" +
            ", exitValue='" + getExitValue() + "'" +
            ", operationType='" + getOperationType() + "'" +
            "}";
    }
}
