package com.example.intershop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intershop.adapters.ProductAdapter;
import com.example.intershop.models.Product;

import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private ListView listViewProducts;
    private Button buttonAddProduct;
    private Button buttonEditProduct;
    private Button buttonDeleteProduct;
    private Button buttonShowLocation;
    private Button buttonProcessPurchase;
    private Button buttonPaymentGateway;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        databaseHelper = new DatabaseHelper(this);

        listViewProducts = findViewById(R.id.listViewProducts);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonEditProduct = findViewById(R.id.buttonEditProduct);
        buttonDeleteProduct = findViewById(R.id.buttonDeleteProduct);
        buttonShowLocation = findViewById(R.id.buttonShowLocation);
        buttonProcessPurchase = findViewById(R.id.buttonProcessPurchase);
        buttonPaymentGateway = findViewById(R.id.buttonPaymentGateway);

        loadProducts();

        buttonAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(ProductListActivity.this, AddProductActivity.class));
        });

        buttonEditProduct.setOnClickListener(v -> showEditProductDialog());

        buttonDeleteProduct.setOnClickListener(v -> showDeleteProductDialog());

        buttonShowLocation.setOnClickListener(v -> {
            startActivity(new Intent(ProductListActivity.this, MapsActivity.class));
        });

        buttonProcessPurchase.setOnClickListener(v -> {
            startActivity(new Intent(ProductListActivity.this, ProcessPurchaseActivity.class));
        });

        buttonPaymentGateway.setOnClickListener(v -> {
            startActivity(new Intent(ProductListActivity.this, MyPaymentActivity.class));
        });
    }

    private void loadProducts() {
        List<Product> products = databaseHelper.getAllProducts();
        ProductAdapter adapter = new ProductAdapter(this, products);
        listViewProducts.setAdapter(adapter);
    }

    private void showEditProductDialog() {
        List<Product> products = databaseHelper.getAllProducts();
        String[] productNames = new String[products.size()];
        for (int i = 0; i < products.size(); i++) {
            productNames[i] = products.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Seleccione un producto para editar")
                .setItems(productNames, (dialog, which) -> {
                    Product selectedProduct = products.get(which);
                    Intent intent = new Intent(ProductListActivity.this, EditProductActivity.class);
                    intent.putExtra("PRODUCT_ID", selectedProduct.getId());
                    startActivity(intent);
                })
                .show();
    }

    private void showDeleteProductDialog() {
        List<Product> products = databaseHelper.getAllProducts();
        String[] productNames = new String[products.size()];
        for (int i = 0; i < products.size(); i++) {
            productNames[i] = products.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Seleccione un producto para eliminar")
                .setItems(productNames, (dialog, which) -> {
                    Product selectedProduct = products.get(which);
                    confirmDelete(selectedProduct);
                })
                .show();
    }

    private void confirmDelete(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de que desea eliminar " + product.getName() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    databaseHelper.deleteProduct(product.getId());
                    loadProducts();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
