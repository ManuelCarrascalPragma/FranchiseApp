package co.com.nequi.api.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiConstantsTest {

    @Test
    void parameterNames_ShouldNotBeEmpty() {
        assertFalse(ApiConstants.ID_PARAM_NAME.isEmpty());
        assertFalse(ApiConstants.FRANCHISE_ID_PARAM_NAME.isEmpty());
        assertFalse(ApiConstants.BRANCH_ID_PARAM_NAME.isEmpty());
        assertFalse(ApiConstants.PRODUCT_ID_PARAM_NAME.isEmpty());
    }
}
