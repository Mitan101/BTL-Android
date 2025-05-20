package com.foodapp.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.foodapp.utils.SharedPreferencesManager;

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
     * Hiển thị dialog loading với thông báo
     * @param message Thông báo hiển thị
     */
    protected void showLoading(String message) {
        if (loadingDialog == null) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(30, 30, 30, 30);

            ProgressBar progressBar = new ProgressBar(this);
            layout.addView(progressBar);

            TextView textView = new TextView(this);
            textView.setText(message);
            textView.setPadding(20, 20, 20, 0);
            textView.setGravity(Gravity.CENTER);
            layout.addView(textView);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(layout);
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
     * Lấy ID người dùng hiện tại
     *
     * @return ID người dùng
     */
    protected String getCurrentUserId() {
        return prefsManager.getUserId();
    }

    /**
     * Lấy tên đăng nhập của người dùng hiện tại
     *
     * @return Tên đăng nhập
     */
    protected String getCurrentUsername() {
        return prefsManager.getUsername();
    }

    /**
     * Lấy email của người dùng hiện tại
     *
     * @return Email người dùng
     */
    protected String getCurrentUserEmail() {
        return prefsManager.getEmail();
    }

    /**
     * Lấy họ tên đầy đủ của người dùng hiện tại
     *
     * @return Họ tên người dùng
     */
    protected String getCurrentUserFullName() {
        return prefsManager.getFullName();
    }

    /**
     * Lấy loại tài khoản của người dùng hiện tại
     *
     * @return Loại tài khoản
     */
    protected String getCurrentUserType() {
        return prefsManager.getUserType();
    }
}