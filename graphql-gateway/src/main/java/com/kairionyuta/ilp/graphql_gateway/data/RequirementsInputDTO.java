package com.kairionyuta.ilp.graphql_gateway.data;

public class RequirementsInputDTO {
    private Double capacity;
    private Boolean cooling;
    private Boolean heating;
    private Integer maxCost;

    public RequirementsInputDTO() {}

    public Double getCapacity() { return capacity; }
    public void setCapacity(Double capacity) { this.capacity = capacity; }

    public Boolean getCooling() { return cooling; }
    public void setCooling(Boolean cooling) { this.cooling = cooling; }

    public Boolean getHeating() { return heating; }
    public void setHeating(Boolean heating) { this.heating = heating; }

    public Integer getMaxMoves() { return maxCost; }
    public void setMaxMoves(Integer maxMoves) { this.maxCost = maxMoves; }
}
