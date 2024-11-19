package com.example.intershop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProcessPurchaseActivity extends AppCompatActivity {
    private TextView textViewOrderSummary;
    private Button buttonConfirmPurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_purchase);

        textViewOrderSummary = findViewById(R.id.textViewOrderSummary);
        buttonConfirmPurchase = findViewById(R.id.buttonConfirmPurchase);

        // Aquí deberías mostrar un resumen de la orden
        textViewOrderSummary.setText("Resumen de la orden: ...");

        buttonConfirmPurchase.setOnClickListener(v -> {
            // Aquí deberías procesar la compra y luego ir a la pasarela de pago
            startActivity(new Intent(ProcessPurchaseActivity.this, MyPaymentActivity.class));
        });
    }
}
