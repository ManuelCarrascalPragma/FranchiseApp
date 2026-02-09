package co.com.nequi.usecase.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorMessagesTest {

    @Test
    void franchiseMessages_ShouldNotBeNull() {
        assertNotNull(ErrorMessages.FRANCHISE_NAME_REQUIRED);
        assertNotNull(ErrorMessages.FRANCHISE_NAME_ALREADY_EXISTS);
        assertNotNull(ErrorMessages.FRANCHISE_NOT_FOUND);
    }

    @Test
    void branchMessages_ShouldNotBeNull() {
        assertNotNull(ErrorMessages.BRANCH_NAME_REQUIRED);
        assertNotNull(ErrorMessages.BRANCH_NAME_ALREADY_EXISTS);
        assertNotNull(ErrorMessages.BRANCH_NOT_FOUND);
    }

    @Test
    void productMessages_ShouldNotBeNull() {
        assertNotNull(ErrorMessages.PRODUCT_NAME_REQUIRED);
        assertNotNull(ErrorMessages.PRODUCT_NAME_ALREADY_EXISTS);
        assertNotNull(ErrorMessages.PRODUCT_NOT_FOUND);
        assertNotNull(ErrorMessages.PRODUCT_STOCK_INVALID);
        assertNotNull(ErrorMessages.PRODUCT_DOES_NOT_BELONG_TO_BRANCH);
    }

    @Test
    void messages_ShouldBeInEnglish() {
        assertTrue(ErrorMessages.FRANCHISE_NAME_REQUIRED.matches("^[a-zA-Z\\s]+$"));
        assertTrue(ErrorMessages.PRODUCT_STOCK_INVALID.contains("Stock"));
    }

    @Test
    void formattedMessages_ShouldContainPlaceholder() {
        assertTrue(ErrorMessages.FRANCHISE_NAME_ALREADY_EXISTS.contains("%s"));
        assertTrue(ErrorMessages.FRANCHISE_NOT_FOUND.contains("%s"));
        assertTrue(ErrorMessages.BRANCH_NOT_FOUND.contains("%s"));
        assertTrue(ErrorMessages.PRODUCT_NOT_FOUND.contains("%s"));
    }
}
