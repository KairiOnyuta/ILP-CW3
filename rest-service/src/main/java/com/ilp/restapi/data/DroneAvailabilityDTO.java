package com.ilp.restapi.data;

import java.util.List;

public class DroneAvailabilityDTO {

    private String id;  // in JSON it’s a string
    private List<AvailabilitySlotDTO> availability;

    public DroneAvailabilityDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AvailabilitySlotDTO> getAvailability() {
        return availability;
    }

    public void setAvailability(List<AvailabilitySlotDTO> availability) {
        this.availability = availability;
    }
}

