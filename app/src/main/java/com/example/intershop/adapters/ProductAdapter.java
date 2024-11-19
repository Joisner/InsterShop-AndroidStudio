package com.example.intershop.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intershop.DatabaseHelper;
import com.example.intershop.R;
import com.example.intershop.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements ListAdapter {

    private Context context;
    private List<Product> products;
    private DatabaseHelper dbHelper;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText(String.format("$%.2f", product.getPrice()));

        holder.buttonEdit.setOnClickListener(v -> showEditProductDialog(product));
        holder.buttonDelete.setOnClickListener(v -> deleteProduct(product));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void addProduct(Product product) {
        products.add(product);
        notifyItemInserted(products.size() - 1);
    }

    private void showEditProductDialog(final Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_edit_product, null);
        builder.setView(view);

        final TextView editTextName = view.findViewById(R.id.editTextProductName);
        final TextView editTextPrice = view.findViewById(R.id.editTextProductPrice);
        final TextView editTextDescription = view.findViewById(R.id.editTextProductDescription);

        editTextName.setText(product.getName());
        editTextPrice.setText(String.valueOf(product.getPrice()));
        editTextDescription.setText(product.getDescription());

        builder.setPositiveButton("Update", (dialog, which) -> {
            String name = editTextName.getText().toString();
            double price = Double.parseDouble(editTextPrice.getText().toString());
            String description = editTextDescription.getText().toString();

            Product updatedProduct = new Product(product.getId(), name, price, description, product.getImage());
            int result = dbHelper.updateProduct(updatedProduct);

            if (result > 0) {
                int index = products.indexOf(product);
                products.set(index, updatedProduct);
                notifyItemChanged(index);
                Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to update product", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteProduct(Product product) {
        int result = dbHelper.deleteProduct(product.getId());

        if (result > 0) {
            int index = products.indexOf(product);
            products.remove(index);
            notifyItemRemoved(index);
            Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewPrice;
        Button buttonEdit;
        Button buttonDelete;

        ProductViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewProductName);
            textViewPrice = itemView.findViewById(R.id.textViewProductPrice);
            buttonEdit = itemView.findViewById(R.id.buttonEditProduct);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteProduct);
        }
    }
}