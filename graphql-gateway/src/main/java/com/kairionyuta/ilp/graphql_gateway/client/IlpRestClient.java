package com.kairionyuta.ilp.graphql_gateway.client;

import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.kairionyuta.ilp.graphql_gateway.data.DroneDetailsDTO;
import com.kairionyuta.ilp.graphql_gateway.data.QueryFilterInputDTO;

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

    public List<Integer> queryDrones(List<QueryFilterInputDTO> filters) {
        String url = baseUrl + "/query";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<QueryFilterInputDTO>> request = 
                new HttpEntity<>(filters, headers);

        Integer[] ids = restTemplate.postForObject(url, request, Integer[].class);

        return Arrays.asList(ids);

    }
}
