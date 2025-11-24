package com.ilp.restapi.data;

import jakarta.validation.constraints.NotNull;

public class RequirementsDTO {

    @NotNull
    private Double capacity;
    private Boolean cooling;
    private Boolean heating;
    private Double maxCost;  // optional, you can ignore it in logic for now

    public RequirementsDTO() {
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Boolean getCooling() {
        return cooling;
    }

    public void setCooling(Boolean cooling) {
        this.cooling = cooling;
    }

    public Boolean getHeating() {
        return heating;
    }

    public void setHeating(Boolean heating) {
        this.heating = heating;
    }

    public Double getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(Double maxCost) {
        this.maxCost = maxCost;
    }
}

