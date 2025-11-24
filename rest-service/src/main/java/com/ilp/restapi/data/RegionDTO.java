package com.ilp.restapi.data;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class RegionDTO {

    @Valid
    @NotNull
    private String name;

    @Valid
    @NotNull
    private List<LngLatDTO> vertices;

    public RegionDTO() {
        this.vertices = new ArrayList<>();
    }

    // Constructor only created for test purposes
    public RegionDTO(List<LngLatDTO> vertices) {
        this.vertices = vertices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LngLatDTO> getVertices() {
        return vertices;
    }

    public void setVertices(List<LngLatDTO> vertices) {
        this.vertices = vertices;
    }

}
