package com.ilp.restapi.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class RegionCheckDTO {
    
    @Valid
    @NotNull
    private LngLatDTO position;

    @Valid
    @NotNull
    private RegionDTO region;

    public RegionCheckDTO(LngLatDTO position, RegionDTO region) {
        this.position = position;
        this.region = region;
    }

    public LngLatDTO getPosition() {
        return position;
    }

    public void setPosition(LngLatDTO position) {
        this.position = position;
    }

    public RegionDTO getRegion() {
        return region;
    }

    public void setRegion(RegionDTO region) {
        this.region = region;
    }
}
