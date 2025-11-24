package com.ilp.restapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations; 
import org.springframework.web.server.ResponseStatusException;

import com.ilp.restapi.data.LngLatDTO;
import com.ilp.restapi.data.MovementVectorDTO;

class NextPositionServiceTest {

    @InjectMocks
    private NextPositionService service;
    
    // Mock input objects
    @Mock
    private MovementVectorDTO mockVector;
    @Mock
    private LngLatDTO mockStart; 
    
    private static final double DELTA = 0.00000001; 
    private static final double DISTANCE = 0.00015;

    @BeforeEach
    void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);
        
        // Link the vector to the starting position mock
        when(mockVector.getStart()).thenReturn(mockStart);
    }

    // --- Validation Tests (Expect 400 ResponseStatusException) ---

    /**
     * Test case 1: Angle is not a multiple of 22.5 (e.g., 10.0 degrees).
     */
    @Test
    void testInvalidAngle_NotMultipleOf22_5() {
        // Arrange
        when(mockVector.getAngle()).thenReturn(10.0);
        
        // Assert
        assertThrows(ResponseStatusException.class, () -> {
            service.nextPosition(mockVector);
        }, "Should throw 400 for angle not divisible by 22.5.");
    }
    
    /**
     * Test case 2: Angle is exactly 360.0 (violates angle < 360).
     */
    @Test
    void testInvalidAngle_BoundaryAt360() {
        // Arrange
        when(mockVector.getAngle()).thenReturn(360.0);

        // Assert
        assertThrows(ResponseStatusException.class, () -> {
            service.nextPosition(mockVector);
        }, "Should throw 400 for angle >= 360.");
    }
    
    /**
     * Test case 3: Angle is negative (violates angle >= 0).
     */
    @Test
    void testInvalidAngle_Negative() {
        // Arrange
        when(mockVector.getAngle()).thenReturn(-22.5);

        // Assert
        assertThrows(ResponseStatusException.class, () -> {
            service.nextPosition(mockVector);
        }, "Should throw 400 for negative angle.");
    }

    // --- Successful Calculation Tests ---

    /**
     * Test case 4: Movement East (0 degrees). 
     * Expect Lng change = DISTANCE, Lat change = 0.
     */
    @Test
    void testValidAngle_ZeroDegrees_East() {
        // Arrange
        double startLat = 50.0;
        double startLng = 10.0;
        
        when(mockStart.getLat()).thenReturn(startLat);
        when(mockStart.getLng()).thenReturn(startLng);
        when(mockVector.getAngle()).thenReturn(0.0);
        
        double expectedLat = startLat + 0.0; 
        double expectedLng = startLng + DISTANCE * Math.cos(Math.toRadians(0.0));

        // Act
        LngLatDTO result = service.nextPosition(mockVector);

        // Assert
        assertNotNull(result);
        assertEquals(expectedLat, result.getLat(), DELTA, "Latitude should be unchanged at 0 degrees.");
        assertEquals(expectedLng, result.getLng(), DELTA, "Longitude should increase by distance at 0 degrees.");
    }

    /**
     * Test case 5: Movement North (90 degrees).
     * Expect Lng change = 0, Lat change = DISTANCE.
     */
    @Test
    void testValidAngle_90Degrees_North() {
        // Arrange
        double startLat = 50.0;
        double startLng = 10.0;
        
        when(mockStart.getLat()).thenReturn(startLat);
        when(mockStart.getLng()).thenReturn(startLng);
        when(mockVector.getAngle()).thenReturn(90.0);
        
        double expectedLat = startLat + DISTANCE * Math.sin(Math.toRadians(90.0));
        double expectedLng = startLng + 0.0; 

        // Act
        LngLatDTO result = service.nextPosition(mockVector);

        // Assert
        assertEquals(expectedLat, result.getLat(), DELTA, "Latitude should increase by distance at 90 degrees.");
        assertEquals(expectedLng, result.getLng(), DELTA, "Longitude should be unchanged at 90 degrees.");
    }

    /**
     * Test case 6: Movement West (180 degrees).
     * Expect Lng change = -DISTANCE, Lat change = 0.
     */
    @Test
    void testValidAngle_180Degrees_West() {
        // Arrange
        double startLat = 50.0;
        double startLng = 10.0;
        
        when(mockStart.getLat()).thenReturn(startLat);
        when(mockStart.getLng()).thenReturn(startLng);
        when(mockVector.getAngle()).thenReturn(180.0);
        
        double expectedLat = startLat + 0.0; 
        double expectedLng = startLng + DISTANCE * Math.cos(Math.toRadians(180.0));

        // Act
        LngLatDTO result = service.nextPosition(mockVector);

        // Assert
        assertEquals(expectedLat, result.getLat(), DELTA, "Latitude should be unchanged at 180 degrees.");
        assertEquals(expectedLng, result.getLng(), DELTA, "Longitude should decrease by distance at 180 degrees.");
    }
    
    /**
     * Test case 7: Diagonal movement (45 degrees, NE).
     * Expect both Lat and Lng to change by positive amount.
     */
    @Test
    void testValidAngle_45Degrees_Diagonal() {
        // Arrange
        double startLat = 50.0;
        double startLng = 10.0;
        
        when(mockStart.getLat()).thenReturn(startLat);
        when(mockStart.getLng()).thenReturn(startLng);
        when(mockVector.getAngle()).thenReturn(45.0);
        
        double change = DISTANCE * Math.sin(Math.toRadians(45.0));
        
        // Act
        LngLatDTO result = service.nextPosition(mockVector);

        // Assert
        assertEquals(startLat + change, result.getLat(), DELTA, "Latitude change should be correct for 45 degrees.");
        assertEquals(startLng + change, result.getLng(), DELTA, "Longitude change should be correct for 45 degrees.");
    }

    /**
     * Test case 8: Edge case 0 degrees (must be inclusive).
     */
    @Test
    void testValidAngle_BoundaryAtZero() {
        // Arrange
        when(mockStart.getLat()).thenReturn(10.0);
        when(mockStart.getLng()).thenReturn(20.0);
        when(mockVector.getAngle()).thenReturn(0.0);

        // Assert (No exception expected)
        LngLatDTO result = service.nextPosition(mockVector);
        assertNotNull(result, "0.0 degrees should be a valid angle.");
    }
}
