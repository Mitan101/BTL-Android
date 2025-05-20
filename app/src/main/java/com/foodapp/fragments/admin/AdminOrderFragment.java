package com.foodapp.fragments.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.activities.user.OrderDetailActivity;
import com.foodapp.adapters.OrderAdapter;
import com.foodapp.database.dao.OrderDao;
import com.foodapp.models.Order;
import com.foodapp.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderFragment extends Fragment {

    private RecyclerView recyclerViewOrders;
    private TextView tvEmptyOrders;
    private OrderAdapter adapter;
    private OrderDao orderDao;
    private List<Order> orderList;
    private FloatingActionButton fabRefresh;
    private ProgressBar loadingIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_order, container, false);

        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        tvEmptyOrders = view.findViewById(R.id.tvEmptyOrders);
        fabRefresh = view.findViewById(R.id.fabRefresh);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);

        Log.d("AdminOrderFragment", "recyclerViewOrders is " + (recyclerViewOrders == null ? "NULL" : "not null"));
        Log.d("AdminOrderFragment", "tvEmptyOrders is " + (tvEmptyOrders == null ? "NULL" : "not null"));
        Log.d("AdminOrderFragment", "fabRefresh is " + (fabRefresh == null ? "NULL" : "not null"));
        Log.d("AdminOrderFragment", "loadingIndicator is " + (loadingIndicator == null ? "NULL" : "not null"));

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(getContext(), orderList);
        recyclerViewOrders.setAdapter(adapter);

        orderDao = new OrderDao(getContext());

        adapter.setOnItemClickListener((order, position) -> {
            Intent intent = new Intent(getContext(), OrderDetailActivity.class);
            intent.putExtra(Constants.EXTRA_ORDER_ID, order.getMaHoaDon());
            startActivity(intent);
        });

        fabRefresh.setOnClickListener(v -> loadOrders());

        loadOrders();

        return view;
    }

    private void loadOrders() {
        loadingIndicator.setVisibility(View.VISIBLE);
        recyclerViewOrders.setVisibility(View.GONE);

        new Handler().postDelayed(() -> loadOrdersFromDatabase(), 500);
    }

    private void loadOrdersFromDatabase() {
        try {
            List<Order> allOrders = orderDao.getAll();

            orderList.clear();
            orderList.addAll(allOrders);

            if (orderList.isEmpty()) {
                tvEmptyOrders.setVisibility(View.VISIBLE);
                recyclerViewOrders.setVisibility(View.GONE);
            } else {
                tvEmptyOrders.setVisibility(View.GONE);
                recyclerViewOrders.setVisibility(View.VISIBLE);
                adapter.updateData(allOrders);
            }

        } catch (Exception e) {
            Log.e("AdminOrderFragment", "Error loading orders", e);
            tvEmptyOrders.setVisibility(View.VISIBLE);
            recyclerViewOrders.setVisibility(View.GONE);
            tvEmptyOrders.setText("Lỗi khi tải danh sách đơn hàng: " + e.getMessage());
        } finally {
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }
}