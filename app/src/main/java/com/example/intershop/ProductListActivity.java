package com.example.intershop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListActivity extends AppCompatActivity {
    private ListView listViewProducts;
    private List<Map<String, String>> productList;
    private Button buttonProcessPurchase;
    private Button buttonShowLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        listViewProducts = findViewById(R.id.listViewProducts);
        buttonProcessPurchase = findViewById(R.id.buttonProcessPurchase);
        buttonShowLocation = findViewById(R.id.buttonShowLocation);
        productList = new ArrayList<>();

        // Simular productos
        addProduct("Buso Deportivo", "49.99", "buso");
        addProduct("Pantalón Casual", "39.99", "pantalon");
        addProduct("Buso de Invierno", "59.99", "buso");
        addProduct("Pantalón Deportivo", "44.99", "pantalon");

        SimpleAdapter adapter = new SimpleAdapter(this, productList,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "price"},
                new int[]{android.R.id.text1, android.R.id.text2});

        listViewProducts.setAdapter(adapter);

        listViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ProductListActivity.this,
                        "Producto seleccionado: " + productList.get(position).get("name"),
                        Toast.LENGTH_SHORT).show();
            }
        });

        buttonProcessPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductListActivity.this, ShoppingCartActivity.class));
            }
        });

        buttonShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductListActivity.this, MapsActivity.class));
            }
        });
    }

    private void addProduct(String name, String price, String type) {
        Map<String, String> product = new HashMap<>();
        product.put("name", name);
        product.put("price", "$" + price);
        product.put("type", type);
        productList.add(product);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(this, ShoppingCartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}