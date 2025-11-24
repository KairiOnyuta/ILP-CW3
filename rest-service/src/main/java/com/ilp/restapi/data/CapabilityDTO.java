package com.ilp.restapi.data;

public class CapabilityDTO {
    private boolean cooling;
    private boolean heating;
    private Double capacity;
    private int maxMoves;
    private Double costPerMove;
    private Double costInitial;
    private Double costFinal;


    public CapabilityDTO() {
    }

    public CapabilityDTO(boolean cooling, boolean heating) {
        this.cooling = cooling;
        this.heating = heating;
        this.capacity = 0.0;
        this.maxMoves = 0;
        this.costPerMove = 0.0;
        this.costInitial = 0.0;
        this.costFinal = 0.0;   
    }

    public boolean isCooling() {
        return cooling;
    }

    public void setCooling(boolean cooling) {
        this.cooling = cooling;
    }

    public boolean isHeating() {
        return heating;
    }

    public void setHeating(boolean heating) {
        this.heating = heating;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }   

    public int getMaxMoves() {
        return maxMoves;
    }
    public void setMaxMoves(int maxMoves) {
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
