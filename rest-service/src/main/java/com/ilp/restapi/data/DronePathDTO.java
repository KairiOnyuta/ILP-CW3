package com.ilp.restapi.data;

import java.util.List;

public class DronePathDTO {
    private String droneId;
    private List<DeliveryFlightPathDTO> deliveries;

    public String getDroneId() { return droneId; }
    public void setDroneId(String droneId) { this.droneId = droneId; }

    public List<DeliveryFlightPathDTO> getDeliveries() { return deliveries; }
    public void setDeliveries(List<DeliveryFlightPathDTO> deliveries) { this.deliveries = deliveries; }
}
