package com.example.googlemapsandplaces.gpay;

import static com.example.googlemapsandplaces.GeneralUtils.EMAIL;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.googlemapsandplaces.Booking;
import com.example.googlemapsandplaces.Parking;
import com.example.googlemapsandplaces.R;
import com.example.googlemapsandplaces.databinding.ActivityGpayBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.button.ButtonOptions;
import com.google.android.gms.wallet.button.PayButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Checkout implementation for the app
 */
public class GPayActivity extends AppCompatActivity {

    private static final String TAG = ".GPayActivity";

    private Parking parking;
    private CheckoutViewModel model;

    private PayButton googlePayButton;

    // Handle potential conflict from calling loadPaymentData.
    ActivityResultLauncher<IntentSenderRequest> resolvePaymentForResult = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                switch (result.getResultCode()) {
                    case Activity.RESULT_OK:
                        Intent resultData = result.getData();
                        if (resultData != null) {
                            PaymentData paymentData = PaymentData.getFromIntent(result.getData());
                            if (paymentData != null) {
                                handlePaymentSuccess(paymentData);
                            }
                        }
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this, "The user cancelled the payment attempt", Toast.LENGTH_SHORT).show();
                        break;
                }
            });

    /**
     * Initialize the Google Pay API on creation of the activity
     *
     * @see Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUi();

        // Check Google Pay availability
        model = new ViewModelProvider(this).get(CheckoutViewModel.class);
        model.canUseGooglePay.observe(this, this::setGooglePayAvailable);
    }

    private void initializeUi() {

        // Use view binding to access the UI elements
        ActivityGpayBinding layoutBinding = ActivityGpayBinding.inflate(getLayoutInflater());
        setContentView(layoutBinding.getRoot());

        parking = (Parking) getIntent().getSerializableExtra("parkingObject");
        layoutBinding.parkingLayout.placeName.setText(parking.getPlaceName());
        layoutBinding.parkingLayout.address.setText(parking.getAddress());
        layoutBinding.parkingLayout.totalPlaces.setText(parking.getTotalPlaces());
        layoutBinding.parkingLayout.remPlaces.setText(parking.getRemPlaces());
        layoutBinding.parkingLayout.price.setText(parking.getPrice());

        layoutBinding.parkingLayout.btnLayout.setVisibility(View.GONE);

        // Call this to get the current location and the destination
        parking.getDistanceAndTime(this, new Parking.DistanceCallback() {
            @Override
            public void onDistanceReceived(String distance, String time) {
                // Update the UI with the calculated distance and time.
                layoutBinding.parkingLayout.distance.setText(distance);
                layoutBinding.parkingLayout.time.setText(time);
            }
        });

        layoutBinding.parkingLayout.distance.setText(parking.getDistance());
        layoutBinding.parkingLayout.time.setText(parking.getTime());
        layoutBinding.parkingLayout.startDateTime.setText(parking.getStartDateTime());
        layoutBinding.parkingLayout.endDateTime.setText(parking.getEndDateTime());

        // The Google Pay button is a layout file – take the root view
        googlePayButton = layoutBinding.googlePayButton;
        try {
            googlePayButton.initialize(
                    ButtonOptions.newBuilder()
                            .setAllowedPaymentMethods(PaymentsUtil.getAllowedPaymentMethods().toString()).build()
            );
            googlePayButton.setOnClickListener(this::requestPayment);
        } catch (JSONException e) {
            // Keep Google Pay button hidden (consider logging this to your app analytics service)
        }
    }

    /**
     * If isReadyToPay returned {@code true}, show the button and hide the "checking" text.
     * Otherwise, notify the user that Google Pay is not available. Please adjust to fit in with
     * your current user flow. You are not required to explicitly let the user know if isReadyToPay
     * returns {@code false}.
     *
     * @param available isReadyToPay API response.
     */
    private void setGooglePayAvailable(boolean available) {
        if (available) {
            googlePayButton.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, R.string.google_pay_status_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    public void requestPayment(View view) {

        // Disables the button to prevent multiple clicks.
        googlePayButton.setClickable(false);

        /**
         * The cost of the service/item should be the total (including taxes, shipping, etc) shown to the user
         */
        final Task<PaymentData> task = model.getLoadPaymentDataTask(Constants.PARKING_COST_CENTS);

        task.addOnCompleteListener(completedTask -> {
            if (completedTask.isSuccessful()) {
                handlePaymentSuccess(completedTask.getResult());
            } else {
                Exception exception = completedTask.getException();
                if (exception instanceof ResolvableApiException) {
                    PendingIntent resolution = ((ResolvableApiException) exception).getResolution();
                    resolvePaymentForResult.launch(new IntentSenderRequest.Builder(resolution).build());

                } else if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    handleError(apiException.getStatusCode(), apiException.getMessage());

                } else {
                    handleError(CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                            " exception when trying to deliver the task result to an activity!");
                }
            }

            // Re-enables the Google Pay payment button.
            googlePayButton.setClickable(true);
        });
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see <a href="https://developers.google.com/pay/api/android/reference/
     * object#PaymentData">PaymentData</a>
     */
    private void handlePaymentSuccess(PaymentData paymentData) {
        final String paymentInfo = paymentData.toJson();

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");
            Toast.makeText(
                    this, getString(R.string.payments_show_name, billingName),
                    Toast.LENGTH_LONG).show();

            // Logging token string.
            Log.d("Google Pay token", paymentMethodData.toString());
            Log.d("Google Pay token", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token"));

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("booking");
            String address = parking.getAddress();
            String key = dbRef.push().getKey(); // Generates a unique key

            Booking booking = new Booking(key, EMAIL, address);
            dbRef.child(key).setValue(booking);

            // TODO: add the start date and time


            Intent intent = new Intent(this, SuccessActivity.class);
            startActivity(intent);

        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e);
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode holds the value of any constant from CommonStatusCode or one of the
     *                   WalletConstants.ERROR_CODE_* constants.
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/wallet/
     * WalletConstants#constant-summary">Wallet Constants Library</a>
     */
    private void handleError(int statusCode, @Nullable String message) {
        Log.e("loadPaymentData failed",
                String.format(Locale.getDefault(), "Error code: %d, Message: %s", statusCode, message));
    }
}