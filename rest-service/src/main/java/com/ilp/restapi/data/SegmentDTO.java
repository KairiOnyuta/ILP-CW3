package com.ilp.restapi.data;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class SegmentDTO {

    @Valid
    @NotNull
    private LngLatDTO position1;

    @Valid
    @NotNull
    private LngLatDTO position2;

    public SegmentDTO(LngLatDTO position1, LngLatDTO position2) {
        this.position1 = position1;
        this.position2 = position2;
    }

    public LngLatDTO getPosition1() {
        return position1;
    }

    public void setPosition1(LngLatDTO position1) {
        this.position1 = position1;
    }

    public LngLatDTO getPosition2() {
        return position2;   
    }
    
    public void setPosition2(LngLatDTO position2) {
        this.position2 = position2;
    }
}