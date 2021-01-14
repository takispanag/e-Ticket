package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.eticket.Model.Route;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentActivity extends AppCompatActivity {
    private static final String BACKEND_URL = "https://eticket.pythonanywhere.com/";
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;
    final int TIMI_EISITIRIOU = 10;
    String katigoriaEkptwsis;
    int arithmosEisitirwnMeEkptwsi;
    double telikoPoso = 0;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Todo: dimiourgia profil activity
        //Todo: dimiourgia dio koympiwn meta to signup/signin sto main activity gia na klisei thesi i na dei tis theseis poy exei kleisei idi (profile)
        //Todo: vres design gia tin parapanw ilopoiisi
        //Todo: Merge to checkout activity sto payment activity
        //Todo: multiple languages
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getSupportActionBar().hide();

        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
        cardInputWidget.setPostalCodeEnabled(false);
        List<Integer> eisitiriaList = new ArrayList<>();
        eisitiriaList.add(0);
        int eisitiria = getIntent().getIntExtra("size", 0);

        Spinner spinnerKatigoriaEkptwsis = findViewById(R.id.spinnerKatigoriaEkptwsis);
        Spinner spinnerAirthmosEisitiriwn = findViewById(R.id.spinnerArithmosEisitiriwn);

        TextView telikoPosoTextView = (TextView) findViewById(R.id.telikoPoso);
        telikoPosoTextView.setText("Συνολικό ποσό: " + TIMI_EISITIRIOU * eisitiria);

        for (int i = 0; i < eisitiria; i++) {
            eisitiriaList.add(i + 1);
        }
        spinnerKatigoriaEkptwsis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                katigoriaEkptwsis = spinnerKatigoriaEkptwsis.getSelectedItem().toString();
                if (!katigoriaEkptwsis.equals("Χωρίς Έκπτωση")) {
                    ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, eisitiriaList);
                    spinnerAirthmosEisitiriwn.setAdapter(adapter);
                } else {
                    ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, eisitiriaList.get(0));
                    spinnerAirthmosEisitiriwn.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Do nothing
            }
        });
        spinnerAirthmosEisitiriwn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                telikoPoso = TIMI_EISITIRIOU * eisitiria;
                arithmosEisitirwnMeEkptwsi = Integer.parseInt(spinnerAirthmosEisitiriwn.getSelectedItem().toString());
                if (katigoriaEkptwsis.equals("Φοιτητικό -50%")) {
                    telikoPoso = telikoPoso - arithmosEisitirwnMeEkptwsi * (TIMI_EISITIRIOU * 0.5);
                    telikoPosoTextView.setText("Συνολικό ποσό: " + telikoPoso);
                } else if (katigoriaEkptwsis.equals("Φοιτητικό -25%") || katigoriaEkptwsis.equals("Πολύτεκνο -25%") || katigoriaEkptwsis.equals("Στρατιωτικό -25%")) {
                    telikoPoso = telikoPoso - arithmosEisitirwnMeEkptwsi * (TIMI_EISITIRIOU * 0.25);
                    telikoPosoTextView.setText("Συνολικό ποσό: " + telikoPoso);
                }

                // Configure the SDK with your Stripe publishable key so it can make requests to Stripe
                stripe = new Stripe(

                        getApplicationContext(),

                        Objects.requireNonNull("pk_test_51I5wGLEiMZrqQBFptQbRv7Q54oNZWmdfFFSA2LsqAtck8S7lJxN3n1Ia1I8Rn75u5DYtw7djVoiyOMtt0Q4GxsHz00D2cs3u3p")

                );
                startCheckout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                telikoPoso = TIMI_EISITIRIOU * eisitiria;
                stripe = new Stripe(

                        getApplicationContext(),

                        Objects.requireNonNull("pk_test_51I5wGLEiMZrqQBFptQbRv7Q54oNZWmdfFFSA2LsqAtck8S7lJxN3n1Ia1I8Rn75u5DYtw7djVoiyOMtt0Q4GxsHz00D2cs3u3p")

                );
                startCheckout();
            }
        });

    }

    private void startCheckout() {

        // Create a PaymentIntent by calling the server's endpoint.

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        int amount = (int) Math.round(telikoPoso * 100);
        Map<String, Object> payMap = new HashMap<>();
        payMap.put("currency", "eur");
        payMap.put("amount", amount);
        String json = new Gson().toJson(payMap);

        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder()

                .url(BACKEND_URL + "create-payment-intent")

                .post(body)

                .build();

        httpClient.newCall(request).enqueue(new PaymentActivity.PayCallback(this));

        // Hook up the pay button to the card widget and stripe instance

        Button payButton = findViewById(R.id.payButton);

        payButton.setOnClickListener((View view) -> {
           dialog = ProgressDialog.show(PaymentActivity.this, "",
                    "Loading", true);

            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);

            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

            if (params != null) {

                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams

                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);

                stripe.confirmPayment(this, confirmParams);

            }

        });

    }

    private void displayAlert(@NonNull String title,

                              @Nullable String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)

                .setTitle(title)

                .setMessage(message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do work here
                Intent profile = new Intent(PaymentActivity.this, ProfileActivity.class);
                Route myRoute =  (Route) getIntent().getSerializableExtra("route");
                profile.putExtra("route",myRoute);
                startActivity(profile);
            }
        });
        builder.create().show();

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment

        stripe.onPaymentResult(requestCode, data, new PaymentActivity.PaymentResultCallback(this));
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {

        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, String>>() {
        }.getType();

        Map<String, String> responseMap = gson.fromJson(

                Objects.requireNonNull(response.body()).string(),

                type

        );

        paymentIntentClientSecret = responseMap.get("clientSecret");
    }

    private static final class PayCallback implements Callback {

        @NonNull
        private final WeakReference<PaymentActivity> activityRef;

        PayCallback(@NonNull PaymentActivity activity) {

            activityRef = new WeakReference<>(activity);

        }

        @Override

        public void onFailure(@NonNull Call call, @NonNull IOException e) {

            final PaymentActivity activity = activityRef.get();

            if (activity == null) {

                return;

            }

            activity.runOnUiThread(() ->

                    Toast.makeText(

                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG

                    ).show()

            );

        }

        @Override

        public void onResponse(@NonNull Call call, @NonNull final Response response)

                throws IOException {

            final PaymentActivity activity = activityRef.get();

            if (activity == null) {

                return;

            }

            if (!response.isSuccessful()) {

                activity.runOnUiThread(() ->

                        Toast.makeText(

                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG

                        ).show()

                );

            } else {

                activity.onPaymentSuccess(response);

            }

        }

    }

    private final class PaymentResultCallback

            implements ApiResultCallback<PaymentIntentResult> {

        @NonNull
        private final WeakReference<PaymentActivity> activityRef;

        PaymentResultCallback(@NonNull PaymentActivity activity) {

            activityRef = new WeakReference<>(activity);

        }

        @Override

        public void onSuccess(@NonNull PaymentIntentResult result) {

            final PaymentActivity activity = activityRef.get();

            if (activity == null) {

                return;

            }

            PaymentIntent paymentIntent = result.getIntent();

            PaymentIntent.Status status = paymentIntent.getStatus();

            if (status == PaymentIntent.Status.Succeeded) {

                // Payment completed successfully

                Route myRoute =  (Route) getIntent().getSerializableExtra("route");
                dialog.dismiss();
                activity.displayAlert(

                        "Πληρωμή ολοκληρώθηκε!", "Η κράτηση σας για το δρομολόγιο\n" + myRoute.toString() + " δημιουργήθηκε επιτυχώς."

                );

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {

                // Payment failed – allow retrying using a different payment method

                activity.displayAlert(

                        "Payment failed",

                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()

                );

            }

        }

        @Override

        public void onError(@NonNull Exception e) {

            final PaymentActivity activity = activityRef.get();

            if (activity == null) {

                return;

            }

            // Payment request failed – allow retrying using the same payment method

            activity.displayAlert("Error", e.toString());

        }
    }
}