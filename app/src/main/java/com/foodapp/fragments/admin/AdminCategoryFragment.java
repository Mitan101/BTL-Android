package com.foodapp.fragments.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.adapters.AdminCategoryAdapter;
import com.foodapp.database.dao.CategoryDao;
import com.foodapp.models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdminCategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddCategory;
    private AdminCategoryAdapter adapter;
    private CategoryDao categoryDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_category, container, false);

        // Ánh xạ các view
        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        fabAddCategory = view.findViewById(R.id.fabAddCategory);

        // Khởi tạo DAO
        categoryDao = new CategoryDao(getContext());

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminCategoryAdapter(getContext(), null);
        recyclerView.setAdapter(adapter);

        // Lấy danh sách loại sản phẩm
        loadCategories();

        // Thiết lập sự kiện cho nút thêm
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCategoryDialog();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories(); // Tải lại dữ liệu khi quay lại fragment
    }

    private void loadCategories() {
        // Lấy danh sách loại sản phẩm từ database
        List<Category> categories = categoryDao.getAll();
        adapter.updateData(categories);

        // Hiển thị thông báo nếu không có dữ liệu
        if (categories == null || categories.isEmpty()) {
            Toast.makeText(getContext(), "Không có loại sản phẩm nào", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm loại sản phẩm mới");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        final EditText etName = dialogView.findViewById(R.id.etCategoryName);

        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etName.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập tên loại sản phẩm", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thêm loại sản phẩm mới vào database
                Category newCategory = new Category();
                newCategory.setTenLoai(name);
                long result = categoryDao.insert(newCategory);

                if (result > 0) {
                    loadCategories();
                    Toast.makeText(getContext(), "Đã thêm loại sản phẩm mới", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Thêm loại sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }
}