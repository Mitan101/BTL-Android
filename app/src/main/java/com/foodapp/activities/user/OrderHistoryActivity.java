package com.foodapp.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.activities.common.BaseActivity;
import com.foodapp.adapters.OrderAdapter;
import com.foodapp.database.dao.OrderDao;
import com.foodapp.models.Order;
import com.foodapp.utils.Constants;
import com.foodapp.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private TextView tvEmptyOrders;
    private OrderDao orderDao;
    private List<Order> orderList;
    private SharedPreferencesManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Ánh xạ view
        toolbar = findViewById(R.id.toolbarOrderHistory);
        recyclerView = findViewById(R.id.recyclerViewOrderHistory);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrderHistory);

        // Cấu hình toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Lịch sử đơn hàng");

        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderList);
        recyclerView.setAdapter(adapter);

        orderDao = new OrderDao(this);
        prefsManager = new SharedPreferencesManager(this);

        adapter.setOnItemClickListener((order, position) -> {
            Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailActivity.class);
            intent.putExtra(Constants.EXTRA_ORDER_ID, order.getMaHoaDon());
            startActivity(intent);
        });

        // Tải dữ liệu đơn hàng
        loadOrderHistory();
    }

    private void loadOrderHistory() {
        showLoading();

        String idUser = getCurrentUserId();

        try {
            orderList = orderDao.getByUser(idUser);

            if (orderList.isEmpty()) {
                tvEmptyOrders.setVisibility(View.VISIBLE);
                tvEmptyOrders.setText("Bạn chưa có đơn hàng nào");
            } else {
                tvEmptyOrders.setVisibility(View.GONE);
                adapter.updateData(orderList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tvEmptyOrders.setVisibility(View.VISIBLE);
            tvEmptyOrders.setText("Lỗi khi tải đơn hàng: " + e.getMessage() + "\n\nQuét màn hình xuống để thử lại");
        }

        hideLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderHistory();
    }
}