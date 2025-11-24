package com.ilp.restapi.data;

import java.util.List;

public class ServicePointAvailabilityDTO {

    private int servicePointId;
    private List<DroneAvailabilityDTO> drones;

    public ServicePointAvailabilityDTO() {
    }

    public int getServicePointId() {
        return servicePointId;
    }

    public void setServicePointId(int servicePointId) {
        this.servicePointId = servicePointId;
    }

    public List<DroneAvailabilityDTO> getDrones() {
        return drones;
    }

    public void setDrones(List<DroneAvailabilityDTO> drones) {
        this.drones = drones;
    }
}

