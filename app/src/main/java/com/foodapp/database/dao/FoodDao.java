package com.foodapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foodapp.database.DatabaseHelper;
import com.foodapp.models.Food;
import com.foodapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class FoodDao {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public FoodDao(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert food
    public long insert(Food food) {
        ContentValues values = new ContentValues();
        values.put("tendoan", food.getTenDoAn());
        values.put("giadoan", food.getGiaDoAn());
        values.put("maloai", food.getMaLoai());
        values.put("thongtin", food.getThongTin());
        values.put("anh", food.getLinkAnh());

        return db.insert(Constants.TABLE_FOOD, null, values);
    }

    // Update food
    public int update(Food food) {
        ContentValues values = new ContentValues();
        values.put("tendoan", food.getTenDoAn());
        values.put("giadoan", food.getGiaDoAn());
        values.put("maloai", food.getMaLoai());
        values.put("thongtin", food.getThongTin());
        values.put("anh", food.getLinkAnh());

        return db.update(Constants.TABLE_FOOD, values, "madoan=?", new String[]{String.valueOf(food.getMaDoAn())});
    }

    // Delete food
    public int delete(String id) {
        return db.delete(Constants.TABLE_FOOD, "madoan=?", new String[]{id});
    }

    // Get all foods
    public List<Food> getAll() {
        String sql = "SELECT * FROM " + Constants.TABLE_FOOD;
        return getData(sql);
    }

    // Get foods by category
    public List<Food> getFoodsByCategory(String categoryId) {
        String sql = "SELECT * FROM " + Constants.TABLE_FOOD + " WHERE maloai=?";
        return getData(sql, categoryId);
    }

    // Get food by ID
    public Food getById(String id) {
        String sql = "SELECT * FROM " + Constants.TABLE_FOOD + " WHERE madoan=?";
        List<Food> list = getData(sql, id);
        return list.get(0);
    }

    // Format data from cursor
    private List<Food> getData(String sql, String... selectionArgs) {
        List<Food> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        while (cursor.moveToNext()) {
            Food food = new Food();
            food.setMaDoAn(cursor.getInt(0));
            food.setTenDoAn(cursor.getString(1));
            food.setGiaDoAn(cursor.getDouble(2));
            food.setMaLoai(cursor.getInt(3));
            food.setThongTin(cursor.getString(4));
            food.setLinkAnh(cursor.getString(5));

            list.add(food);
        }
        cursor.close();
        return list;
    }
}