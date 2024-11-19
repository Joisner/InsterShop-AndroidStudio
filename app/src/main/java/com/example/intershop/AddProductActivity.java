package com.example.intershop;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddProductActivity extends AppCompatActivity {
    private EditText editTextProductName;
    private EditText editTextProductPrice;
    private EditText editTextProductDescription;
    private Button buttonAddProduct;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        databaseHelper = new DatabaseHelper(this);

        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);

        buttonAddProduct.setOnClickListener(v -> {
            String name = editTextProductName.getText().toString();
            String priceText = editTextProductPrice.getText().toString();
            Double price = null;

            if (!priceText.isEmpty()) {
                price = Double.parseDouble(priceText);
            }
            String description = editTextProductDescription.getText().toString();

            if (!name.isEmpty() && price != null && !description.isEmpty()) {
                long productId = databaseHelper.addProduct(name, price, description, "");
                if (productId != -1) {
                    Toast.makeText(AddProductActivity.this, "Producto añadido con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddProductActivity.this, "Error al añadir el producto", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddProductActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
