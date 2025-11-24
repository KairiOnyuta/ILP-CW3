package com.ilp.restapi.data;

public class DroneDTO {
    private String name;
    private int id;
    private CapabilityDTO capability;
    
    public DroneDTO() {
    }

    public DroneDTO(String name, int id, CapabilityDTO capability) {
        this.name = name;
        this.id = id;
        this.capability = capability;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public CapabilityDTO getCapability() {
        return capability;
    }

    public void setCapability(CapabilityDTO capability) {
        this.capability = capability;
    }
}
