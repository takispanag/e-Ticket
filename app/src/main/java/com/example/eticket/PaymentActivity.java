package com.example.eticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


import com.stripe.android.view.CardInputWidget;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    final int TIMI_EISITIRIOU = 10;
    String katigoriaEkptwsis;
    int arithmosEisitirwnMeEkptwsi;
    double telikoPoso = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
        cardInputWidget.setPostalCodeEnabled(false);
        List<Integer> eisitiriaList = new ArrayList<>();
        eisitiriaList.add(0);
        int eisitiria = getIntent().getIntExtra("size",0);

        Spinner spinnerKatigoriaEkptwsis = findViewById(R.id.spinnerKatigoriaEkptwsis);
        Spinner spinnerAirthmosEisitiriwn = findViewById(R.id.spinnerArithmosEisitiriwn);

        TextView telikoPosoTextView = (TextView)findViewById(R.id.telikoPoso);
        telikoPosoTextView.setText("Συνολικό ποσό: " + TIMI_EISITIRIOU * eisitiria);

        for(int i=0; i < eisitiria ; i++){
            eisitiriaList.add(i + 1);
        }
        spinnerKatigoriaEkptwsis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                katigoriaEkptwsis = spinnerKatigoriaEkptwsis.getSelectedItem().toString();
                if(!katigoriaEkptwsis.equals("Χωρίς Έκπτωση")){
                    ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getBaseContext(),android.R.layout.simple_spinner_item, eisitiriaList);
                    spinnerAirthmosEisitiriwn.setAdapter(adapter);
                }
                else{
                    ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getBaseContext(),android.R.layout.simple_spinner_item, eisitiriaList.get(0));
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
                arithmosEisitirwnMeEkptwsi = Integer.parseInt(spinnerAirthmosEisitiriwn.getSelectedItem().toString());
                telikoPoso = TIMI_EISITIRIOU * eisitiria;
                if(katigoriaEkptwsis.equals("Φοιτητικό -50%")){
                    telikoPoso = telikoPoso - arithmosEisitirwnMeEkptwsi*(TIMI_EISITIRIOU*0.5);
                    telikoPosoTextView.setText("Συνολικό ποσό: " + telikoPoso);
                }
                else if(katigoriaEkptwsis.equals("Φοιτητικό -25%") || katigoriaEkptwsis.equals("Πολύτεκνο -25%") || katigoriaEkptwsis.equals("Στρατιωτικό -25%")){
                    telikoPoso = telikoPoso - arithmosEisitirwnMeEkptwsi * (TIMI_EISITIRIOU * 0.25);
                    telikoPosoTextView.setText("Συνολικό ποσό: " + telikoPoso);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }
}