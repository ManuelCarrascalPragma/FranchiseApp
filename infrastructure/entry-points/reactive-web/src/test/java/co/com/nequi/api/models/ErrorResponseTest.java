package co.com.nequi.api.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void constructor_ShouldCreateInstance() {
        ErrorResponse errorResponse = new ErrorResponse("Test message", 400, "Bad Request");

        assertNotNull(errorResponse);
        assertEquals("Test message", errorResponse.getMessage());
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Bad Request", errorResponse.getError());
    }

    @Test
    void setters_ShouldUpdateValues() {
        ErrorResponse errorResponse = new ErrorResponse("Initial", 400, "Bad Request");

        errorResponse.setMessage("Updated message");
        errorResponse.setStatus(500);
        errorResponse.setError("Internal Server Error");

        assertEquals("Updated message", errorResponse.getMessage());
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        ErrorResponse error1 = new ErrorResponse("Test", 400, "Bad Request");
        ErrorResponse error2 = new ErrorResponse("Test", 400, "Bad Request");
        ErrorResponse error3 = new ErrorResponse("Different", 500, "Error");

        assertEquals(error1, error2);
        assertNotEquals(error1, error3);
    }

    @Test
    void hashCode_ShouldBeConsistent() {
        ErrorResponse error1 = new ErrorResponse("Test", 400, "Bad Request");
        ErrorResponse error2 = new ErrorResponse("Test", 400, "Bad Request");

        assertEquals(error1.hashCode(), error2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        ErrorResponse errorResponse = new ErrorResponse("Test message", 400, "Bad Request");
        String toString = errorResponse.toString();

        assertTrue(toString.contains("Test message"));
        assertTrue(toString.contains("400"));
        assertTrue(toString.contains("Bad Request"));
    }
}
