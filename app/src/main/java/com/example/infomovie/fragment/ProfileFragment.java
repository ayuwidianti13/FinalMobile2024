package com.example.infomovie.fragment;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.infomovie.EditProfileActivity;
import com.example.infomovie.LoginActivity;
import com.example.infomovie.R;
import com.example.infomovie.Sqlite.DatabaseHelper;

public class ProfileFragment extends Fragment {
    private TextView tv_name, tv_number;
    private Button btn_logout, btn_change;
    private ImageView iv_delete;
    private DatabaseHelper databaseHelper;
    private int recordId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(getActivity());

        tv_name = view.findViewById(R.id.tv_username);
        tv_number = view.findViewById(R.id.tv_phone);
        btn_change = view.findViewById(R.id.btn_change);
        btn_logout = view.findViewById(R.id.btn_logout);
        iv_delete = view.findViewById(R.id.iv_delete);

        loadUserData();

        btn_change.setOnClickListener(v -> {
            startActivityForResult(new Intent(getActivity(), EditProfileActivity.class), 1);
        });

        btn_logout.setOnClickListener(v -> logoutUser());

        iv_delete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void loadUserData() {
        try (SQLiteDatabase db = databaseHelper.getReadableDatabase();
             Cursor cursor = db.query(
                     DatabaseHelper.TABLE_NAME,
                     new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_PHONE},
                     DatabaseHelper.COLUMN_IS_LOGGED_IN + " = ?",
                     new String[]{"1"},
                     null, null, null)) {

            if (cursor.moveToFirst()) {
                recordId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));

                tv_name.setText(username);
                tv_number.setText(phone);
            }
        }
    }

    private void logoutUser() {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_IS_LOGGED_IN, 0);
            db.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.COLUMN_IS_LOGGED_IN + " = ?", new String[]{"1"});
        }

        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            loadUserData();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Hapus Akun");
        builder.setMessage("Apakah anda yakin ingin menghapus akun ini?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            databaseHelper.deleteRecords(recordId);
            logoutUser();
        });
        builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}