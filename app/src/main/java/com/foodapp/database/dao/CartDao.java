package com.foodapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.foodapp.database.DatabaseHelper;
import com.foodapp.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartDao {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public CartDao(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Thêm sản phẩm vào giỏ hàng
    public long insert(CartItem cartItem) {
        ContentValues values = new ContentValues();
        values.put("tensp", cartItem.getTenSanPham());
        values.put("tendoanphu", cartItem.getTenDoAnPhu());
        values.put("giasp", cartItem.getGiaSanPham());
        values.put("soluong", cartItem.getSoLuong());
        values.put("anhsp", cartItem.getAnhSanPham());

        return db.insert("dt_giohang", null, values);
    }

    // Cập nhật số lượng sản phẩm trong giỏ
    public int update(CartItem cartItem) {
        ContentValues values = new ContentValues();
        values.put("soluong", cartItem.getSoLuong());

        return db.update("dt_giohang", values, "masp=?", new String[]{String.valueOf(cartItem.getMaSanPham())});
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public int delete(String id) {
        return db.delete("dt_giohang", "masp=?", new String[]{id});
    }

    // Xóa toàn bộ giỏ hàng
    public void deleteAll() {
        db.delete("dt_giohang", null, null);
    }

    // Lấy tất cả sản phẩm trong giỏ hàng
    public List<CartItem> getAll() {
        String sql = "SELECT * FROM dt_giohang";
        return getData(sql);
    }

    // Xử lý dữ liệu từ cursor
    private List<CartItem> getData(String sql, String... selectionArgs) {
        List<CartItem> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        while (cursor.moveToNext()) {
            CartItem cartItem = new CartItem();
            cartItem.setMaSanPham(cursor.getInt(0));
            cartItem.setTenSanPham(cursor.getString(1));
            cartItem.setTenDoAnPhu(cursor.getString(2));
            cartItem.setGiaSanPham(cursor.getDouble(3));
            cartItem.setSoLuong(cursor.getInt(4));
            cartItem.setAnhSanPham(cursor.getString(5));
            list.add(cartItem);
        }
        cursor.close();
        return list;
    }

    // Tính tổng giá trị giỏ hàng
    public double getTotalPrice() {
        double total = 0;
        List<CartItem> cartItems = getAll();
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }
}