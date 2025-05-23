package com.foodapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.foodapp.database.DatabaseHelper;
import com.foodapp.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public OrderDao(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Thêm hóa đơn mới
    public long insert(Order order) {
        ContentValues values = new ContentValues();
        values.put("maTV", order.getUserId());
        values.put("hoten", order.getHoTen());
        values.put("SDT", order.getSdt());
        values.put("diachinhan", order.getDiaChi());
        values.put("thucdon", order.getThucDon());
        values.put("ngaydathang", order.getNgayDat());
        values.put("tongtien", order.getTongTien());
        values.put("thanhtoan", order.getThanhToan());

        long result = db.insert("dt_hoadon", null, values);
        return result;
    }

    public int delete(String id) {
        return db.delete("dt_hoadon", "mahoadon=?", new String[]{id});
    }

    public List<Order> getAll() {
        String sql = "SELECT * FROM dt_hoadon";
        return getData(sql);
    }

    public Order getById(String id) {
        String sql = "SELECT * FROM dt_hoadon WHERE mahoadon=?";
        List<Order> list = getData(sql, id);
        if (list.isEmpty()) {
            return null;
        }
        Order foundOrder = list.get(0);
        return foundOrder;
    }

    public List<Order> getByUser(String userId) {
        String sql = "SELECT * FROM dt_hoadon WHERE maTV=?";
        List<Order> result = getData(sql, userId);
        return result;
    }

    // Lấy hóa đơn theo userId
    public List<Order> getOrdersByUser(String userId) {
        String sql = "SELECT * FROM dt_hoadon WHERE Email=(SELECT Email FROM dt_nguoidung WHERE MaND=?)";
        return getData(sql, userId);
    }


    private List<Order> getData(String sql, String... selectionArgs) {
        List<Order> list = new ArrayList<>();
        try {
            Log.d("OrderDao", "Executing query: " + sql + " with args: " + String.join(", ", selectionArgs));
            Cursor cursor = db.rawQuery(sql, selectionArgs);
            Log.d("OrderDao", "Cursor count: " + cursor.getCount());

            while (cursor.moveToNext()) {
                try {
                    Order order = new Order();
                    order.setMaHoaDon(cursor.getInt(cursor.getColumnIndexOrThrow("mahoadon")));
                    order.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("maTV")));
                    order.setHoTen(cursor.getString(cursor.getColumnIndexOrThrow("hoten")));
                    order.setSdt(cursor.getString(cursor.getColumnIndexOrThrow("SDT")));
                    order.setDiaChi(cursor.getString(cursor.getColumnIndexOrThrow("diachinhan")));
                    order.setThucDon(cursor.getString(cursor.getColumnIndexOrThrow("thucdon")));
                    order.setNgayDat(cursor.getString(cursor.getColumnIndexOrThrow("ngaydathang")));
                    order.setTongTien(cursor.getInt(cursor.getColumnIndexOrThrow("tongtien")));
                    order.setThanhToan(cursor.getString(cursor.getColumnIndexOrThrow("thanhtoan")));
                    list.add(order);
                } catch (Exception e) {
                    Log.e("OrderDao", "Error parsing order data", e);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("OrderDao", "Database error in getData", e);
        }
        Log.d("OrderDao", "Returning " + list.size() + " orders");
        return list;
    }
}