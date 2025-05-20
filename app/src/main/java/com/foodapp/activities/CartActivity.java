package com.foodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    private TextView tvTotalPrice, tvEmptyCart, tvSelectedCount, tvSelectedTotal;
    private Button btnCheckout, btnContinueShopping;
    private LinearLayout layoutEmptyCart, layoutCartContent;
    private CheckBox cbSelectAll;

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
        cbSelectAll = findViewById(R.id.cbSelectAll);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        tvSelectedTotal = findViewById(R.id.tvSelectedTotal);

        // Cấu hình toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.cart);

        toolbar.setNavigationOnClickListener(view -> finish());

        // Khởi tạo RecyclerView và Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        adapter = new CartAdapter(this, new ArrayList<>());
        adapter.setOnCartItemActionListener(this);
        recyclerView.setAdapter(adapter);

        // Khởi tạo DAO
        cartDao = new CartDao(this);

        // Load dữ liệu giỏ hàng
        loadCartItems();

        // Thiết lập sự kiện cho nút
        btnCheckout.setOnClickListener(view -> {
            if (!cartItems.isEmpty()) {
                List<CartItem> selectedItems = getSelectedItems();
                if (selectedItems.isEmpty()) {
                    showToast(getString(R.string.please_select_items));
                    return;
                }

                // Get IDs of selected items to pass to the next activity
                ArrayList<Integer> selectedIds = new ArrayList<>();
                for (CartItem item : selectedItems) {
                    selectedIds.add(item.getMaSanPham());
                }

                // Truyền danh sách món đã chọn sang CartCheckoutActivity
                Intent intent = new Intent(CartActivity.this, CartCheckoutActivity.class);
                intent.putExtra(Constants.EXTRA_SELECTED_ITEMS_ONLY, true);
                intent.putIntegerArrayListExtra(Constants.EXTRA_SELECTED_ITEM_IDS, selectedIds);
                startActivity(intent);
            }
        });

        btnContinueShopping.setOnClickListener(view -> finish());

        // Thiết lập sự kiện cho checkbox chọn tất cả
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Chọn/bỏ chọn tất cả các item
            for (CartItem item : cartItems) {
                item.setSelected(isChecked);
            }
            adapter.notifyDataSetChanged();
            updateSelectedInfo();
        });
    }

    private void loadCartItems() {
        showLoading();

        try {
            // Create a new list to store items
            List<CartItem> freshCartItems = cartDao.getAll();

            // Reset current list reference to avoid issues
            cartItems = new ArrayList<>();

            // Add all items to the fresh list
            if (freshCartItems != null && !freshCartItems.isEmpty()) {
                cartItems.addAll(freshCartItems);
            }

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
                updateSelectedInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            layoutCartContent.setVisibility(View.GONE);
            layoutEmptyCart.setVisibility(View.VISIBLE);
        } finally {
            hideLoading();
        }
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

    // Cập nhật thông tin về các món đã chọn
    private void updateSelectedInfo() {
        List<CartItem> selectedItems = getSelectedItems();
        int selectedCount = selectedItems.size();

        // Hiển thị số lượng món được chọn
        tvSelectedCount.setText(getString(R.string.selected_count, selectedCount));

        // Tính tổng tiền của các món được chọn
        double selectedTotal = 0;
        for (CartItem item : selectedItems) {
            selectedTotal += item.getTotalPrice();
        }

        // Định dạng giá tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(selectedTotal);

        // Hiển thị tổng tiền của các món được chọn
        tvSelectedTotal.setText(getString(R.string.selected_total, formattedPrice));

        // Cập nhật nút thanh toán
        btnCheckout.setEnabled(selectedCount > 0);
        btnCheckout.setText(selectedCount > 0 ?
                getString(R.string.checkout_selected, selectedCount) : getString(R.string.checkout));
    }

    // Lấy danh sách các món đã được chọn
    private List<CartItem> getSelectedItems() {
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    @Override
    public void onIncreaseQuantity(CartItem item, int position) {
        try {
            // Show loading indicator
            showLoading("Đang cập nhật số lượng...");

            // Tăng số lượng
            int newQuantity = item.getSoLuong() + 1;
            item.setSoLuong(newQuantity);

            // Cập nhật database
            cartDao.update(item);

            // Cập nhật UI
            adapter.notifyItemChanged(position);
            updateTotalPrice();

            // Update selected info if this item is selected
            if (item.isSelected()) {
                updateSelectedInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Hide loading indicator
            hideLoading();
        }
    }

    @Override
    public void onDecreaseQuantity(CartItem item, int position) {
        try {
            // Show loading indicator
            showLoading("Đang cập nhật số lượng...");

            // Giảm số lượng nếu số lượng > 1
            if (item.getSoLuong() > 1) {
                int newQuantity = item.getSoLuong() - 1;
                item.setSoLuong(newQuantity);

                // Cập nhật database
                cartDao.update(item);

                // Cập nhật UI
                adapter.notifyItemChanged(position);
                updateTotalPrice();

                // Update selected info if this item is selected
                if (item.isSelected()) {
                    updateSelectedInfo();
                }
            } else {
                // If quantity is 1, show message to user
                showToast("Nhấn nút xóa để loại bỏ sản phẩm");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Hide loading indicator
            hideLoading();
        }
    }

    @Override
    public void onRemoveItem(CartItem item, int position) {
        try {
            // Show loading to prevent multiple clicks
            showLoading("Đang xóa sản phẩm...");

            // Xóa khỏi database
            cartDao.delete(String.valueOf(item.getMaSanPham()));

            // Xóa khỏi list - add bounds check to avoid IndexOutOfBoundsException
            if (position >= 0 && position < cartItems.size()) {
                // Store a copy of the removed item for better animation
                CartItem removedItem = cartItems.get(position);

                // Remove the item from our list
                cartItems.remove(position);

                // Use notifyDataSetChanged instead of individual notifications
                // This prevents animation glitches with the last item
                adapter.notifyDataSetChanged();
            } else {
                // If position is invalid, refresh the entire list
                loadCartItems();
                return;
            }

            // Cập nhật tổng tiền
            updateTotalPrice();

            // Nếu không còn sản phẩm nào, hiển thị layout giỏ hàng trống
            if (cartItems.isEmpty()) {
                layoutCartContent.setVisibility(View.GONE);
                layoutEmptyCart.setVisibility(View.VISIBLE);
            }

            showToast(getString(R.string.item_removed));

            // Cập nhật thông tin về các món đã chọn
            updateSelectedInfo();
        } catch (Exception e) {
            e.printStackTrace();
            // Reload cart items to ensure UI is in sync with data
            loadCartItems();
        } finally {
            // Hide loading when done
            hideLoading();
        }
    }

    @Override
    public void onItemSelectionChanged() {
        updateSelectedInfo();

        // Kiểm tra xem tất cả các món có được chọn không
        boolean allSelected = true;
        for (CartItem item : cartItems) {
            if (!item.isSelected()) {
                allSelected = false;
                break;
            }
        }

        // Cập nhật trạng thái checkbox "Chọn tất cả" mà không kích hoạt sự kiện
        cbSelectAll.setOnCheckedChangeListener(null);
        cbSelectAll.setChecked(allSelected);
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItems) {
                item.setSelected(isChecked);
            }
            adapter.notifyDataSetChanged();
            updateSelectedInfo();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại giỏ hàng khi quay lại màn hình
        loadCartItems();
    }
}