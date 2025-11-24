package com.ilp.restapi.service;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ilp.restapi.data.LngLatDTO;
import com.ilp.restapi.data.MovementVectorDTO;

@Service
public class NextPositionService {
    
    public LngLatDTO nextPosition(MovementVectorDTO movementVector) {
        Double latitude = movementVector.getStart().getLat();
        Double longitude = movementVector.getStart().getLng();

        Double angle = movementVector.getAngle();

        if (angle % 22.5 != 0) { // Angle must be a multiple of 22.5
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data");
        }

        if (angle < 0 || angle >= 360) { // Angle must be in [0, 360)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data");
        }

        // Use euclidean geometry to calculate the changes in latitude and longitude
        Double changeInLatitude = 0.00015 * Math.sin(Math.toRadians(angle));
        Double changeInLongitude = 0.00015 * Math.cos(Math.toRadians(angle));

        return new LngLatDTO(longitude + changeInLongitude, latitude + changeInLatitude);
    }
}
