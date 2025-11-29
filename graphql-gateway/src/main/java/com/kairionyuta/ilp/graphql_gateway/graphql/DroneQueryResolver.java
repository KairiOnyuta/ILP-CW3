package com.kairionyuta.ilp.graphql_gateway.graphql;

import com.kairionyuta.ilp.graphql_gateway.client.IlpRestClient;
import com.kairionyuta.ilp.graphql_gateway.data.DroneDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import com.kairionyuta.ilp.graphql_gateway.data.DroneDetailsDTO;
import com.kairionyuta.ilp.graphql_gateway.data.CapabilityDTO;
import com.kairionyuta.ilp.graphql_gateway.data.QueryFilterInputDTO;

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

            // Copies capability from droneDetailsDTO to droneDTO
            DroneDTO out = new DroneDTO();
            out.setName(drone.name);
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

    @QueryMapping
    public DroneDTO droneDetails(@Argument int id) {
        DroneDetailsDTO droneDetails = ilpRestClient.droneDetails(id);
        if (droneDetails == null || droneDetails.capability == null) return null;

        CapabilityDTO capability = droneDetails.capability;

        DroneDTO out = new DroneDTO();
        out.setName(droneDetails.name);
        out.setId(droneDetails.id);
        out.setCooling(capability.cooling);
        out.setHeating(capability.heating);
        out.setCapacity(capability.capacity.doubleValue());
        out.setMaxMoves(capability.maxMoves);
        out.setCostPerMove(capability.costPerMove.doubleValue());
        out.setCostInitial(capability.costInitial.doubleValue());
        out.setCostFinal(capability.costFinal.doubleValue());

        return out;
    }

    @QueryMapping
    public List<DroneDTO> queryDrones(@Argument(name = "filters") List<QueryFilterInputDTO> filters) {

    // 1) Ask CW2 dynamic query for matching IDs
    List<Integer> ids = ilpRestClient.queryDrones(filters);

    // 2) Expand each id into a full DroneDTO (same as cooling slice)
    List<DroneDTO> drones = new ArrayList<>();

    for (Integer id : ids) {
        DroneDetailsDTO droneDetails = ilpRestClient.droneDetails(id);

        if (droneDetails == null || droneDetails.capability == null) continue;

        CapabilityDTO capability = droneDetails.capability;

        DroneDTO out = new DroneDTO();
        out.setName(droneDetails.name);
        out.setId(droneDetails.id);
        out.setCooling(capability.cooling);
        out.setHeating(capability.heating);
        out.setCapacity(capability.capacity.doubleValue());
        out.setMaxMoves(capability.maxMoves);
        out.setCostPerMove(capability.costPerMove.doubleValue());
        out.setCostInitial(capability.costInitial.doubleValue());
        out.setCostFinal(capability.costFinal.doubleValue());

        drones.add(out);
    }

    return drones;
}


}
