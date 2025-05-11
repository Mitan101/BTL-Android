package com.foodapp.fragments.chef;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.activities.OrderDetailActivity;
import com.foodapp.adapters.OrderAdapter;
import com.foodapp.database.dao.OrderDao;
import com.foodapp.fragments.BaseFragment;
import com.foodapp.interfaces.OnItemClickListener;
import com.foodapp.models.Order;
import com.foodapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ChefOrderFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private OrderDao orderDao;
    private List<Order> orderList;
    private TextView tvEmptyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chef_order, container, false);

        // Ánh xạ view
        recyclerView = view.findViewById(R.id.recyclerViewChefOrders);
        tvEmptyList = view.findViewById(R.id.tvEmptyListChef);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách đơn hàng và adapter
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(getContext(), orderList);
        recyclerView.setAdapter(adapter);

        // Thiết lập listener cho adapter
        setupAdapterListeners();

        return view;
    }

    @Override
    protected void initData() {
        // Khởi tạo OrderDao
        orderDao = new OrderDao(getContext());

        // Lấy danh sách đơn hàng
        loadPendingOrders();
    }

    private void setupAdapterListeners() {
        // Xử lý sự kiện click vào item
        adapter.setOnItemClickListener(new OnItemClickListener<Order>() {
            @Override
            public void onItemClick(Order order, int position) {
                // Chuyển sang Activity hiển thị chi tiết đơn hàng
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra(Constants.EXTRA_ORDER_ID, order.getMaHoaDon());
                startActivity(intent);
            }
        });

        // Xử lý sự kiện click vào nút action
        adapter.setOnActionButtonClickListener(new OrderAdapter.OnActionButtonClickListener() {
            @Override
            public void onActionButtonClick(Order order, int position) {
                updateOrderStatus(order);
            }
        });
    }

    private void loadPendingOrders() {
        showLoading();

        // Lấy tất cả đơn hàng từ database
        List<Order> allOrders = orderDao.getAll();

        // Lọc các đơn hàng đang chờ xử lý
        orderList.clear();
        for (Order order : allOrders) {
            // Chỉ hiển thị đơn hàng có trạng thái "Đang chờ xử lý" hoặc "Đang chế biến"
            String status = order.getTrangThai();
            if (status != null && (status.equals(Constants.STATUS_PENDING) || status.equals(Constants.STATUS_COOKING))) {
                orderList.add(order);
            }
        }

        // Hiển thị thông báo nếu danh sách rỗng
        if (orderList.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
        }

        // Cập nhật adapter
        adapter.updateData(orderList);

        hideLoading();
    }

    private void updateOrderStatus(Order order) {
        String currentStatus = order.getTrangThai();
        String newStatus;

        // Cập nhật trạng thái theo workflow
        if (currentStatus.equals(Constants.STATUS_PENDING)) {
            newStatus = Constants.STATUS_COOKING;
        } else if (currentStatus.equals(Constants.STATUS_COOKING)) {
            newStatus = Constants.STATUS_DELIVERING;
        } else {
            return;
        }

        // Cập nhật trạng thái trong database
        int result = orderDao.updateStatus(order.getMaHoaDon(), newStatus);

        if (result > 0) {
            showToast(getString(R.string.update_status_success));
            // Cập nhật lại danh sách
            loadPendingOrders();
        } else {
            showToast(getString(R.string.update_status_failed));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại danh sách mỗi khi fragment được hiển thị
        loadPendingOrders();
    }
}