package com.foodapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.adapters.OrderAdapter;
import com.foodapp.database.dao.OrderDao;
import com.foodapp.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private TextView tvEmptyOrders;
    private OrderDao orderDao;
    private String userId;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Lấy userId từ Intent
        userId = getIntent().getStringExtra("ma");
        if (userId == null) {
            userId = getCurrentUsername();
        }

        // Ánh xạ view
        toolbar = findViewById(R.id.toolbarOrderHistory);
        recyclerView = findViewById(R.id.recyclerViewOrderHistory);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrderHistory);

        // Cấu hình toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.menu_orders);

        toolbar.setNavigationOnClickListener(v -> finish());

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderList);
        recyclerView.setAdapter(adapter);

        // Khởi tạo DAO
        orderDao = new OrderDao(this);

        // Xử lý sự kiện click vào đơn hàng
        adapter.setOnItemClickListener((order, position) -> {
            // Mở màn hình chi tiết đơn hàng
        });

        // Tải dữ liệu đơn hàng
        loadOrderHistory();
    }

    private void loadOrderHistory() {
        showLoading();
        orderList = orderDao.getOrdersByUser(userId);

        if (orderList.isEmpty()) {
            tvEmptyOrders.setVisibility(View.VISIBLE);
        } else {
            tvEmptyOrders.setVisibility(View.GONE);
            adapter.updateData(orderList);
        }
        hideLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderHistory();
    }
}