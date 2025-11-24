package com.ilp.restapi.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class MovementVectorDTO {

    @Valid
    @NotNull
    private LngLatDTO start;

    @Valid
    @NotNull
    private Double angle;

    public MovementVectorDTO(LngLatDTO start, Double angle) {
        this.start = start;
        this.angle = angle;
    }

    public LngLatDTO getStart() {
        return start;
    }

    public void setStart(LngLatDTO start) {
        this.start = start;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }   
}
