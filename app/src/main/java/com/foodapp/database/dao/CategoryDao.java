package com.foodapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foodapp.database.DatabaseHelper;
import com.foodapp.models.Category;
import com.foodapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public CategoryDao(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert category
    public long insert(Category category) {
        ContentValues values = new ContentValues();
        values.put("tenloai", category.getTenLoai());

        return db.insert(Constants.TABLE_CATEGORY, null, values);
    }

    // Update category
    public int update(Category category) {
        ContentValues values = new ContentValues();
        values.put("tenloai", category.getTenLoai());

        return db.update(Constants.TABLE_CATEGORY, values, "maloai=?", new String[]{String.valueOf(category.getMaLoai())});
    }

    // Delete category
    public int delete(String id) {
        return db.delete(Constants.TABLE_CATEGORY, "maloai=?", new String[]{id});
    }

    // Get all categories
    public List<Category> getAll() {
        String sql = "SELECT * FROM " + Constants.TABLE_CATEGORY;
        return getData(sql);
    }

    // Get category by ID
    public Category getById(String id) {
        String sql = "SELECT * FROM " + Constants.TABLE_CATEGORY + " WHERE maloai=?";
        List<Category> list = getData(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    // Format data from cursor
    private List<Category> getData(String sql, String... selectionArgs) {
        List<Category> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            Category category = new Category();
            category.setMaLoai(cursor.getInt(0));
            category.setTenLoai(cursor.getString(1));

            list.add(category);
        }
        cursor.close();
        return list;
    }
}