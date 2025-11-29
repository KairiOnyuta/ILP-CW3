package com.kairionyuta.ilp.graphql_gateway.data;

import java.util.List;

public class DeliveryPathResultDTO {
    public Double totalCost;
    public Integer totalMoves;
    public List<DronePathDTO> dronePaths;
}