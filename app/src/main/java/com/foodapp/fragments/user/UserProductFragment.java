package com.foodapp.fragments.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.adapters.FoodAdapter;
import com.foodapp.database.dao.FoodDao;
import com.foodapp.models.Food;

import java.util.ArrayList;
import java.util.List;

public class UserProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private FoodDao foodDao;
    private List<Food> foodList;
    private List<Food> filteredList;
    private EditText edtSearch;
    private ImageButton btnSearch;
    private ProgressBar progressBar;
    private TextView tvEmptyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_product, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        edtSearch = view.findViewById(R.id.edtSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);

        // Thiết lập RecyclerView với GridLayoutManager (2 cột)
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Khởi tạo danh sách và adapter
        foodList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new FoodAdapter(getContext(), filteredList);
        recyclerView.setAdapter(adapter);

        // Thiết lập sự kiện tìm kiếm
        setupSearchFunctionality();

        // Lấy dữ liệu từ database
        loadData();

        return view;
    }

    private void setupSearchFunctionality() {
        // Sự kiện khi click vào nút tìm kiếm
        btnSearch.setOnClickListener(v -> {
            filterFood(edtSearch.getText().toString());
        });

        // Sự kiện khi nhấn Enter trên bàn phím
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterFood(edtSearch.getText().toString());
                return true;
            }
            return false;
        });

        // Sự kiện khi thay đổi text tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Không cần xử lý
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterFood(s.toString());
            }
        });
    }

    // Lọc danh sách món ăn theo keyword
    private void filterFood(String keyword) {
        filteredList.clear();

        if (keyword.isEmpty()) {
            // Nếu không có từ khóa, hiển thị tất cả món
            filteredList.addAll(foodList);
        } else {
            // Tìm kiếm theo tên món ăn, không phân biệt hoa thường
            String lowercaseKeyword = keyword.toLowerCase().trim();
            for (Food food : foodList) {
                if (food.getTenDoAn().toLowerCase().contains(lowercaseKeyword) ||
                        food.getThongTin().toLowerCase().contains(lowercaseKeyword)) {
                    filteredList.add(food);
                }
            }
        }
        // Cập nhật adapter
        adapter.updateData(filteredList);

        // Hiển thị thông báo nếu không có kết quả
        if (filteredList.isEmpty() && !foodList.isEmpty()) {
            tvEmptyList.setText(getString(R.string.no_results));
            tvEmptyList.setVisibility(View.VISIBLE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        // Hiển thị loading
        progressBar.setVisibility(View.VISIBLE);

        // Khởi tạo FoodDao để lấy dữ liệu từ database
        foodDao = new FoodDao(getContext());

        // Lấy tất cả món ăn
        foodList = foodDao.getAll();

        // Ban đầu, hiển thị tất cả món ăn
        filteredList.clear();
        filteredList.addAll(foodList);

        // Cập nhật adapter
        adapter.updateData(foodList);
        recyclerView.setAdapter(adapter);

        // Hiển thị thông báo nếu không có dữ liệu
        if (foodList.isEmpty()) {
            tvEmptyList.setText(getString(R.string.no_products));
            tvEmptyList.setVisibility(View.VISIBLE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
        }

        // Ẩn loading
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Làm mới dữ liệu khi fragment được hiển thị lại
        loadData();
    }
}