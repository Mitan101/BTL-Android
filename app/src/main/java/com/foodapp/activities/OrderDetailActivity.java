package com.foodapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.foodapp.R;
import com.foodapp.database.dao.OrderDao;
import com.foodapp.models.Order;
import com.foodapp.utils.Constants;
import com.foodapp.utils.SharedPreferencesManager;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderDetailActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView tvOrderId, tvOrderDate, tvOrderTotal;
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;
    private TextView tvOrderItems;

    private OrderDao orderDao;
    private Order order;
    private int orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Get order ID from intent
        orderId = getIntent().getIntExtra(Constants.EXTRA_ORDER_ID, -1);
        Log.d("OrderDetailActivity", "Received order ID: " + orderId);
        if (orderId == -1) {
            showToast("Không tìm thấy thông tin đơn hàng");
            finish();
            return;
        }

        // Ánh xạ view
        toolbar = findViewById(R.id.toolbarOrderDetail);
        tvOrderId = findViewById(R.id.tvOrderDetailId);
        tvOrderDate = findViewById(R.id.tvOrderDetailDate);
        tvOrderTotal = findViewById(R.id.tvOrderDetailTotal);
        tvCustomerName = findViewById(R.id.tvOrderDetailCustomerName);
        tvCustomerPhone = findViewById(R.id.tvOrderDetailCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvOrderDetailCustomerAddress);
        tvOrderItems = findViewById(R.id.tvOrderDetailItems);

        // Cấu hình toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chi tiết đơn hàng đã thanh toán");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Khởi tạo DAO và lấy dữ liệu
        orderDao = new OrderDao(this);
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        order = orderDao.getById(String.valueOf(orderId));
        Log.d("OrderDetailActivity", "Loading order details for ID: " + orderId + ", result: " + (order != null));
        if (order == null) {
            showToast("Không tìm thấy thông tin đơn hàng");
            finish();
            return;
        }

        // Hiển thị thông tin đơn hàng
        tvOrderId.setText(String.format("#%d", order.getMaHoaDon()));
        tvOrderDate.setText(order.getNgayDat());

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
    }
}