package com.ilp.restapi.data;

import java.util.List;

public class DeliveryFlightPathDTO {
    private Integer deliveryId;
    private List<LngLatDTO> flightPath;

    public Integer getDeliveryId() { return deliveryId; }
    public void setDeliveryId(Integer deliveryId) { this.deliveryId = deliveryId; }

    public List<LngLatDTO> getFlightPath() { return flightPath; }
    public void setFlightPath(List<LngLatDTO> flightPath) { this.flightPath = flightPath; }
}

