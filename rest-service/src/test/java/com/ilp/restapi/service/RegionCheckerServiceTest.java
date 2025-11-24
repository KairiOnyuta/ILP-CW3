package com.ilp.restapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.ilp.restapi.data.LngLatDTO;
import com.ilp.restapi.data.RegionCheckDTO;
import com.ilp.restapi.data.RegionDTO;

class RegionCheckerServiceTest {

    @InjectMocks
    private RegionCheckerService service;

    // Mock objects for the primary inputs
    @Mock
    private RegionCheckDTO mockCheck;
    @Mock
    private RegionDTO mockRegion;
    @Mock
    private LngLatDTO mockPosition;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Link the check object to its components
        when(mockCheck.getRegion()).thenReturn(mockRegion);
        when(mockCheck.getPosition()).thenReturn(mockPosition);
    }

    // --- Helper for creating a clean, stubbed LngLatDTO mock ---
    
    private LngLatDTO createMockLngLat(double lat, double lng) {
        LngLatDTO mockV = mock(LngLatDTO.class);
        when(mockV.getLat()).thenReturn(lat);
        when(mockV.getLng()).thenReturn(lng);
        return mockV;
    }

    // --- Helper for creating a mockable closed square region ---
    
    private List<LngLatDTO> mockClosedSquare() {
        // Create V0 once
        LngLatDTO mockV0 = createMockLngLat(10.0, 10.0);
        
        // Creates a list of fully stubbed mock LngLatDTOs, reusing mockV0 for V4
        return Arrays.asList(
            mockV0,                              // V0 (Lat 10, Lng 10) - Start
            createMockLngLat(20.0, 10.0),        // V1 (Lat 20, Lng 10)
            createMockLngLat(20.0, 20.0),        // V2 (Lat 20, Lng 20)
            createMockLngLat(10.0, 20.0),        // V3 (Lat 10, Lng 20)
            mockV0                               // V4 (Lat 10, Lng 10) - Closure, reuse V0 mock
        );
    }
    
    // --- Validation Tests (Expect ResponseStatusException) ---

    /**
     * Test case 1: Fails polygon minimum vertex requirement (< 4 vertices).
     */
    @Test
    void testInvalidRegion_TooFewVertices() {
        // Arrange: Only 3 vertices
        List<LngLatDTO> fewVertices = mockClosedSquare().subList(0, 3);
        when(mockRegion.getVertices()).thenReturn(fewVertices);

        // Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.isInRegion(mockCheck);
        }, "Should throw 400 for less than 4 vertices.");
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
    
    /**
     * Test case 2: Fails polygon closure requirement (First != Last vertex).
     */
    @Test
    void testInvalidRegion_NotClosed() {
        // Arrange: Use 5 vertices, but ensure last vertex is outside epsilon of the first.
        
        
        LngLatDTO mockV0 = createMockLngLat(10.0, 10.0);
        LngLatDTO mockV4Different = createMockLngLat(10.1, 10.1); 
        
        List<LngLatDTO> vertices = Arrays.asList(
            mockV0,
            createMockLngLat(20.0, 10.0),
            createMockLngLat(20.0, 20.0),
            createMockLngLat(10.0, 20.0),
            mockV4Different 
        );
        
        when(mockRegion.getVertices()).thenReturn(vertices);
        
        // Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.isInRegion(mockCheck);
        }, "Should throw 400 for a polygon that is not closed.");
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    // --- Point-in-Polygon Tests ---

    /**
     * Test case 3: Point is clearly inside the square.
     */
    @Test
    void testPointIsInsideRegion() {
        // ARRANGE: Prepare the list before stubbing
        List<LngLatDTO> vertices = mockClosedSquare(); 
        when(mockRegion.getVertices()).thenReturn(vertices); // Stub the mock method
        
        // Stub the query position: (Lat 15.0, Lng 15.0)
        when(mockPosition.getLat()).thenReturn(15.0); 
        when(mockPosition.getLng()).thenReturn(15.0); 

        // Act & Assert
        assertEquals(true, service.isInRegion(mockCheck), "Point (15, 15) should be inside the square.");
    }
    
    /**
     * Test case 4: Point is clearly outside the square.
     */
    @Test
    void testPointIsOutsideRegion() {
        // ARRANGE
        List<LngLatDTO> vertices = mockClosedSquare();
        when(mockRegion.getVertices()).thenReturn(vertices);
        
        // Stub the query position: (Lat 5.0, Lng 5.0)
        when(mockPosition.getLat()).thenReturn(5.0); 
        when(mockPosition.getLng()).thenReturn(5.0); 

        // Act & Assert
        assertEquals(false, service.isInRegion(mockCheck), "Point (5, 5) should be outside the square.");
    }
    
    /**
     * Test case 5: Point is on the boundary
     */
    @Test
    void testPointIsOnBoundary() {
        // ARRANGE
        List<LngLatDTO> vertices = mockClosedSquare();
        when(mockRegion.getVertices()).thenReturn(vertices);
        
        // Stub the query position: (Lat 15.0, Lng 10.0) is on the left edge.
        when(mockPosition.getLat()).thenReturn(15.0); 
        when(mockPosition.getLng()).thenReturn(10.0); 

        // Act & Assert
        assertEquals(true, service.isInRegion(mockCheck), "Point on boundary (15, 10) must be true.");
    }
    
    /**
     * Test case 6: Point near the boundary but inside (ensuring no false negatives).
     */
    @Test
    void testPointNearBoundaryInside() {
        // ARRANGE
        List<LngLatDTO> vertices = mockClosedSquare();
        when(mockRegion.getVertices()).thenReturn(vertices);
        
        // Stub the query position: (Lat 15.0001, Lng 10.0001) is just inside the left edge.
        when(mockPosition.getLat()).thenReturn(15.0001); 
        when(mockPosition.getLng()).thenReturn(10.0001); 

        // Act & Assert
        assertEquals(true, service.isInRegion(mockCheck), "Point just inside the boundary must be true.");
    }
    
    /**
     * Test case 7: Point near the boundary but outside (ensuring no false positives).
     */
    @Test
    void testPointNearBoundaryOutside() {
        // ARRANGE
        List<LngLatDTO> vertices = mockClosedSquare();
        when(mockRegion.getVertices()).thenReturn(vertices);
        
        // Stub the query position: (Lat 15.0, Lng 9.9999) is just outside the left edge.
        when(mockPosition.getLat()).thenReturn(15.0); 
        when(mockPosition.getLng()).thenReturn(9.9999); 

        // Act & Assert
        assertEquals(false, service.isInRegion(mockCheck), "Point just outside the boundary must be false.");
    }
}
