package com.foodapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foodapp.database.DatabaseHelper;
import com.foodapp.models.SideDish;
import com.foodapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SideDishDao {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public SideDishDao(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert side dish
    public long insert(SideDish sideDish) {
        ContentValues values = new ContentValues();
        values.put("TenDoAnPhu", sideDish.getTenDoAnPhu());
        values.put("Gia", sideDish.getGia());

        // Only include image if it's not null
        if (sideDish.getAnh() != null) {
            values.put("anh", sideDish.getAnh());
        }

        return db.insert(Constants.TABLE_SIDE_DISH, null, values);
    }

    // Update side dish
    public int update(SideDish sideDish) {
        ContentValues values = new ContentValues();
        values.put("TenDoAnPhu", sideDish.getTenDoAnPhu());
        values.put("Gia", sideDish.getGia());

        // Only include image if it's not null
        if (sideDish.getAnh() != null) {
            values.put("anh", sideDish.getAnh());
        }

        return db.update(Constants.TABLE_SIDE_DISH, values, "MaDoAnPhu=?",
                new String[]{String.valueOf(sideDish.getMaDoAnPhu())});
    }

    // Delete side dish
    public int delete(String id) {
        return db.delete(Constants.TABLE_SIDE_DISH, "MaDoAnPhu=?", new String[]{id});
    }

    // Get all side dishes
    public List<SideDish> getAll() {
        String sql = "SELECT * FROM " + Constants.TABLE_SIDE_DISH;
        return getData(sql);
    }

    // Get side dish by ID
    public SideDish getById(String id) {
        String sql = "SELECT * FROM " + Constants.TABLE_SIDE_DISH + " WHERE MaDoAnPhu=?";
        List<SideDish> list = getData(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    // Format data from cursor
    private List<SideDish> getData(String sql, String... selectionArgs) {
        List<SideDish> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        while (cursor.moveToNext()) {
            SideDish sideDish = new SideDish();
            sideDish.setMaDoAnPhu(cursor.getInt(cursor.getColumnIndexOrThrow("MaDoAnPhu")));
            sideDish.setTenDoAnPhu(cursor.getString(cursor.getColumnIndexOrThrow("TenDoAnPhu")));

            // Get image if exists
            int anhIndex = cursor.getColumnIndex("anh");
            if (anhIndex != -1) {
                sideDish.setAnh(cursor.getString(anhIndex));
            }

            // Get price if exists
            int giaIndex = cursor.getColumnIndex("Gia");
            if (giaIndex != -1) {
                sideDish.setGia(cursor.getDouble(giaIndex));
            }

            list.add(sideDish);
        }
        cursor.close();
        return list;
    }
}