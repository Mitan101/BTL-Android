package com.foodapp.activities.common;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import com.foodapp.R;
import com.foodapp.database.dao.UserDao;
import com.foodapp.models.User;
import com.foodapp.utils.AppUtils;
import com.foodapp.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;

public class UserProfileActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextInputEditText etUsername, etFullName, etEmail, etPhone, etYearOfBirth;
    private Button btnUpdateProfile;
    private UserDao userDao;
    private String userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Lấy userId từ SharedPreferencesManager
        userId = getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            showToast("Không tìm thấy thông tin người dùng");
            finish();
            return;
        }

        // Ánh xạ view
        toolbar = findViewById(R.id.toolbarUserProfile);
        etUsername = findViewById(R.id.etProfileUsername);
        etFullName = findViewById(R.id.etProfileFullName);
        etEmail = findViewById(R.id.etProfileEmail);
        etPhone = findViewById(R.id.etProfilePhone);
        etYearOfBirth = findViewById(R.id.etProfileYearOfBirth);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        // Cấu hình toolbar
        toolbar.setTitle(getString(R.string.profile));
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Khởi tạo DAO
        userDao = new UserDao(this);

        // Lấy thông tin người dùng
        loadUserProfile();

        // Xử lý sự kiện cập nhật thông tin
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void loadUserProfile() {
        user = userDao.getById(userId);
        if (user != null) {
            etUsername.setText(user.getMaND());
            etUsername.setEnabled(false); // Không cho phép thay đổi username
            etFullName.setText(user.getHoTen());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getSdt());
            etYearOfBirth.setText(user.getNamSinh());
        }
    }

    private void updateProfile() {
        if (user == null) return;

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String yearOfBirth = etYearOfBirth.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty()) {
            showToast(getString(R.string.empty_fields));
            return;
        }

        // Cập nhật thông tin người dùng
        user.setHoTen(fullName);
        user.setEmail(email);
        user.setSdt(phone);
        user.setNamSinh(yearOfBirth);

        int result = userDao.update(user);

        if (result > 0) {
            // Lưu thông tin người dùng vào SharedPreferences
            prefsManager.saveUserDetails(userId, fullName, email, phone, user.getLoaiTaiKhoan());

            AppUtils.showInfoDialog(this,
                    getString(R.string.success),
                    getString(R.string.update_profile_success));
            finish();
        } else {
            showToast(getString(R.string.update_profile_failed));
        }
    }
}