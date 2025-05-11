package com.foodapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        values.put("Email", order.getEmail());
        values.put("hoten", order.getHoTen());
        values.put("SDT", order.getSdt());
        values.put("diachinhan", order.getDiaChi());
        values.put("thucdon", order.getThucDon());
        values.put("ngaydathang", order.getNgayDat());
        values.put("tongtien", order.getTongTien());
        values.put("thanhtoan", order.getThanhToan());
        values.put("trangthai", order.getTrangThai());

        return db.insert("dt_hoadon", null, values);
    }

    // Cập nhật hóa đơn
    public int update(Order order) {
        ContentValues values = new ContentValues();
        values.put("Email", order.getEmail());
        values.put("hoten", order.getHoTen());
        values.put("SDT", order.getSdt());
        values.put("diachinhan", order.getDiaChi());
        values.put("thucdon", order.getThucDon());
        values.put("ngaydathang", order.getNgayDat());
        values.put("tongtien", order.getTongTien());
        values.put("thanhtoan", order.getThanhToan());
        values.put("trangthai", order.getTrangThai());

        return db.update("dt_hoadon", values, "mahoadon=?", new String[]{String.valueOf(order.getMaHoaDon())});
    }

    // Xóa hóa đơn
    public int delete(String id) {
        return db.delete("dt_hoadon", "mahoadon=?", new String[]{id});
    }

    // Lấy tất cả hóa đơn
    public List<Order> getAll() {
        String sql = "SELECT * FROM dt_hoadon";
        return getData(sql);
    }

    // Lấy hóa đơn theo ID
    public Order getById(String id) {
        String sql = "SELECT * FROM dt_hoadon WHERE mahoadon=?";
        List<Order> list = getData(sql, id);
        return list.get(0);
    }

    // Lấy hóa đơn theo email người dùng
    public List<Order> getByUserEmail(String email) {
        String sql = "SELECT * FROM dt_hoadon WHERE Email=?";
        return getData(sql, email);
    }

    // Lấy hóa đơn theo userId
    public List<Order> getOrdersByUser(String userId) {
        String sql = "SELECT * FROM dt_hoadon WHERE Email=(SELECT Email FROM dt_user WHERE MaND=?)";
        return getData(sql, userId);
    }

    // Cập nhật trạng thái hóa đơn
    public int updateStatus(int maHoaDon, String trangThai) {
        ContentValues values = new ContentValues();
        values.put("trangthai", trangThai);

        return db.update("dt_hoadon", values, "mahoadon=?", new String[]{String.valueOf(maHoaDon)});
    }

    // Xử lý dữ liệu từ cursor
    private List<Order> getData(String sql, String... selectionArgs) {
        List<Order> list = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        while (cursor.moveToNext()) {
            Order order = new Order();
            order.setMaHoaDon(cursor.getInt(0));
            order.setEmail(cursor.getString(1));
            order.setHoTen(cursor.getString(2));
            order.setSdt(cursor.getString(3));
            order.setDiaChi(cursor.getString(4));
            order.setThucDon(cursor.getString(5));
            order.setNgayDat(cursor.getString(6));
            order.setTongTien(cursor.getDouble(7));
            order.setThanhToan(cursor.getString(8));
            order.setTrangThai(cursor.getString(9));

            list.add(order);
        }
        cursor.close();
        return list;
    }
}