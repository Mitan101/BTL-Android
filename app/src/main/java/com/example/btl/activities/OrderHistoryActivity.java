package com.example.btl.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.adapters.OrderAdapter;
import com.example.btl.database.dao.OrderDao;
import com.example.btl.models.Order;
import com.example.btl.utils.Constants;

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
        getSupportActionBar().setTitle("Lịch sử đơn hàng");

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
            Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailActivity.class);
            intent.putExtra(Constants.EXTRA_ORDER_ID, order.getMaHoaDon());
            startActivity(intent);
        });

        // Tải dữ liệu đơn hàng
        loadOrderHistory();
    }

    private void loadOrderHistory() {
        showLoading();
        orderList = orderDao.getOrdersByUser(userId);

        if (orderList.isEmpty()) {
            tvEmptyOrders.setVisibility(View.VISIBLE);
            tvEmptyOrders.setText("Bạn chưa có đơn hàng nào");
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