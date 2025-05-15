package com.example.btl.utils;

/**
 * Các hằng số và cấu hình cố định sử dụng trong ứng dụng
 */
public class Constants {

    // Tên SharedPreferences
    public static final String PREF_NAME = "FoodAppPreferences";
    public static final String PREF_USER = "user";

    // Keys cho SharedPreferences
    public static final String KEY_USERNAME = "USERNAME";
    public static final String KEY_PASSWORD = "PASSWORD";
    public static final String KEY_REMEMBER = "REMEMBER";
    public static final String KEY_IS_LOGGED_IN = "IS_LOGGED_IN";

    // Mã lỗi request
    public static final int FOOD_DETAIL_REQUEST_CODE = 100;
    public static final int CART_REQUEST_CODE = 101;
    public static final int USER_PROFILE_REQUEST_CODE = 102;

    // Trạng thái đơn hàng
    public static final String STATUS_PENDING = "Đang chờ xử lý";
    public static final String STATUS_COOKING = "Đang chế biến";
    public static final String STATUS_DELIVERING = "Đang giao hàng";
    public static final String STATUS_COMPLETED = "Đã hoàn thành";
    public static final String STATUS_CANCELLED = "Đã hủy";

    // Phương thức thanh toán
    public static final String PAYMENT_CASH = "Tiền mặt khi nhận hàng";
    public static final String PAYMENT_CREDIT_CARD = "Thẻ tín dụng";

    // Loại người dùng
    public static final String USER_TYPE_ADMIN = "admin";
    public static final String USER_TYPE_CHEF = "daubep";
    public static final String USER_TYPE_USER = "user";

    // Intent extra keys
    public static final String EXTRA_FOOD_ID = "food_id";
    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_CATEGORY_NAME = "category_name";
    public static final String EXTRA_USER_ID = "ma";
    public static final String EXTRA_ORDER_ID = "order_id";
    public static final String EXTRA_SELECTED_ITEMS_ONLY = "selected_items_only";

    // Database constants
    public static final String DB_NAME = "duan_datdoan";
    public static final int DB_VERSION = 20;

    // Tables
    public static final String TABLE_USER = "dt_nguoidung";
    public static final String TABLE_FOOD = "dt_doan";
    public static final String TABLE_CATEGORY = "dt_loai";
    public static final String TABLE_SIDE_DISH = "dt_doanphu";
    public static final String TABLE_ORDER = "dt_hoadon";
    public static final String TABLE_CART = "dt_giohang";
    public static final String TABLE_TABLE = "dt_banan";
}