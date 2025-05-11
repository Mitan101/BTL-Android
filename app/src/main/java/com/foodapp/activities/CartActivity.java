package com.foodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.adapters.CartAdapter;
import com.foodapp.database.dao.CartDao;
import com.foodapp.models.CartItem;
import com.foodapp.utils.Constants;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends BaseActivity implements CartAdapter.OnCartItemActionListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView tvTotalPrice, tvEmptyCart;
    private Button btnCheckout, btnContinueShopping;
    private LinearLayout layoutEmptyCart, layoutCartContent;

    private CartDao cartDao;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Ánh xạ view
        toolbar = findViewById(R.id.toolbarCart);
        recyclerView = findViewById(R.id.recyclerViewCart);
        tvTotalPrice = findViewById(R.id.tvCartTotal);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        btnCheckout = findViewById(R.id.btnCartCheckout);
        btnContinueShopping = findViewById(R.id.btnCartContinueShopping);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        layoutCartContent = findViewById(R.id.layoutCartContent);

        // Cấu hình toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.cart);

        toolbar.setNavigationOnClickListener(view -> finish());

        // Khởi tạo RecyclerView và Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        adapter = new CartAdapter(this, cartItems);
        adapter.setOnCartItemActionListener(this);
        recyclerView.setAdapter(adapter);

        // Khởi tạo DAO
        cartDao = new CartDao(this);

        // Load dữ liệu giỏ hàng
        loadCartItems();

        // Thiết lập sự kiện cho nút
        btnCheckout.setOnClickListener(view -> {
            if (!cartItems.isEmpty()) {
                startActivity(new Intent(CartActivity.this, CartCheckoutActivity.class));
            }
        });

        btnContinueShopping.setOnClickListener(view -> finish());
    }

    private void loadCartItems() {
        showLoading();

        // Lấy danh sách sản phẩm trong giỏ hàng
        cartItems = cartDao.getAll();

        // Hiển thị layout phù hợp
        if (cartItems.isEmpty()) {
            layoutCartContent.setVisibility(View.GONE);
            layoutEmptyCart.setVisibility(View.VISIBLE);
        } else {
            layoutCartContent.setVisibility(View.VISIBLE);
            layoutEmptyCart.setVisibility(View.GONE);

            // Cập nhật adapter
            adapter.updateData(cartItems);

            // Cập nhật tổng tiền
            updateTotalPrice();
        }

        hideLoading();
    }

    private void updateTotalPrice() {
        // Tính tổng tiền
        double totalPrice = cartDao.getTotalPrice();

        // Định dạng giá tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(totalPrice);

        // Hiển thị tổng tiền
        tvTotalPrice.setText(getString(R.string.total_price, formattedPrice));
    }

    @Override
    public void onIncreaseQuantity(CartItem item, int position) {
        // Tăng số lượng
        int newQuantity = item.getSoLuong() + 1;
        item.setSoLuong(newQuantity);

        // Cập nhật database
        cartDao.update(item);

        // Cập nhật UI
        adapter.notifyItemChanged(position);
        updateTotalPrice();
    }

    @Override
    public void onDecreaseQuantity(CartItem item, int position) {
        // Giảm số lượng nếu số lượng > 1
        if (item.getSoLuong() > 1) {
            int newQuantity = item.getSoLuong() - 1;
            item.setSoLuong(newQuantity);

            // Cập nhật database
            cartDao.update(item);

            // Cập nhật UI
            adapter.notifyItemChanged(position);
            updateTotalPrice();
        }
    }

    @Override
    public void onRemoveItem(CartItem item, int position) {
        // Xóa khỏi database
        cartDao.delete(String.valueOf(item.getMaSanPham()));

        // Xóa khỏi list
        cartItems.remove(position);

        // Cập nhật adapter
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, cartItems.size());

        // Cập nhật tổng tiền
        updateTotalPrice();

        // Nếu không còn sản phẩm nào, hiển thị layout giỏ hàng trống
        if (cartItems.isEmpty()) {
            layoutCartContent.setVisibility(View.GONE);
            layoutEmptyCart.setVisibility(View.VISIBLE);
        }

        showToast(getString(R.string.item_removed));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại giỏ hàng khi quay lại màn hình
        loadCartItems();
    }
}