package com.example.infomovie;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infomovie.Sqlite.DatabaseHelper;

public class EditProfileActivity extends AppCompatActivity {
    EditText et_name, et_number;
    Button btn_simpan;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        // Inisialisasi objek database
        databaseHelper = new DatabaseHelper(this);

        et_name = findViewById(R.id.et_username);
        et_number = findViewById(R.id.et_numberPhone);
        btn_simpan = findViewById(R.id.btn_simpan);

        // Load user data from SQLite
        loadUserData();

        btn_simpan.setOnClickListener(view -> {
            String name = et_name.getText().toString();
            String number = et_number.getText().toString();

            if (!name.isEmpty() && !number.isEmpty()) {
                saveUserData(name, number);

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                Toast.makeText(EditProfileActivity.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditProfileActivity.this, "Harap isi semua bidang", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                new String[]{DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_PHONE},
                DatabaseHelper.COLUMN_IS_LOGGED_IN + " = ?",
                new String[]{"1"},
                null, null, null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));

            et_name.setText(name);
            et_number.setText(phone);
        }

        cursor.close();
        db.close();
    }

    private void saveUserData(String name, String number) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, name);
        values.put(DatabaseHelper.COLUMN_PHONE, number);

        db.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.COLUMN_IS_LOGGED_IN + " = ?", new String[]{"1"});
        db.close();
    }
}