package com.ilp.restapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import java.util.List;

import com.ilp.restapi.data.CalcDeliveryPathResponseDTO;
import com.ilp.restapi.data.DroneDTO;
import com.ilp.restapi.data.LngLatDTO;
import com.ilp.restapi.data.MedDispatchRecDTO;
import com.ilp.restapi.data.RegionCheckDTO;
import com.ilp.restapi.data.MovementVectorDTO;
import com.ilp.restapi.data.QueryConditionDTO;
import com.ilp.restapi.data.SegmentDTO;
import com.ilp.restapi.service.DistanceCalculationService;
import com.ilp.restapi.service.DroneService;
import com.ilp.restapi.service.NextPositionService;
import com.ilp.restapi.service.RegionCheckerService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class Controller {
    

    // Uses the constructor method to initialize the services
    private DistanceCalculationService distanceCalculationService;
    private NextPositionService nextPositionService;
    private RegionCheckerService regionCheckerService;
    private DroneService droneService;

    public Controller(DistanceCalculationService distanceCalculationService, 
                          NextPositionService nextPositionService,
                          RegionCheckerService regionCheckerService,
                          DroneService droneService) {

        this.distanceCalculationService = distanceCalculationService;
        this.nextPositionService = nextPositionService;
        this.regionCheckerService = regionCheckerService;
        this.droneService = droneService;
    }

    @GetMapping("/uid")
    public String getUid() {
        return "s2456789";
    }

    @PostMapping("/distanceTo")
    public Double distanceTo(@Valid @RequestBody SegmentDTO coordinate) {
        return distanceCalculationService.distanceTo(coordinate);

    }

    @PostMapping("/isCloseTo")
    public Boolean isCloseTo(@Valid @RequestBody SegmentDTO coordinate) {

        Double THRESHOLD_DISTANCE = 0.00015;
        return distanceCalculationService.distanceTo(coordinate) < THRESHOLD_DISTANCE;
    }

    @PostMapping("/nextPosition")
    public LngLatDTO nextPosition(@Valid @RequestBody MovementVectorDTO movementVector) {
        return nextPositionService.nextPosition(movementVector);
    }

    @PostMapping("/isInRegion")
    public boolean isInRegion(@Valid @RequestBody RegionCheckDTO regionChecker) {
        return regionCheckerService.isInRegion(regionChecker);
        
    }
    @GetMapping("/dronesWithCooling/{state}")
    public ResponseEntity<List<Integer>> getDronesWithCooling(@PathVariable String state) {
        return ResponseEntity.ok(droneService.findIdsByCooling(state));
    }

    @GetMapping("/droneDetails/{id}")
    public ResponseEntity<DroneDTO> getDroneDetails(@PathVariable int id) {
        DroneDTO drone = droneService.findDroneById(id);
        if (drone == null) {
            // This is the one endpoint where the spec *wants* 404 for invalid/non-existent id
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(drone);
    }

    @GetMapping("/queryAsPath/{attribute}/{value}")
    public ResponseEntity<List<Integer>> queryAsPath(
        @PathVariable String attribute,
        @PathVariable String value) {

        List<Integer> result = droneService.queryAsPath(attribute, value);
        return ResponseEntity.ok(result);  // always 200 per spec
    } 

    @PostMapping("/query")
    public ResponseEntity<List<Integer>> query(
            @RequestBody(required = false) List<QueryConditionDTO> conditions) {

        // If body is missing or invalid, treat as empty list
        if (conditions == null) {
            conditions = List.of();
        }

        List<Integer> result = droneService.query(conditions);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/queryAvailableDrones")
    public ResponseEntity<List<Integer>> queryAvailableDrones(
            @RequestBody(required = false) List<@Valid MedDispatchRecDTO> dispatches) {

        if (dispatches == null) {
            // To avoid 400 and follow the "always 200" rule,
            // treat null body as "no requirements" ⇒ all drones or []
            dispatches = List.of();
        }

        List<Integer> result = droneService.queryAvailableDrones(dispatches);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/calcDeliveryPath")
    public ResponseEntity<CalcDeliveryPathResponseDTO> calcDeliveryPath(
            @Valid @RequestBody List<@Valid MedDispatchRecDTO> dispatches) {

        CalcDeliveryPathResponseDTO response = droneService.calcDeliveryPath(dispatches);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/calcDeliveryPathAsGeoJson")
    public ResponseEntity<String> calcDeliveryPathAsGeoJson(
            @Valid @RequestBody List<@Valid MedDispatchRecDTO> dispatches) {

        String geoJson = droneService.calcDeliveryPathAsGeoJson(dispatches);
        return ResponseEntity.ok(geoJson);
    }




}
