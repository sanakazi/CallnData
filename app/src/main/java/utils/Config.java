package utils;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

public class Config {

    // PayPal app configuration
    public static final String PAYPAL_CLIENT_ID = "Af9nlNvqMmhz-NSQ8RstBz3uGTuyzsUD25VLVYVrhvbdMRFIOZJFOV0xtiP6UMnbraD98nayQXbTaCxh";
    public static final String PAYPAL_CLIENT_SECRET = "";

   // public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
     public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
    public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
    public static final String DEFAULT_CURRENCY = "USD";

    // PayPal server urls
    public static final String URL_PRODUCTS = "http://192.168.0.103/PayPalServer/v1/products";
    public static final String URL_VERIFY_PAYMENT = "http://192.168.0.103/PayPalServer/v1/verifyPayment";

}