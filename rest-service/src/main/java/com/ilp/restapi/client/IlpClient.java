package com.ilp.restapi.client;


import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ilp.restapi.data.DroneDTO;
import com.ilp.restapi.data.RestrictedAreaDTO;
import com.ilp.restapi.data.ServicePointAvailabilityDTO;
import com.ilp.restapi.data.ServicePointDTO;


@Component
public class IlpClient {

    private final String baseUrl;
    private final RestTemplate rest;

    public IlpClient(String ilpEndpoint, RestTemplateBuilder builder) {
    this.baseUrl = ilpEndpoint;

    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setConnectTimeout(5000);  // 5 seconds
    requestFactory.setReadTimeout(10000);    // 10 seconds

    this.rest = builder
            .requestFactory(() -> requestFactory)
            .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();
    }


    public List<DroneDTO> fetchDrones() {
        try {
            DroneDTO[] arr = rest.getForObject(baseUrl + "/drones", DroneDTO[].class);
            return Arrays.asList(Objects.requireNonNullElse(arr, new DroneDTO[0]));
        } catch (RestClientException ex) {
            // For this endpoint spec you still return 200 with [] on failures.
            return List.of();
        }
    }

    public List<ServicePointAvailabilityDTO> fetchAvailability() {
        try {
            ServicePointAvailabilityDTO[] arr =
                    rest.getForObject(baseUrl + "/drones-for-service-points", ServicePointAvailabilityDTO[].class);
            if (arr == null) {
                return List.of();
            }
            return Arrays.asList(arr);
        } catch (Exception e) {
            // If this fails, you can choose to treat as "no availability info"
            return List.of();
        }
    }

    public List<ServicePointDTO> fetchServicePoints() {
        try {
            ServicePointDTO[] arr =
                    rest.getForObject(baseUrl + "/service-points", ServicePointDTO[].class);
            if (arr == null) {
                return List.of();
            }
            return Arrays.asList(arr);
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<RestrictedAreaDTO> fetchRestrictedAreas() {
        try {
            RestrictedAreaDTO[] arr =
                    rest.getForObject(baseUrl + "/restricted-areas", RestrictedAreaDTO[].class);
            if (arr == null) {
                return List.of();
            }
            return Arrays.asList(arr);
        } catch (Exception e) {
            return List.of();   // if it fails, treat as “no restricted areas”
        }
    }


}

