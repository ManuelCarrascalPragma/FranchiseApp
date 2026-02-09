package co.com.nequi.usecase.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationConstantsTest {

    @Test
    void minStockValue_ShouldBeZero() {
        assertEquals(0, ValidationConstants.MIN_STOCK_VALUE);
    }

    @Test
    void minStockValue_ShouldBeNonNegative() {
        assertTrue(ValidationConstants.MIN_STOCK_VALUE >= 0);
    }
}
