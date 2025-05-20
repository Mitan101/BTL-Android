package com.foodapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.foodapp.R;
import com.foodapp.database.dao.UserDao;
import com.foodapp.utils.AppUtils;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextInputEditText etEmail, etUsername;
    private Button btnResetPassword;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo DAO
        userDao = new UserDao(this);

        // Ánh xạ view
        toolbar = findViewById(R.id.toolbarForgotPassword);
        etEmail = findViewById(R.id.etForgotPasswordEmail);
        etUsername = findViewById(R.id.etForgotPasswordUsername);
        btnResetPassword = findViewById(R.id.btnForgotPasswordReset);

        // Cấu hình toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.forgot_password);

        toolbar.setNavigationOnClickListener(v -> finish());

        // Xử lý sự kiện cho nút reset mật khẩu
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();

        if (email.isEmpty() || username.isEmpty()) {
            showToast(getString(R.string.empty_fields));
            return;
        }

        // Hiện thông báo đặt lại mật khẩu thành công
        // Trong thực tế, bạn sẽ cần gửi email reset hoặc thực hiện logic reset mật khẩu khác
        AppUtils.showInfoDialog(this,
                getString(R.string.success),
                "Hướng dẫn đặt lại mật khẩu đã được gửi đến email của bạn.");
        finish();
    }
}