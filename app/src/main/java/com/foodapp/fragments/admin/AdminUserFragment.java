package com.foodapp.fragments.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.adapters.admin.UserAdapter;
import com.foodapp.database.dao.UserDao;
import com.foodapp.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminUserFragment extends Fragment implements UserAdapter.UserListener {

    private RecyclerView recyclerViewUsers;
    private UserDao userDao;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FloatingActionButton fabAddUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user, container, false);

        // Initialize components
        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        userDao = new UserDao(getContext());
        userList = new ArrayList<>();

        userAdapter = new UserAdapter(getContext(), userList, this);
        recyclerViewUsers.setAdapter(userAdapter);

        // Setup FAB for adding new users
        fabAddUser = view.findViewById(R.id.fabAddUser);
        fabAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUserDialog();
            }
        });

        // Load user data
        loadUsers();

        return view;
    }

    private void loadUsers() {
        // Get all users from database
        userList = userDao.getAll();
        userAdapter.setUserList(userList);

        // Show message if no users found
        if (userList.isEmpty()) {
            Toast.makeText(getContext(), "Không có người dùng nào trong cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm người dùng");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_user, null);
        TextInputEditText editTextUsername = view.findViewById(R.id.etUsername);
        TextInputEditText editTextHoTen = view.findViewById(R.id.etFullName);
        TextInputEditText editTextEmail = view.findViewById(R.id.etEmail);
        TextInputEditText editTextSoDienThoai = view.findViewById(R.id.etPhone);
        TextInputEditText editTextNamSinh = view.findViewById(R.id.etBirthYear);
        TextInputEditText editTextPassword = view.findViewById(R.id.etPassword);
        RadioGroup rgUserType = view.findViewById(R.id.rgUserType);
        RadioButton rbCustomer = view.findViewById(R.id.rbCustomer);
        // RadioButton rbChef = view.findViewById(R.id.rbChef);

        builder.setView(view);

        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = editTextUsername.getText().toString().trim();
                String fullName = editTextHoTen.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String phone = editTextSoDienThoai.getText().toString().trim();
                String birthYear = editTextNamSinh.getText().toString().trim();

                if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userDao.checkID(username)) {
                    Toast.makeText(getContext(), "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User();
                user.setMaND(username);
                user.setHoTen(fullName);
                user.setEmail(email);
                user.setMatKhau(password);
                user.setSdt(phone);
                user.setNamSinh(birthYear);

                user.setLoaiTaiKhoan("user");

                long result = userDao.insert(user);

                if (result > 0) {
                    loadUsers();
                    Toast.makeText(getContext(), "Đã thêm người dùng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Thêm người dùng thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }

    @Override
    public void onUserClick(User user) {
        Toast.makeText(getContext(), "Đã chọn: " + user.getHoTen(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chỉnh sửa người dùng");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);
        TextInputEditText editTextHoTen = view.findViewById(R.id.etFullName);
        TextInputEditText editTextEmail = view.findViewById(R.id.etEmail);
        TextInputEditText editTextSoDienThoai = view.findViewById(R.id.etPhone);
        TextInputEditText editTextNamSinh = view.findViewById(R.id.etBirthYear);
        TextInputEditText editTextPassword = view.findViewById(R.id.etPassword);

        editTextHoTen.setText(user.getHoTen());
        editTextEmail.setText(user.getEmail());
        editTextSoDienThoai.setText(user.getSdt());
        editTextNamSinh.setText(user.getNamSinh());
        editTextPassword.setText(user.getMatKhau());

        builder.setView(view);

        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.setHoTen(editTextHoTen.getText().toString());
                user.setEmail(editTextEmail.getText().toString());
                user.setSdt(editTextSoDienThoai.getText().toString());
                user.setNamSinh(editTextNamSinh.getText().toString());
                user.setMatKhau(editTextPassword.getText().toString());

                // Update user in database
                int result = userDao.update(user);

                if (result > 0) {
                    loadUsers();
                    Toast.makeText(getContext(), "Đã chỉnh sửa người dùng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Chỉnh sửa người dùng thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }

    @Override
    public void onDeleteClick(User user) {
        showDeleteConfirmDialog(user);
    }

    private void showDeleteConfirmDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa người dùng");
        builder.setMessage("Bạn có chắc chắn muốn xóa người dùng " + user.getHoTen() + "?");

        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Don't allow deletion of admin users
                if (user.getLoaiTaiKhoan().equals("admin")) {
                    Toast.makeText(getContext(), "Không thể xóa tài khoản admin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Delete user from database
                int result = userDao.delete(user.getMaND());

                if (result > 0) {
                    loadUsers();
                    Toast.makeText(getContext(), "Đã xóa người dùng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Xóa người dùng thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }
}