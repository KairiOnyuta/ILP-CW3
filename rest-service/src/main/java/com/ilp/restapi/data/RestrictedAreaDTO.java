package com.ilp.restapi.data;

import java.util.List;

public class RestrictedAreaDTO {

    private Integer id;
    private String name;
    private LimitsDTO limits;
    private List<LngLatDTO> vertices;

    public RestrictedAreaDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LimitsDTO getLimits() { return limits; }
    public void setLimits(LimitsDTO limits) { this.limits = limits; }

    public List<LngLatDTO> getVertices() { return vertices; }
    public void setVertices(List<LngLatDTO> vertices) { this.vertices = vertices; }
}

