package com.example.intershop;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCartActivity extends AppCompatActivity {
    private ListView listViewCart;
    private TextView textViewTotal;
    private Button buttonCheckout;
    private List<Map<String, String>> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        listViewCart = findViewById(R.id.listViewCart);
        textViewTotal = findViewById(R.id.textViewTotal);
        buttonCheckout = findViewById(R.id.buttonCheckout);

        cartItems = new ArrayList<>();

        // Simular items en el carrito
        addCartItem("Buso Deportivo", "49.99", "1");
        addCartItem("Pantalón Casual", "39.99", "2");

        SimpleAdapter adapter = new SimpleAdapter(this, cartItems,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "subtotal"},
                new int[]{android.R.id.text1, android.R.id.text2});

        listViewCart.setAdapter(adapter);

        updateTotal();

        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShoppingCartActivity.this, "Procesando compra...", Toast.LENGTH_SHORT).show();
                // Aquí iría la lógica para procesar la compra
            }
        });
    }

    private void addCartItem(String name, String price, String quantity) {
        Map<String, String> item = new HashMap<>();
        item.put("name", name);
        double subtotal = Double.parseDouble(price) * Integer.parseInt(quantity);
        item.put("subtotal", String.format("$%.2f x %s", subtotal, quantity));
        cartItems.add(item);
    }

    private void updateTotal() {
        double total = 0;
        for (Map<String, String> item : cartItems) {
            String subtotalStr = item.get("subtotal").split(" x ")[0].substring(1);
            total += Double.parseDouble(subtotalStr);
        }
        textViewTotal.setText(String.format("Total: $%.2f", total));
    }
}