package com.foodapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foodapp.database.DatabaseHelper;
import com.foodapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public UserDao(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Insert user
    public long insert(User user) {
        ContentValues values = new ContentValues();
        values.put("MaND", user.getMaND());
        values.put("HoTen", user.getHoTen());
        values.put("MatKhau", user.getMatKhau());
        values.put("Email", user.getEmail());
        values.put("NamSinh", user.getNamSinh());
        values.put("SDT", user.getSdt());
        values.put("LoaiTaiKhoan", "user");

        return db.insert("dt_nguoidung", null, values);
    }

    // Update user
    public int update(User user) {
        ContentValues values = new ContentValues();
        values.put("HoTen", user.getHoTen());
        values.put("MatKhau", user.getMatKhau());
        values.put("Email", user.getEmail());
        values.put("NamSinh", user.getNamSinh());
        values.put("SDT", user.getSdt());

        return db.update("dt_nguoidung", values, "MaND=?", new String[]{user.getMaND()});
    }

    // Delete user
    public int delete(String id) {
        return db.delete("dt_nguoidung", "MaND=?", new String[]{id});
    }

    // Get all users
    public List<User> getAll() {
        String sql = "SELECT * FROM dt_nguoidung";
        return getData(sql);
    }

    // Get user by ID
    public User getById(String id) {
        String sql = "SELECT * FROM dt_nguoidung WHERE MaND=?";
        List<User> list = getData(sql, id);
        if (list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    // Check login
    public User checkLogin(String id, String password) {
        String sql = "SELECT * FROM dt_nguoidung WHERE MaND=? AND MatKhau=?";
        List<User> list = getData(sql, id, password);
        if (list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    // Check if user ID exists
    public boolean checkID(String id) {
        String sql = "SELECT * FROM dt_nguoidung WHERE MaND=?";
        List<User> list = getData(sql, id);
        if (list.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    // Find user ID by username
    public String findUserIdByUsername(String username) {
        String sql = "SELECT MaND FROM dt_nguoidung WHERE HoTen=?";
        List<User> list = getData(sql, username);
        if (list.size() > 0) {
            return list.get(0).getMaND();
        }
        return null;
    }

    // Update password
    public int updatePassword(User user) {
        ContentValues values = new ContentValues();
        values.put("MatKhau", user.getMatKhau());

        return db.update("dt_nguoidung", values, "MaND=?", new String[]{user.getMaND()});
    }

    // Format data from cursor
    private List<User> getData(String sql, String... selectionArgs) {
        List<User> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        while (cursor.moveToNext()) {
            User user = new User();
            user.setMaTV(cursor.getInt(0));
            user.setMaND(cursor.getString(1));
            user.setHoTen(cursor.getString(2));
            user.setMatKhau(cursor.getString(3));
            user.setEmail(cursor.getString(4));
            user.setNamSinh(cursor.getString(5));
            user.setSdt(cursor.getString(6));
            user.setLoaiTaiKhoan(cursor.getString(7));

            list.add(user);
        }
        cursor.close();
        return list;
    }
}