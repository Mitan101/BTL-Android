package com.foodapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.database.dao.OrderDao;
import com.foodapp.models.Order;
import com.foodapp.utils.Constants;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderDetailActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal;
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;
    private TextView tvOrderItems;
    private Button btnUpdateStatus, btnCancel;

    private OrderDao orderDao;
    private Order order;
    private int orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Nhận ID đơn hàng từ intent
        orderId = getIntent().getIntExtra(Constants.EXTRA_ORDER_ID, 0);
        if (orderId <= 0) {
            showToast("Không tìm thấy thông tin đơn hàng");
            finish();
            return;
        }

        // Ánh xạ view
        toolbar = findViewById(R.id.toolbarOrderDetail);
        tvOrderId = findViewById(R.id.tvOrderDetailId);
        tvOrderDate = findViewById(R.id.tvOrderDetailDate);
        tvOrderStatus = findViewById(R.id.tvOrderDetailStatus);
        tvOrderTotal = findViewById(R.id.tvOrderDetailTotal);
        tvCustomerName = findViewById(R.id.tvOrderDetailCustomerName);
        tvCustomerPhone = findViewById(R.id.tvOrderDetailCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvOrderDetailCustomerAddress);
        tvOrderItems = findViewById(R.id.tvOrderDetailItems);
        btnUpdateStatus = findViewById(R.id.btnOrderDetailUpdateStatus);
        btnCancel = findViewById(R.id.btnOrderDetailCancel);

        // Cấu hình toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.order_detail);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Khởi tạo DAO và lấy dữ liệu
        orderDao = new OrderDao(this);
        loadOrderDetails();

        // Thiết lập sự kiện cho các nút
        setupButtonListeners();
    }

    private void loadOrderDetails() {
        order = orderDao.getById(String.valueOf(orderId));
        if (order == null) {
            showToast("Không tìm thấy thông tin đơn hàng");
            finish();
            return;
        }

        // Hiển thị thông tin đơn hàng
        tvOrderId.setText(String.format("#%d", order.getMaHoaDon()));
        tvOrderDate.setText(order.getNgayDat());
        tvOrderStatus.setText(order.getTrangThai());

        // Định dạng giá tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedAmount = formatter.format(order.getTongTien());
        tvOrderTotal.setText(formattedAmount);

        // Thông tin khách hàng
        tvCustomerName.setText(String.format(getString(R.string.customer_name), order.getHoTen()));
        tvCustomerPhone.setText(String.format(getString(R.string.customer_phone), order.getSdt()));
        tvCustomerAddress.setText(String.format(getString(R.string.customer_address), order.getDiaChi()));

        // Thực đơn đã đặt
        tvOrderItems.setText(order.getThucDon());

        // Hiển thị hoặc ẩn nút cập nhật trạng thái
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        String status = order.getTrangThai();

        if (status.equals(Constants.STATUS_PENDING)) {
            btnUpdateStatus.setVisibility(View.VISIBLE);
            btnUpdateStatus.setText(R.string.start_cooking);
            btnCancel.setVisibility(View.VISIBLE);
        } else if (status.equals(Constants.STATUS_COOKING)) {
            btnUpdateStatus.setVisibility(View.VISIBLE);
            btnUpdateStatus.setText(R.string.complete_order);
            btnCancel.setVisibility(View.GONE);
        } else {
            btnUpdateStatus.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }
    }

    private void setupButtonListeners() {
        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
            }
        });
    }

    private void updateOrderStatus() {
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
            // Cập nhật lại thông tin đơn hàng
            loadOrderDetails();
        } else {
            showToast(getString(R.string.update_status_failed));
        }
    }

    private void cancelOrder() {
        // Cập nhật trạng thái trong database
        int result = orderDao.updateStatus(order.getMaHoaDon(), Constants.STATUS_CANCELLED);

        if (result > 0) {
            showToast(getString(R.string.update_status_success));
            // Cập nhật lại thông tin đơn hàng
            loadOrderDetails();
        } else {
            showToast(getString(R.string.update_status_failed));
        }
    }
}