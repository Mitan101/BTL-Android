package com.foodapp.fragments.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foodapp.R;
import com.foodapp.database.dao.UserDao;
import com.foodapp.models.User;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordFragment extends Fragment {

    private TextInputEditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChangePassword;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        // Ánh xạ view
        edtCurrentPassword = view.findViewById(R.id.edtCurrentPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        // Khởi tạo DAO
        userDao = new UserDao(getContext());

        // Thiết lập sự kiện cho nút đổi mật khẩu
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        return view;
    }

    private void changePassword() {
        String currentPassword = edtCurrentPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Kiểm tra dữ liệu nhập vào
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getContext(), R.string.password_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thông tin người dùng hiện tại
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "");

        // Kiểm tra mật khẩu hiện tại
        if (userDao.checkLogin(username, currentPassword) != null) {
            Toast.makeText(getContext(), R.string.current_password_incorrect, Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật mật khẩu mới
        User user = userDao.getById(username);
        user.setMatKhau(newPassword);

        int result = userDao.updatePassword(user);
        if (result > 0) {
            Toast.makeText(getContext(), R.string.change_password_success, Toast.LENGTH_SHORT).show();
            // Xóa dữ liệu đã nhập
            edtCurrentPassword.setText("");
            edtNewPassword.setText("");
            edtConfirmPassword.setText("");
        } else {
            Toast.makeText(getContext(), R.string.change_password_failed, Toast.LENGTH_SHORT).show();
        }
    }
}