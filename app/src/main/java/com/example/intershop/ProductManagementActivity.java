package com.example.intershop;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intershop.adapters.ProductAdapter;
import com.example.intershop.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ProductManagementActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        dbHelper = new DatabaseHelper(this);
        new LoadProductsTask().execute();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fabAddProduct = findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(v -> showAddProductDialog());
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextProductName);
        final EditText editTextPrice = dialogView.findViewById(R.id.editTextProductPrice);
        final EditText editTextDescription = dialogView.findViewById(R.id.editTextProductDescription);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = editTextName.getText().toString();
            double price = Double.parseDouble(editTextPrice.getText().toString());
            String description = editTextDescription.getText().toString();

            new AddProductTask().execute(new Product(0, name, price, description, ""));
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private class LoadProductsTask extends AsyncTask<Void, Void, List<Product>> {
        @Override
        protected List<Product> doInBackground(Void... voids) {
            return dbHelper.getAllProducts();
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            productAdapter = new ProductAdapter(ProductManagementActivity.this, products);
            RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
            recyclerView.setAdapter(productAdapter);
        }
    }

    private class AddProductTask extends AsyncTask<Product, Void, Long> {
        @Override
        protected Long doInBackground(Product... products) {
            Product product = products[0];
            return dbHelper.addProduct(product.getName(), product.getPrice(), product.getDescription(), product.getImageUrl());
        }

        @Override
        protected void onPostExecute(Long productId) {
            if (productId != -1) {
                new LoadProductsTask().execute();
                Toast.makeText(ProductManagementActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProductManagementActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void editProduct(Product product) {
        // Implementar lógica para editar producto
        new EditProductTask().execute(product);
    }

    public void deleteProduct(Product product) {
        // Implementar lógica para eliminar producto
        new DeleteProductTask().execute(product);
    }

    private class EditProductTask extends AsyncTask<Product, Void, Integer> {
        @Override
        protected Integer doInBackground(Product... products) {
            return dbHelper.updateProduct(products[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result > 0) {
                new LoadProductsTask().execute();
                Toast.makeText(ProductManagementActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProductManagementActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DeleteProductTask extends AsyncTask<Product, Void, Void> {
        @Override
        protected Void doInBackground(Product... products) {
            dbHelper.deleteProduct(products[0].getId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new LoadProductsTask().execute();
            Toast.makeText(ProductManagementActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
        }
    }
}