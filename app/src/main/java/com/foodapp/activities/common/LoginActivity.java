package com.foodapp.activities.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.view.KeyEvent;

import com.foodapp.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.foodapp.R;
import com.foodapp.database.dao.UserDao;


public class LoginActivity extends BaseActivity {
    private Button loginButton, signupButton;
    private TextInputEditText usernameEditText, passwordEditText;
    private CheckBox rememberCheckbox;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ các view
        usernameEditText = findViewById(R.id.lg_name);
        passwordEditText = findViewById(R.id.lg_pass);
        rememberCheckbox = findViewById(R.id.lg_check);
        loginButton = findViewById(R.id.lg_login);
        signupButton = findViewById(R.id.lg_singup);

        // Khởi tạo DAO
        userDao = new UserDao(this);

        // Thiết lập ẩn bàn phím khi nhấn bên ngoài
        setupUI(findViewById(android.R.id.content));

        // Thiết lập sự kiện ẩn bàn phím khi nhấn Done
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard();
                return true;
            }
            return false;
        });

        // Lấy thông tin đăng nhập đã lưu (nếu có)
        if (prefsManager.isRemembered()) {
            usernameEditText.setText(prefsManager.getUsername());
            passwordEditText.setText(prefsManager.getPassword());
            rememberCheckbox.setChecked(true);
        }

        // Xử lý sự kiện đăng nhập
        loginButton.setOnClickListener(v -> checkLogin());

        // Xử lý sự kiện chuyển đến màn hình đăng ký
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, com.foodapp.activities.common.SignUpActivity.class);
            startActivity(intent);
        });
    }

    // Kiểm tra đăng nhập
    private void checkLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showToast(getString(R.string.empty_fields));
            return;
        }

        showLoading();
        User isLogin = userDao.checkLogin(username, password);
        if (isLogin != null) {
            // Lưu thông tin đăng nhập
            prefsManager.saveLoginDetails(username, password, rememberCheckbox.isChecked());

            // Lưu thông tin chi tiết người dùng
            prefsManager.saveUserDetails(
                    isLogin.getMaND(),
                    isLogin.getHoTen(),
                    isLogin.getEmail(),
                    isLogin.getSdt(),
                    isLogin.getLoaiTaiKhoan()
            );

            hideLoading();
            showToast(getString(R.string.login_success));

            // Chuyển đến HomeActivity
            Intent intent = new Intent(LoginActivity.this, com.foodapp.activities.common.HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            hideLoading();
            showToast(getString(R.string.login_failed));
        }
    }

    // Phương thức để ẩn bàn phím
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Thiết lập sự kiện ẩn bàn phím khi nhấn vào bất kỳ vị trí nào ngoài EditText
    public void setupUI(View view) {
        // Nếu không phải là EditText thì thiết lập sự kiện onClick
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard();
                    return false;
                }
            });
        }

        // Nếu là ViewGroup thì đệ quy cho các view con
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}