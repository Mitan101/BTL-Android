package com.foodapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.foodapp.R;
import com.foodapp.database.dao.CartDao;
import com.foodapp.database.dao.OrderDao;
import com.foodapp.models.CartItem;
import com.foodapp.models.Order;
import com.foodapp.utils.AppUtils;
import com.foodapp.utils.Constants;

import java.util.List;
import java.util.UUID;

public class CartCheckoutActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText edtName, edtPhone, edtAddress;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCash, rbCredit;
    private TextView tvTotalPrice;
    private Button btnConfirm, btnCancel;

    private CartDao cartDao;
    private OrderDao orderDao;
    private List<CartItem> cartItems;
    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_checkout);

        // Ánh xạ view
        toolbar = findViewById(R.id.toolbarCheckout);
        edtName = findViewById(R.id.edtCheckoutName);
        edtPhone = findViewById(R.id.edtCheckoutPhone);
        edtAddress = findViewById(R.id.edtCheckoutAddress);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCash = findViewById(R.id.rbCash);
        rbCredit = findViewById(R.id.rbCredit);
        tvTotalPrice = findViewById(R.id.tvCheckoutTotal);
        btnConfirm = findViewById(R.id.btnCheckoutConfirm);
        btnCancel = findViewById(R.id.btnCheckoutCancel);

        // Cấu hình toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.checkout);

        toolbar.setNavigationOnClickListener(view -> finish());

        // Khởi tạo DAO
        cartDao = new CartDao(this);
        orderDao = new OrderDao(this);

        // Thiết lập sự kiện cho nút
        btnConfirm.setOnClickListener(view -> placeOrder());
        btnCancel.setOnClickListener(view -> finish());

        // Lấy dữ liệu giỏ hàng
        loadCartItems();
    }

    private void loadCartItems() {
        showLoading();

        // Lấy danh sách sản phẩm trong giỏ hàng
        cartItems = cartDao.getAll();

        if (cartItems.isEmpty()) {
            showToast(getString(R.string.cart_empty));
            finish();
            return;
        }

        // Tính tổng tiền
        totalPrice = cartDao.getTotalPrice();

        // Hiển thị tổng tiền
        tvTotalPrice.setText(getString(R.string.total_price, AppUtils.formatCurrency(totalPrice)));

        // Đổ dữ liệu người dùng nếu đã đăng nhập
        if (isLoggedIn()) {
            edtName.setText(prefsManager.getFullName());
            edtPhone.setText(prefsManager.getPhone());
        }

        hideLoading();
    }

    private void placeOrder() {
        // Kiểm tra dữ liệu nhập vào
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            showToast(getString(R.string.empty_fields));
            return;
        }

        // Lấy phương thức thanh toán
        String paymentMethod = rbCash.isChecked() ? Constants.PAYMENT_CASH : Constants.PAYMENT_CREDIT_CARD;

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setMaHoaDon(generateOrderId());
        order.setEmail(prefsManager.getEmail());
        order.setHoTen(name);
        order.setSdt(phone);
        order.setDiaChi(address);
        order.setThucDon(generateOrderItems());
        order.setNgayDat(AppUtils.getCurrentDateTime());
        order.setTongTien(totalPrice);
        order.setThanhToan(paymentMethod);
        order.setTrangThai(Constants.STATUS_PENDING);

        long result = orderDao.insert(order);

        if (result > 0) {
            cartDao.deleteAll();

            AppUtils.showInfoDialog(this,
                    getString(R.string.success),
                    getString(R.string.order_success));
            finish();
        } else {
            showToast(getString(R.string.error));
        }
    }

    private int generateOrderId() {
        // Tạo ID ngẫu nhiên cho đơn hàng
        return Math.abs(UUID.randomUUID().hashCode());
    }

    private String generateOrderItems() {
        // Tạo chuỗi mô tả các món ăn trong đơn hàng
        StringBuilder sb = new StringBuilder();

        for (CartItem item : cartItems) {
            sb.append(item.getSoLuong())
                    .append(" x ")
                    .append(item.getTenSanPham());

            if (item.getTenDoAnPhu() != null && !item.getTenDoAnPhu().isEmpty()) {
                sb.append(" + ").append(item.getTenDoAnPhu());
            }

            sb.append(" (").append(AppUtils.formatCurrency(item.getGiaSanPham())).append(")\n");
        }

        return sb.toString();
    }
}