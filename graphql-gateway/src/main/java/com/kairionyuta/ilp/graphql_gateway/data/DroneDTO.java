package com.kairionyuta.ilp.graphql_gateway.data;

public class DroneDTO {
    private String name;
    private Integer id;
    private Boolean cooling;
    private Boolean heating;
    private Double capacity;
    private Integer maxMoves;
    private Double costPerMove;
    private Double costInitial;
    private Double costFinal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Integer getMaxMoves() {
        return maxMoves;
    }

    public void setMaxMoves(Integer maxMoves) {
        this.maxMoves = maxMoves;
    }   

    public Double getCostPerMove() {
        return costPerMove;
    }

    public void setCostPerMove(Double costPerMove) {
        this.costPerMove = costPerMove;
    }

    public Double getCostInitial() {
        return costInitial;
    }

    public void setCostInitial(Double costInitial) {
        this.costInitial = costInitial;
    }

    public Double getCostFinal() {
        return costFinal;
    }

    public void setCostFinal(Double costFinal) {
        this.costFinal = costFinal;
    }
}
