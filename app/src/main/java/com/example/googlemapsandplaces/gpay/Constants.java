package com.example.googlemapsandplaces.gpay;

import com.google.android.gms.wallet.WalletConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This file contains several constants you must edit before proceeding.
 * Please take a look at PaymentsUtil.java to see where the constants are used and to potentially
 * remove ones not relevant to your integration.
 *
 * <p>Required changes:
 * <ol>
 * <li> Update SUPPORTED_NETWORKS and SUPPORTED_METHODS if required (consult your processor if
 *      unsure)
 * <li> Update CURRENCY_CODE to the currency you use.
 * <li> Update SHIPPING_SUPPORTED_COUNTRIES to list the countries where you currently ship. If this
 *      is not applicable to your app, remove the relevant bits from PaymentsUtil.java.
 * <li> If you're integrating with your {@code PAYMENT_GATEWAY}, update
 *      PAYMENT_GATEWAY_TOKENIZATION_NAME and PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS per the
 *      instructions they provided. You don't need to update DIRECT_TOKENIZATION_PUBLIC_KEY.
 * <li> If you're using {@code DIRECT} integration, please edit protocol version and public key as
 *      per the instructions.
 */
public class Constants {

    /**
     * The Parking spot price in cents
     */
    public static final double PARKING_COST_CENTS = 1000;

    /**
     * Changing this to ENVIRONMENT_PRODUCTION will make the API return chargeable card information.
     * Please refer to the documentation to read about the required steps needed to enable
     * ENVIRONMENT_PRODUCTION.
     *
     * @value #PAYMENTS_ENVIRONMENT
     */
    public static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST;

    /**
     * Get this from google
     */
    public static final String GOOGLE_PAY_MERCHANT_NAME = "UrbanPark - One-Step Parking App";

    /**
     * The allowed networks to be requested from the API. If the user has cards from networks not
     * specified here in their account, these will not be offered for them to choose in the popup.
     *
     * @value #SUPPORTED_NETWORKS
     */
    public static final List<String> SUPPORTED_NETWORKS = Arrays.asList(
            "AMEX",
            "DISCOVER",
            "JCB",
            "MASTERCARD",
            "VISA");

    /**
     * The Google Pay API may return cards on file on Google.com (PAN_ONLY) and/or a device token on
     * an Android device authenticated with a 3-D Secure cryptogram (CRYPTOGRAM_3DS).
     *
     * @value #SUPPORTED_METHODS
     */
    public static final List<String> SUPPORTED_METHODS = Arrays.asList(
            "PAN_ONLY",
            "CRYPTOGRAM_3DS");

    /**
     * Required by the API, but not visible to the user.
     *
     * @value #COUNTRY_CODE Your local country
     */
    public static final String COUNTRY_CODE = "IT";

    /**
     * Required by the API, but not visible to the user.
     *
     * @value #CURRENCY_CODE Your local currency
     */
    public static final String CURRENCY_CODE = "EUR";

    /**
     * Supported countries for shipping (use ISO 3166-1 alpha-2 country codes). Relevant only when
     * requesting a shipping address.
     *
     * @value #SHIPPING_SUPPORTED_COUNTRIES
     */
    //public static final List<String> SHIPPING_SUPPORTED_COUNTRIES = Arrays.asList("US", "GB", "IT");

    /**
     * The name of your payment processor/gateway. Please refer to their documentation for more
     * information.
     *
     * @value #PAYMENT_GATEWAY_TOKENIZATION_NAME
     */
    public static final String PAYMENT_GATEWAY_TOKENIZATION_NAME = "stripe";

    // Not needed for stripe
    public static final String PAYMENT_GATEWAY_TOKENIZATION_MERCHANT_ID = "exampleGatewayMerchantId";

    // These 2 are for the TESTING. The live ones will be used when ready to deploy and receive actual payments.
    public static final String PAYMENT_GATEWAY_TOKENIZATION_PUBLISHABLE_KEY = "${PUBLISHABLE_KEY}";
    public static final String PAYMENT_GATEWAY_TOKENIZATION_SECRET_KEY = "${SECRET_KEY}";

    public static final String PAYMENT_GATEWAY_TOKENIZATION_VERSION = "2018-10-31";

    /**
     * Custom parameters required by the processor/gateway.
     * In many cases, your processor / gateway will only require a gatewayMerchantId.
     * Please refer to your processor's documentation for more information. The number of parameters
     * required and their names vary depending on the processor.
     *
     * @value #PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS
     */
    public static final HashMap<String, String> PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS =
            new HashMap<String, String>() {{
                put("gateway", PAYMENT_GATEWAY_TOKENIZATION_NAME);
                //put("gatewayMerchantId", PAYMENT_GATEWAY_TOKENIZATION_MERCHANT_ID);
                put("stripe:version", PAYMENT_GATEWAY_TOKENIZATION_VERSION);
                put("stripe:publishableKey", PAYMENT_GATEWAY_TOKENIZATION_PUBLISHABLE_KEY);
                // Your processor may require additional parameters.
            }};
}