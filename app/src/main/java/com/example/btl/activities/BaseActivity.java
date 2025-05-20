package com.example.btl.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl.utils.SharedPreferencesManager;

/**
 * Activity cơ sở cung cấp các phương thức hỗ trợ chung cho tất cả các Activity khác
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected SharedPreferencesManager prefsManager;
    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo SharedPreferencesManager
        prefsManager = new SharedPreferencesManager(this);
    }

    /**
     * Hiển thị thông báo ngắn
     *
     * @param message Nội dung thông báo
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Hiển thị dialog loading
     */
    protected void showLoading() {
        if (loadingDialog == null) {
            ProgressBar progressBar = new ProgressBar(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(progressBar);
            builder.setCancelable(false);
            loadingDialog = builder.create();
            loadingDialog.show();
        }
    }

    /**
     * Ẩn dialog loading
     */
    protected void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**
     * Kiểm tra trạng thái đăng nhập
     *
     * @return true nếu người dùng đã đăng nhập, false nếu chưa
     */
    protected boolean isLoggedIn() {
        return prefsManager.isLoggedIn();
    }

    /**
     * Lấy tên đăng nhập của người dùng hiện tại
     *
     * @return Tên đăng nhập
     */
    protected String getCurrentUsername() {
        return prefsManager.getUsername();
    }
}