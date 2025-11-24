package com.ilp.restapi.data;

public class ServicePointDTO {

    private Integer id;
    private String name;
    private LngLatDTO location;

    public ServicePointDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LngLatDTO getLocation() {
        return location;
    }

    public void setLocation(LngLatDTO location) {
        this.location = location;
    }
}
