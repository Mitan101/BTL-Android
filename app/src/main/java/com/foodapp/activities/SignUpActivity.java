package com.foodapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.foodapp.R;
import com.foodapp.database.dao.UserDao;
import com.foodapp.models.User;
import com.foodapp.utils.AppUtils;
import com.foodapp.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextInputEditText etUsername, etPassword, etConfirmPassword;
    private TextInputEditText etFullName, etEmail, etPhone, etYearOfBirth;
    private Button btnSignUp;
    private TextView tvLogin;

    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Ánh xạ view
        toolbar = findViewById(R.id.toolbarSignUp);
        etUsername = findViewById(R.id.etSignUpUsername);
        etPassword = findViewById(R.id.etSignUpPassword);
        etConfirmPassword = findViewById(R.id.etSignUpConfirmPassword);
        etFullName = findViewById(R.id.etSignUpFullName);
        etEmail = findViewById(R.id.etSignUpEmail);
        etPhone = findViewById(R.id.etSignUpPhone);
        etYearOfBirth = findViewById(R.id.etSignUpYearOfBirth);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLoginLink);

        // Cấu hình toolbar
        toolbar.setTitle(getString(R.string.signup));
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Khởi tạo DAO
        userDao = new UserDao(this);

        // Thiết lập sự kiện cho nút đăng ký
        btnSignUp.setOnClickListener(v -> registerUser());

        // Thiết lập sự kiện cho link đăng nhập
        tvLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        // Lấy dữ liệu đầu vào
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String yearOfBirth = etYearOfBirth.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(fullName) ||
                TextUtils.isEmpty(email)) {
            showToast(getString(R.string.empty_fields));
            return;
        }

        // Kiểm tra mật khẩu xác nhận
        if (!password.equals(confirmPassword)) {
            showToast(getString(R.string.password_not_match));
            return;
        }

        // Kiểm tra xem username đã tồn tại chưa
        if (userDao.checkID(username)) {
            showToast(getString(R.string.username_exists));
            return;
        }

        // Tạo đối tượng User mới
        User user = new User();
        user.setMaND(username);
        user.setHoTen(fullName);
        user.setMatKhau(password);
        user.setEmail(email);
        user.setNamSinh(yearOfBirth);
        user.setSdt(phone);
        user.setLoaiTaiKhoan(Constants.USER_TYPE_USER);

        long result = userDao.insert(user);

        if (result > 0) {
            prefsManager.saveLoginDetails(username, password, false);
            prefsManager.saveUserDetails(fullName, email, phone, Constants.USER_TYPE_USER);

            AppUtils.showInfoDialog(this,
                    getString(R.string.success),
                    getString(R.string.signup_success));
            finish();
        } else {
            showToast(getString(R.string.signup_failed));
        }
    }
}