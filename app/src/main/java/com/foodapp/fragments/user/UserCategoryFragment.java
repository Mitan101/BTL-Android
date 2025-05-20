package com.foodapp.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.adapters.CategoryAdapter;
import com.foodapp.database.dao.CategoryDao;
import com.foodapp.models.Category;

import java.util.ArrayList;
import java.util.List;

public class UserCategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private CategoryDao categoryDao;
    private List<Category> categoryList;
    private TextView tvEmptyCategoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_category, container, false);

        // Ánh xạ view
        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        tvEmptyCategoryList = view.findViewById(R.id.tvEmptyCategoryList);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách và adapter
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(getContext(), categoryList);
        recyclerView.setAdapter(adapter);

        // Lấy dữ liệu từ database
        loadCategories();

        return view;
    }

    private void loadCategories() {
        // Khởi tạo CategoryDao để lấy dữ liệu
        categoryDao = new CategoryDao(getContext());

        // Lấy tất cả danh mục
        categoryList = categoryDao.getAll();

        // Cập nhật adapter
        adapter.updateData(categoryList);

        // Hiển thị thông báo nếu không có dữ liệu
        if (categoryList == null || categoryList.isEmpty()) {
            tvEmptyCategoryList.setVisibility(View.VISIBLE);
        } else {
            tvEmptyCategoryList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Làm mới dữ liệu khi fragment được hiển thị lại
        loadCategories();
    }
}