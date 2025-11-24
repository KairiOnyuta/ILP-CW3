package com.ilp.restapi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.ilp.restapi.data.LngLatDTO;
import com.ilp.restapi.data.RegionCheckDTO;
import com.ilp.restapi.data.RegionDTO;

@Service
public class RegionCheckerService {
    public boolean isInRegion(RegionCheckDTO regionChecker) {
        LngLatDTO position = regionChecker.getPosition();
        RegionDTO region = regionChecker.getRegion();
        List<LngLatDTO> vertices = region.getVertices();

        int numberOfVertices = vertices.size();

        if (numberOfVertices < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data");
        }

        LngLatDTO firstVertex = vertices.get(0);
        LngLatDTO lastVertex = vertices.get(numberOfVertices - 1);

        final Double EPSILON = 0.00001;
        boolean latitudesAreDifferent = Math.abs(firstVertex.getLat() - lastVertex.getLat()) > EPSILON;
        boolean longitudesAreDifferent = Math.abs(firstVertex.getLng() - lastVertex.getLng()) > EPSILON;

        if (latitudesAreDifferent || longitudesAreDifferent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data");
        }

        int intersections = 0;

        Double xp = position.getLng();
        Double yp = position.getLat();
        // Starts with the last vertex to form a closed loop
        LngLatDTO p1 = vertices.get(numberOfVertices-2);


        for (int i = 0; i < numberOfVertices - 1; i++) {
            LngLatDTO p2 = vertices.get(i);

            Double x1 = p1.getLng();
            Double y1 = p1.getLat();
            Double x2 = p2.getLng();
            Double y2 = p2.getLat();


            // Checks if the y coordinate of the point is between the y coordinates
            // of the edge
            if (((yp < y1) != (yp < y2)) &&
            // Formula to check if the x coordinate of the point is to the left of
            // the edge
               (xp < x1 + ((yp-y1)/(y2-y1)) * (x2-x1))){
            // If these are both true, the ray intersects the edge
                intersections++;
            }

            // Move to the next edge
            p1 = p2;
        }
        if (intersections % 2 == 1) { // If odd number of intersections
            return true; 
        } else {
            return false;
        }
    }   
}
