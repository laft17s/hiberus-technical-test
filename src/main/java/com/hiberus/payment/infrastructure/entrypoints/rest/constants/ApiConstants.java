package com.hiberus.payment.infrastructure.entrypoints.rest.constants;

public final class ApiConstants {
    
    private ApiConstants() {
        // Restrict instantiation
    }
    
    // Status Codes
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_ERROR = -1;
    
    // Status Strings
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";
    
    // Messages (English)
    public static final String MSG_PAYMENT_ORDER_CREATED = "Payment order initiated successfully";
    public static final String MSG_PAYMENT_ORDER_RETRIEVED = "Payment order retrieved successfully";
    public static final String MSG_PAYMENT_ORDER_LIST_RETRIEVED = "Payment order list retrieved successfully";
    public static final String MSG_PAYMENT_ORDER_STATUS_RETRIEVED = "Payment order status retrieved successfully";
    
    public static final String MSG_NOT_FOUND = "Resource not found";
    public static final String MSG_BAD_REQUEST = "Invalid request payload or parameters";
    public static final String MSG_INTERNAL_ERROR = "An unexpected internal error occurred";
}
