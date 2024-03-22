package com.example.navigationbarapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.telecom.Call;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Response;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.stripe.android.paymentsheet.addresselement.AddressDetails;
import com.stripe.android.paymentsheet.addresselement.AddressLauncher;
import com.stripe.android.paymentsheet.addresselement.AddressLauncherResult;

import okhttp3.*;
import okhttp3.Callback;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private static final String BACKEND_URL = "http://10.0.2.2:4242";

    private String paymentIntentClientSecret;
    private PaymentSheet paymentSheet;

    private Button payButton;

    private AddressLauncher addressLauncher;

    private AddressDetails shippingDetails;

    private Button addressButton;

    private final AddressLauncher.Configuration configuration =
            new AddressLauncher.Configuration.Builder()
                    .additionalFields(
                            new AddressLauncher.AdditionalFieldsConfiguration(
                                AddressLauncher.AdditionalFieldsConfiguration.FieldConfiguration.REQUIRED
                            )
                    )
                    .allowedCountries(new HashSet<>(Arrays.asList("US", "CA", "GB")))
                    .title("Shipping Address")
                    .googlePlacesApiKey("(optional) YOUR KEY HERE")
                    .build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checkout);

        Intent intent = getIntent();
        double totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0);
        String userName = intent.getStringExtra("USER_NAME");

        // Hook up the pay button
        payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(this::onPayClicked);
        payButton.setEnabled(false);

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

          // Hook up the address button
          addressButton = findViewById(R.id.address_button);
          addressButton.setOnClickListener(this::onAddressClicked);
          addressLauncher = new AddressLauncher(this, this::onAddressLauncherResult);

        fetchPaymentIntent();
    }

    private void showAlert(String title, @Nullable String message) {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .create();
            dialog.show();
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    private void fetchPaymentIntent() {
        final String shoppingCartContent = "{\"items\": [ {\"id\":\"xl-tshirt\"}]}";

        final RequestBody requestBody = RequestBody.create(
            shoppingCartContent,
            MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
            .url(BACKEND_URL + "/create-payment-intent")
            .post(requestBody)
            .build();

        new OkHttpClient()
            .newCall(request)
            .enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        showAlert(
                                "Failed to load page",
                                "Error: " + response.toString()
                        );
                    } else {
                        final JSONObject responseJson = parseResponse(response.body());
                        paymentIntentClientSecret = responseJson.optString("clientSecret");
                        runOnUiThread(() -> payButton.setEnabled(true));
                        Log.i(TAG, "Retrieved PaymentIntent");
                    }
                }

                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                    showAlert("Failed to load data", "Error: " + e.toString());
                }
            });
    }

    private JSONObject parseResponse(ResponseBody responseBody) {
        if (responseBody != null) {
            try {
                return new JSONObject(responseBody.string());
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error parsing response", e);
            }
        }

        return new JSONObject();
    }

    private void onPayClicked(View view) {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("Example, Inc.");

        // Present Payment Sheet
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
    }

    private void onAddressClicked(View view) {
        String publishableKey="pk_test_51H2N69JagFRBbT76zkxxidhhJzGQHmtWW0b6D0i0F680UGszBCHuod7DnmS1EZyYUXUV6ZVbCoTWEHiGEDjiIr1P00DjBSKDjB";
        AddressLauncher.Configuration addressConfiguration= new AddressLauncher.Configuration.Builder().build();
        addressLauncher.present(
        publishableKey,
        addressConfiguration
      );
    }

    private void onPaymentSheetResult(
        final PaymentSheetResult paymentSheetResult
    ) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            showToast("Payment complete!");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.i(TAG, "Payment canceled!");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Throwable error = ((PaymentSheetResult.Failed) paymentSheetResult).getError();
            showAlert("Payment failed", error.getLocalizedMessage());
        }
    }

    private void onAddressLauncherResult(AddressLauncherResult result) {
        // TODO: Handle result and update your UI
        if (result instanceof AddressLauncherResult.Succeeded) {
            shippingDetails = ((AddressLauncherResult.Succeeded) result).getAddress();
        } else if (result instanceof AddressLauncherResult.Canceled) {
            // TODO: Handle cancel
        }
    }
}