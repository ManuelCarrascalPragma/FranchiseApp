package co.com.nequi.usecase.constants;

public final class ErrorMessages {

    private ErrorMessages() {
    }

    public static final String FRANCHISE_NAME_REQUIRED = "Franchise name is required";
    public static final String FRANCHISE_NAME_ALREADY_EXISTS = "A franchise with this name already exists: %s";
    public static final String FRANCHISE_NOT_FOUND = "Franchise not found with id: %s";

    public static final String BRANCH_NAME_REQUIRED = "Branch name is required";
    public static final String BRANCH_NAME_ALREADY_EXISTS = "Branch '%s' already exists in this franchise";
    public static final String BRANCH_NOT_FOUND = "Branch not found with id: %s";

    public static final String PRODUCT_NAME_REQUIRED = "Product name is required";
    public static final String PRODUCT_NAME_ALREADY_EXISTS = "Product '%s' already exists in this branch";
    public static final String PRODUCT_NOT_FOUND = "Product not found with id: %s";
    public static final String PRODUCT_STOCK_INVALID = "Stock must be a number greater than or equal to zero";
    public static final String PRODUCT_DOES_NOT_BELONG_TO_BRANCH = "Product does not belong to the specified branch";
}
