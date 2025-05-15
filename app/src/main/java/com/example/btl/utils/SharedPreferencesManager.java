package com.example.btl.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Lớp tiện ích để quản lý SharedPreferences
 */
public class SharedPreferencesManager {
    private static final String PREF_NAME = "FoodAppPreferences";
    private static final String KEY_USERNAME = "USERNAME";
    private static final String KEY_PASSWORD = "PASSWORD";
    private static final String KEY_REMEMBER = "REMEMBER";
    private static final String KEY_IS_LOGGED_IN = "IS_LOGGED_IN";
    private static final String KEY_FULL_NAME = "FULL_NAME";
    private static final String KEY_EMAIL = "EMAIL";
    private static final String KEY_PHONE = "PHONE";
    private static final String KEY_USER_TYPE = "USER_TYPE";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    // Constructor
    public SharedPreferencesManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Lưu thông tin đăng nhập
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @param remember Ghi nhớ đăng nhập
     */
    public void saveLoginDetails(String username, String password, boolean remember) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_REMEMBER, remember);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Lưu thông tin chi tiết của người dùng
     * @param fullName Tên đầy đủ
     * @param email Email
     * @param phone Số điện thoại
     * @param userType Loại tài khoản
     */
    public void saveUserDetails(String fullName, String email, String phone, String userType) {
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_USER_TYPE, userType);
        editor.apply();
    }

    /**
     * Kiểm tra xem người dùng có đăng nhập hay không
     * @return true nếu đã đăng nhập, false nếu chưa
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Lấy tên đăng nhập đã lưu
     * @return Tên đăng nhập
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    /**
     * Lấy mật khẩu đã lưu
     * @return Mật khẩu
     */
    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }

    /**
     * Lấy tên đầy đủ của người dùng
     * @return Tên đầy đủ
     */
    public String getFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, "");
    }

    /**
     * Lấy email của người dùng
     *
     * @return Email
     */
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    /**
     * Lấy số điện thoại của người dùng
     *
     * @return Số điện thoại
     */
    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, "");
    }

    /**
     * Lấy loại tài khoản người dùng
     *
     * @return Loại tài khoản (admin, user, chef)
     */
    public String getUserType() {
        return sharedPreferences.getString(KEY_USER_TYPE, "");
    }

    /**
     * Kiểm tra có ghi nhớ đăng nhập hay không
     * @return true nếu có ghi nhớ, false nếu không
     */
    public boolean isRemembered() {
        return sharedPreferences.getBoolean(KEY_REMEMBER, false);
    }

    /**
     * Xóa thông tin đăng nhập
     */
    public void clearLoginDetails() {
        if (!isRemembered()) {
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_PASSWORD);
        }
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    /**
     * Xóa toàn bộ dữ liệu trong SharedPreferences
     */
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}