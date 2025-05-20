package com.foodapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.adapters.FoodAdapter;
import com.foodapp.database.dao.FoodDao;
import com.foodapp.models.Food;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private FoodDao foodDao;
    private List<Food> foodList;
    private TextView tvEmptyList;
    private Toolbar toolbar;

    private int categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_products);

        // Nhận dữ liệu từ Intent
        categoryId = getIntent().getIntExtra("category_id", 0);
        categoryName = getIntent().getStringExtra("category_name");

        // Ánh xạ view
        recyclerView = findViewById(R.id.recyclerViewCategoryProducts);
        tvEmptyList = findViewById(R.id.tvEmptyListCategory);
        toolbar = findViewById(R.id.toolbarCategory);

        // Cấu hình toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(categoryName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Thiết lập RecyclerView với GridLayoutManager (2 cột)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Khởi tạo danh sách và adapter
        foodList = new ArrayList<>();
        adapter = new FoodAdapter(this, foodList);
        recyclerView.setAdapter(adapter);

        // Khởi tạo DAO
        foodDao = new FoodDao(this);

        // Lấy dữ liệu từ database
        loadFoodsByCategory();
    }

    private void loadFoodsByCategory() {
        if (categoryId > 0) {
            // Lấy tất cả món ăn thuộc danh mục
            foodList = foodDao.getFoodsByCategory(String.valueOf(categoryId));

            // Hiển thị thông báo nếu danh sách rỗng
            if (foodList.isEmpty()) {
                tvEmptyList.setVisibility(View.VISIBLE);
            } else {
                tvEmptyList.setVisibility(View.GONE);
            }

            // Cập nhật adapter
            adapter.updateData(foodList);
        }
    }
}