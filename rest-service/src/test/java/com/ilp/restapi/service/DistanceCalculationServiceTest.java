package com.ilp.restapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ilp.restapi.data.LngLatDTO;
import com.ilp.restapi.data.SegmentDTO;

class DistanceCalculationServiceTest {

    @InjectMocks
    private DistanceCalculationService service;

    // Create mock objects for the input structure
    @Mock
    private SegmentDTO mockSegment;
    @Mock
    private LngLatDTO mockP1;
    @Mock
    private LngLatDTO mockP2;

    private static final double DELTA = 0.00001; 

    @BeforeEach
    void setUp() {
        // Initialize Mockito annotations before each test
        MockitoAnnotations.openMocks(this);
        
        // Link the segment to the positions
        when(mockSegment.getPosition1()).thenReturn(mockP1);
        when(mockSegment.getPosition2()).thenReturn(mockP2);
    }

    // --- Core Test Cases ---

    /**
     * Test case 1: Zero distance (start and end points are identical).
     */
    @Test
    void testZeroDistance() {
        // Arrange - Mock getters to return the same coordinate
        double lat = 10.0;
        double lng = 20.0;
        
        when(mockP1.getLat()).thenReturn(lat);
        when(mockP1.getLng()).thenReturn(lng);
        when(mockP2.getLat()).thenReturn(lat);
        when(mockP2.getLng()).thenReturn(lng);

        // Act
        Double result = service.distanceTo(mockSegment);

        // Assert
        assertEquals(0.0, result, DELTA, "Distance between identical points should be 0.");
    }

    /**
     * Test case 2: Horizontal distance (change only in Longitude).
     */
    @Test
    void testHorizontalDistance() {
        // Arrange
        when(mockP1.getLat()).thenReturn(50.0);
        when(mockP1.getLng()).thenReturn(10.0);
        when(mockP2.getLat()).thenReturn(50.0);
        when(mockP2.getLng()).thenReturn(30.0);

        // Act
        Double result = service.distanceTo(mockSegment);

        // Assert
        assertEquals(20.0, result, DELTA, "Distance should match the difference in longitudes.");
    }

    /**
     * Test case 3: Vertical distance (change only in Latitude).
     */
    @Test
    void testVerticalDistance() {
        // Arrange
        when(mockP1.getLat()).thenReturn(10.0);
        when(mockP1.getLng()).thenReturn(45.0);
        when(mockP2.getLat()).thenReturn(40.0);
        when(mockP2.getLng()).thenReturn(45.0); 

        // Act
        Double result = service.distanceTo(mockSegment);

        // Assert
        assertEquals(30.0, result, DELTA, "Distance should match the difference in latitudes.");
    }

    /**
     * Test case 4: Diagonal distance using a known 3-4-5 Pythagorean triangle.
     */
    @Test
    void testDiagonalDistance() {
        // Arrange
        when(mockP1.getLat()).thenReturn(0.0);
        when(mockP1.getLng()).thenReturn(0.0);
        when(mockP2.getLat()).thenReturn(4.0);
        when(mockP2.getLng()).thenReturn(3.0); 

        // Act
        Double result = service.distanceTo(mockSegment);

        // Assert
        assertEquals(5.0, result, DELTA, "Distance should be 5.0 for a 3-4-5 triangle.");
    }

    /**
     * Test case 5: Distance with negative coordinates.
     */
    @Test
    void testNegativeCoordinates() {
        // Arrange
        when(mockP1.getLat()).thenReturn(-10.0);
        when(mockP1.getLng()).thenReturn(-5.0);
        when(mockP2.getLat()).thenReturn(-14.0);
        when(mockP2.getLng()).thenReturn(-8.0);

        // Act
        Double result = service.distanceTo(mockSegment);

        // Assert
        assertEquals(5.0, result, DELTA, "Distance should be 5.0 with negative coordinates.");
    }
}
