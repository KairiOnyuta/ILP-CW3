package com.kairionyuta.ilp.graphql_gateway.graphql;

import com.kairionyuta.ilp.graphql_gateway.client.IlpRestClient;
import com.kairionyuta.ilp.graphql_gateway.data.DroneDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import com.kairionyuta.ilp.graphql_gateway.data.DroneDetailsDTO;
import com.kairionyuta.ilp.graphql_gateway.data.CapabilityDTO;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DroneQueryResolver {

    private final IlpRestClient ilpRestClient;

    public DroneQueryResolver(IlpRestClient ilpRestClient) {
        this.ilpRestClient = ilpRestClient;
    }

    @QueryMapping
    public List<DroneDTO> dronesWithCooling(@Argument boolean state) {

        // Step 1: get IDs from CW2
        List<Integer> ids = ilpRestClient.dronesWithCooling(state);

        // Step 2: expand each ID into a full DroneDTO
        List<DroneDTO> drones = new ArrayList<>();
        for (Integer id : ids) {
            DroneDetailsDTO drone = ilpRestClient.droneDetails(id);
            CapabilityDTO capability = drone.capability;

            DroneDTO out = new DroneDTO();
            out.setId(drone.id);
            out.setCooling(capability.cooling);
            out.setHeating(capability.heating);
            out.setCapacity(capability.capacity.doubleValue());
            out.setMaxMoves(capability.maxMoves);
            out.setCostPerMove(capability.costPerMove.doubleValue());
            out.setCostInitial(capability.costInitial.doubleValue());
            out.setCostFinal(capability.costFinal.doubleValue());

            drones.add(out);
        }

        // Step 3: returned list is shaped by your schema
        return drones;
    }
}
