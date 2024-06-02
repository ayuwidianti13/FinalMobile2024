package com.example.infomovie;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.infomovie.MainActivity;
import com.example.infomovie.R;
import com.example.infomovie.RegistrasiActivity;
import com.example.infomovie.Sqlite.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    EditText et_username;
    EditText et_password;
    Button btn_login;
    TextView tv_register;
    DatabaseHelper databaseHelper;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        tv_register = findViewById(R.id.tv_register);

        tv_register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrasiActivity.class);
            startActivity(intent);
        });

        btn_login.setOnClickListener(v -> {
            String username = et_username.getText().toString().trim();
            String password = et_password.getText().toString().trim();

            if (username.isEmpty()) {
                et_username.setError("Please enter your username");
            } else if (password.isEmpty()) {
                et_password.setError("Please enter your password");
            } else {
                login(username, password);
            }
        });
    }

    private void login(String username, String password) {
        try (SQLiteDatabase db = databaseHelper.getReadableDatabase();
             //mencari pengguna berdasarkan username dan password.
             Cursor cursor = db.query(
                     DatabaseHelper.TABLE_NAME,
                     new String[]{DatabaseHelper.COLUMN_ID},
                     DatabaseHelper.COLUMN_USERNAME + "=? AND " + DatabaseHelper.COLUMN_PASSWORD + "=?",
                     new String[]{username, password},
                     null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                //mendapatkan indeks kolom id
                int idColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                if (idColumnIndex != -1) {
                    int userId = cursor.getInt(idColumnIndex);
                    loginSuccess(userId); // Menyimpan ID pengguna yang berhasil login
                    updateLoginStatus(username, true);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            } else {
                Toast.makeText(this, "Incorrect username or Password", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void updateLoginStatus(String username, boolean isLoggedIn) {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_IS_LOGGED_IN, isLoggedIn ? 1 : 0);
            db.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.COLUMN_USERNAME + " = ?", new String[]{username});
        }
    }

    private void loginSuccess(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id", userId);
        editor.apply();
    }

    @Override
    // Dipanggil saat aktivitas dimulai
    protected void onStart() {
        super.onStart();
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        try (SQLiteDatabase db = databaseHelper.getReadableDatabase();
             Cursor cursor = db.query(
                     DatabaseHelper.TABLE_NAME,
                     new String[]{DatabaseHelper.COLUMN_ID},
                     DatabaseHelper.COLUMN_IS_LOGGED_IN + " = ?",
                     new String[]{"1"},
                     null, null, null)) {

            if (cursor.getCount() > 0) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }

    }
}
