package com.example.infomovie.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "infoMovie";
    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_NAME = "infoMovies";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_IS_LOGGED_IN = "isLoggedIn";

    public static final String FAVORITES_TABLE_NAME = "favorite";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_MOVIE_ID = "movie_id";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel infoMovies
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_IS_LOGGED_IN + " INTEGER DEFAULT 0)");

        // Membuat tabel favorite
        db.execSQL("CREATE TABLE " + FAVORITES_TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_MOVIE_ID + " TEXT)");
    }

    // Metode untuk memasukkan data pengguna ke dalam tabel infoMovies
    public void insertData(String username, String password, String phone) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_PASSWORD, password);
            values.put(COLUMN_PHONE, phone);
            values.put(COLUMN_IS_LOGGED_IN, 0); // Set default tidak login
            long result = db.insert(TABLE_NAME, null, values);
            if (result == -1) {
                Log.e("DatabaseHelper", "Insert failed"); // Log jika gagal
            } else {
                Log.d("DatabaseHelper", "Insert successful, ID: " + result); // Log jika berhasil
            }
        } finally {
            db.close(); // Menutup koneksi database
        }
    }

    // Metode untuk menghapus data pengguna berdasarkan ID
    public void deleteRecords(int id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            int rows = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            Log.d("DatabaseHelper", "Rows deleted: " + rows); // Log jumlah baris yang dihapus
        } finally {
            db.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Metode ini belum diimplementasikan
    }

    // Metode untuk mendapatkan username dari pengguna yang sedang login
    public String getLoggedInUsername() {
        SQLiteDatabase db = getReadableDatabase();
        String username = null;
        try (Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_USERNAME},
                COLUMN_IS_LOGGED_IN + " = ?",
                new String[]{"1"},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
            }
        } finally {
            db.close();
        }
        return username;
    }

    // Metode untuk menambahkan film favorit ke dalam tabel favorite
    public void insertFavorite(int userId, String movieId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_MOVIE_ID, movieId);
        db.insert(FAVORITES_TABLE_NAME, null, values); // Menyisipkan data ke tabel favorite
        db.close();
    }

    // Metode untuk memeriksa apakah film adalah favorit bagi pengguna tertentu
    public boolean isFavorite(int userId, String movieId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(FAVORITES_TABLE_NAME, new String[]{COLUMN_ID}, COLUMN_USER_ID + " = ? AND " + COLUMN_MOVIE_ID + " = ?", new String[]{String.valueOf(userId), movieId}, null, null, null);
        boolean exists = (cursor.getCount() > 0); // Mengecek apakah data ada
        cursor.close(); // Menutup cursor
        db.close(); // Menutup koneksi database
        return exists; // Mengembalikan true jika ada, jika tidak false
    }

    // Metode untuk mendapatkan semua film favorit berdasarkan userId
    public Cursor getFavoriteMoviesByUserId(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(FAVORITES_TABLE_NAME, null, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
        // Mengembalikan Cursor yang berisi data favorit
    }

    // Metode yang sudah diperbaiki untuk menghapus film favorit
    public void deleteFavorite(int userId, String movieId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FAVORITES_TABLE_NAME, COLUMN_USER_ID + " = ? AND " + COLUMN_MOVIE_ID + " = ?", new String[]{String.valueOf(userId), movieId});
        // Menghapus data dari tabel favorite
        db.close(); // Menutup koneksi database
    }

    public String getTableName() {
        return TABLE_NAME;
    }
    public String getColumnId() {
        return COLUMN_ID;
    }
    public String getColumnUsername() {
        return COLUMN_USERNAME;
    }
    public String getColumnPhone() {
        return COLUMN_PHONE;
    }
}
