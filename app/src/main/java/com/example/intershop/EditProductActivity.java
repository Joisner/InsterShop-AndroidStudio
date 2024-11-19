package com.example.intershop;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intershop.models.Product;

public class EditProductActivity extends AppCompatActivity {
    private EditText editTextProductName;
    private EditText editTextProductPrice;
    private EditText editTextProductDescription;
    private Button buttonUpdateProduct;
    private DatabaseHelper databaseHelper;
    private int productId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        databaseHelper = new DatabaseHelper(this);

        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        editTextProductDescription = findViewById(R.id.editTextProductDescription);
        buttonUpdateProduct = findViewById(R.id.buttonUpdateProduct);

        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            Toast.makeText(this, "Error: No se pudo cargar el producto", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProductDetails();

        buttonUpdateProduct.setOnClickListener(v -> updateProduct());
    }

    private void loadProductDetails() {
        Product product = databaseHelper.getProduct((long) productId);
        if (product != null) {
            editTextProductName.setText(product.getName());
            editTextProductPrice.setText(String.valueOf(product.getPrice()));
            editTextProductDescription.setText(product.getDescription());
        } else {
            Toast.makeText(this, "Error: No se pudo cargar los detalles del producto", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateProduct() {
        String name = editTextProductName.getText().toString();
        String priceText = editTextProductPrice.getText().toString();
        String description = editTextProductDescription.getText().toString();
        Double price = null;

        if (!priceText.isEmpty()) {
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (!name.isEmpty() && price != null && !description.isEmpty()) {
            Product updatedProduct = new Product(productId, name, price, description, "");
            long result = databaseHelper.updateProduct(updatedProduct);
            if (result > 0) {
                Toast.makeText(this, "Producto actualizado con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar el producto", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
}
