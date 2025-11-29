package com.kairionyuta.ilp.graphql_gateway.data;

public class DeliveryInputDTO {
    private Double lng;
    private Double lat;

    public DeliveryInputDTO() {}

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
}
