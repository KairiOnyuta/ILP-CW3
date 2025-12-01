package com.kairionyuta.ilp.graphql_gateway.graphql;

import com.kairionyuta.ilp.graphql_gateway.client.IlpRestClient;
import com.kairionyuta.ilp.graphql_gateway.data.DroneDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import com.kairionyuta.ilp.graphql_gateway.data.DroneDetailsDTO;
import com.kairionyuta.ilp.graphql_gateway.data.CapabilityDTO;
import com.kairionyuta.ilp.graphql_gateway.data.DeliveryPathResultDTO;
import com.kairionyuta.ilp.graphql_gateway.data.QueryFilterInputDTO;
import com.kairionyuta.ilp.graphql_gateway.data.MedDispatchInputDTO;

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

        //get list of IDs from REST Service
        List<Integer> ids = ilpRestClient.dronesWithCooling(state);

        // expand each ID into a full DroneDTO
        List<DroneDTO> drones = new ArrayList<>();

        for (Integer id : ids) {
            // Copies capability from droneDetailsDTO to droneDTO
            DroneDTO out = toDroneDTO(ilpRestClient.droneDetails(id));
            if (out != null) {
                drones.add(out);
            }
        }

        return drones;
    }

    @QueryMapping
    public DroneDTO droneDetails(@Argument int id) {
        // Calls REST service with given id and converts response to DroneDTO
        return toDroneDTO(ilpRestClient.droneDetails(id));
    }

    @QueryMapping
    public List<DroneDTO> queryDrones(@Argument(name = "filters") List<QueryFilterInputDTO> filters) {

        // Gets list of IDs that match query from REST service
        List<Integer> ids = ilpRestClient.queryDrones(filters);

        // Expand each id into a full DroneDTO (same as cooling slice)
        List<DroneDTO> drones = new ArrayList<>();

        for (Integer id : ids) {
            DroneDTO out = toDroneDTO(ilpRestClient.droneDetails(id));
            if (out != null) {
                drones.add(out);
            }
        }

        return drones;
    }

    @QueryMapping
    public List<DroneDTO> availableDrones(@Argument(name = "dispatches") List<MedDispatchInputDTO> dispatches) {

        // Get list of available drone IDs from REST service
        List<Integer> ids = ilpRestClient.availableDrones(dispatches);

        // Expand each id into a full DroneDTO (same as cooling slice)
        List<DroneDTO> drones = new ArrayList<>();

        for (Integer id : ids) {
            DroneDTO out = toDroneDTO(ilpRestClient.droneDetails(id));
            if (out != null) {
                drones.add(out);
            }
        }

        return drones;
    }

    @MutationMapping
    public DeliveryPathResultDTO calculateDeliveryPath(
            @Argument(name = "dispatches") List<MedDispatchInputDTO> dispatches) {
        
        // Calls REST service to calculate delivery path with given dispatches
        return ilpRestClient.calculateDeliveryPath(dispatches);
    }

    // Helper to convert DroneDetailsDTO to DroneDTO
    private DroneDTO toDroneDTO(DroneDetailsDTO details) {
        if (details == null || details.capability == null) return null;

        CapabilityDTO capability = details.capability;

        DroneDTO out = new DroneDTO();
        out.setName(details.name);
        out.setId(details.id);
        out.setCooling(capability.cooling);
        out.setHeating(capability.heating);
        out.setCapacity(capability.capacity.doubleValue());
        out.setMaxMoves(capability.maxMoves);
        out.setCostPerMove(capability.costPerMove.doubleValue());
        out.setCostInitial(capability.costInitial.doubleValue());
        out.setCostFinal(capability.costFinal.doubleValue());
        return out;
    }


}
