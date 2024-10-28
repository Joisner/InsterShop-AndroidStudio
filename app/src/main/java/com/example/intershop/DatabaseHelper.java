package com.example.intershop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "intershop.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla Usuarios
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    private SQLiteDatabase db;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla usuarios
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT)";

        try {
            db.execSQL(CREATE_USERS_TABLE);
            Log.d("DatabaseHelper", "Tabla de usuarios creada exitosamente");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error al crear la tabla de usuarios: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public long addUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);

        try {
            long id = db.insert(TABLE_USERS, null, values);
            Log.d("DatabaseHelper", "Usuario agregado con ID: " + id);
            return id;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error al agregar usuario: " + e.getMessage());
            return -1;
        } finally {
            db.close();
        }
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        try {
            Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
            int count = cursor.getCount();
            cursor.close();
            Log.d("DatabaseHelper", "Usuario encontrado: " + (count > 0));
            return count > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error al verificar usuario: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }
    public void open() {
        db = this.getWritableDatabase();
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}