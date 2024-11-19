package com.example.intershop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.intershop.models.Product;
import com.example.intershop.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "InterShop.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRODUCTS = "products";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    // PRODUCTS Table - column names
    private static final String KEY_PRICE = "price";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE_URL = "image_url";

    // Users table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS +
            "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_NAME + " TEXT," +
            KEY_EMAIL + " TEXT UNIQUE," +
            KEY_PASSWORD + " TEXT" + ")";

    // Products table create statement
    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS +
            "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_NAME + " TEXT," +
            KEY_PRICE + " REAL," +
            KEY_DESCRIPTION + " TEXT," +
            KEY_IMAGE_URL + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Hash password
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // User CRUD operations
    public long addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PASSWORD, hashPassword(password));
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public boolean checkUser(String email, String password) {
        String[] columns = {KEY_ID};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = KEY_EMAIL + " = ?" + " AND " + KEY_PASSWORD + " = ?";
        String[] selectionArgs = {email, hashPassword(password)};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public boolean checkUserExists(String email) {
        String[] columns = {KEY_ID};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = KEY_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public User getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_USERS, new String[]{KEY_ID, KEY_NAME, KEY_EMAIL, KEY_PASSWORD},
                    KEY_EMAIL + "=?", new String[]{email}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int nameIndex = cursor.getColumnIndex(KEY_NAME);
                int emailIndex = cursor.getColumnIndex(KEY_EMAIL);
                int passwordIndex = cursor.getColumnIndex(KEY_PASSWORD);

                if (idIndex != -1 && nameIndex != -1 && emailIndex != -1 && passwordIndex != -1) {
                    user = new User(
                            cursor.getInt(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(emailIndex),
                            cursor.getString(passwordIndex)
                    );
                } else {
                    Log.e("DatabaseHelper", "One or more columns not found in the users table");
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting user: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PASSWORD, hashPassword(user.getPassword()));
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    // Product CRUD operations
    public long addProduct(String name, double price, String description, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PRICE, price);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_IMAGE_URL, imageUrl);
        long id = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return id;
    }

    public Product getProduct(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Product product = null;
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_PRODUCTS, new String[]{KEY_ID, KEY_NAME, KEY_PRICE, KEY_DESCRIPTION, KEY_IMAGE_URL},
                    KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int nameIndex = cursor.getColumnIndex(KEY_NAME);
                int priceIndex = cursor.getColumnIndex(KEY_PRICE);
                int descriptionIndex = cursor.getColumnIndex(KEY_DESCRIPTION);
                int imageUrlIndex = cursor.getColumnIndex(KEY_IMAGE_URL);

                if (idIndex != -1 && nameIndex != -1 && priceIndex != -1 && descriptionIndex != -1 && imageUrlIndex != -1) {
                    product = new Product(
                            cursor.getInt(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getDouble(priceIndex),
                            cursor.getString(descriptionIndex),
                            cursor.getString(imageUrlIndex)
                    );
                } else {
                    Log.e("DatabaseHelper", "One or more columns not found in the products table");
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting product: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return product;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int nameIndex = cursor.getColumnIndex(KEY_NAME);
                int priceIndex = cursor.getColumnIndex(KEY_PRICE);
                int descriptionIndex = cursor.getColumnIndex(KEY_DESCRIPTION);
                int imageUrlIndex = cursor.getColumnIndex(KEY_IMAGE_URL);

                if (idIndex != -1 && nameIndex != -1 && priceIndex != -1 && descriptionIndex != -1 && imageUrlIndex != -1) {
                    do {
                        Product product = new Product(
                                cursor.getInt(idIndex),
                                cursor.getString(nameIndex),
                                cursor.getDouble(priceIndex),
                                cursor.getString(descriptionIndex),
                                cursor.getString(imageUrlIndex)
                        );
                        productList.add(product);
                    } while (cursor.moveToNext());
                } else {
                    Log.e("DatabaseHelper", "One or more columns not found in the products table");
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting all products: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return productList;
    }


    public int deleteProduct(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return 0;
    }

    public Integer updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, product.getName());
        values.put(KEY_PRICE, product.getPrice());
        values.put(KEY_DESCRIPTION, product.getDescription());
        values.put(KEY_IMAGE_URL, product.getImageUrl());
        return db.update(TABLE_PRODUCTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(product.getId())});
    }
}