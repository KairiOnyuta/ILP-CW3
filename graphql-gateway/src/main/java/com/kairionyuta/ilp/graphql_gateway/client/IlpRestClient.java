package com.kairionyuta.ilp.graphql_gateway.client;

import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.kairionyuta.ilp.graphql_gateway.data.DroneDetailsDTO;

import java.util.Arrays;
import java.util.List;

@Component
public class IlpRestClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ilp.rest.base-url}")
    private String baseUrl;

    public List<Integer> dronesWithCooling(boolean state) {
        String url = baseUrl + "/dronesWithCooling/" + state;
        Integer[] ids = restTemplate.getForObject(url, Integer[].class);
        return Arrays.asList(ids);
    }

    public DroneDetailsDTO droneDetails(int id) {
        String url = baseUrl + "/droneDetails/" + id;
        return restTemplate.getForObject(url, DroneDetailsDTO.class);
    }
}
