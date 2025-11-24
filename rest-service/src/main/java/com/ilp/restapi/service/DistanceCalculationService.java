package com.ilp.restapi.service;

import com.ilp.restapi.data.SegmentDTO;
import org.springframework.stereotype.Service;

@Service
public class DistanceCalculationService {

    public Double distanceTo(SegmentDTO coordinate) {
        Double firstLatitude = coordinate.getPosition1().getLat();
        Double firstLongitude = coordinate.getPosition1().getLng();

        Double secondLatitude = coordinate.getPosition2().getLat();
        Double secondLongitude = coordinate.getPosition2().getLng();

        // Implementation of Euclidean distance in Java
        return Math.sqrt(Math.pow(firstLatitude - secondLatitude, 2) + 
                         Math.pow(firstLongitude - secondLongitude, 2));
    }

}

