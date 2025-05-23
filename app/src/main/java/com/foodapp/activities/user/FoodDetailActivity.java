package com.foodapp.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.adapters.SideDishAdapter;
import com.foodapp.database.dao.CartDao;
import com.foodapp.database.dao.FoodDao;
import com.foodapp.database.dao.SideDishDao;
import com.foodapp.models.CartItem;
import com.foodapp.models.Food;
import com.foodapp.models.SideDish;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodDetailActivity extends AppCompatActivity implements SideDishAdapter.OnSideDishQuantityChangeListener {

    private ImageView imgFood;
    private TextView tvName, tvPrice, tvDescription, tvCategory;
    private Button btnAddToCart, btnMinus, btnPlus;
    private TextView tvQuantity;
    private Toolbar toolbar;
    private RecyclerView recyclerViewSideDishes;

    private FoodDao foodDao;
    private CartDao cartDao;
    private SideDishDao sideDishDao;
    private SideDishAdapter sideDishAdapter;
    private Food food;
    private int quantity = 1;
    private List<SideDish> selectedSideDishes = new ArrayList<>();
    private Map<Integer, Integer> sideDishQuantities = new HashMap<>();
    private double totalPrice = 0;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // Khởi tạo DAO
        foodDao = new FoodDao(this);
        cartDao = new CartDao(this);
        sideDishDao = new SideDishDao(this);

        // Ánh xạ view
        imgFood = findViewById(R.id.imgFoodDetail);
        tvName = findViewById(R.id.tvFoodDetailName);
        tvPrice = findViewById(R.id.tvFoodDetailPrice);
        tvDescription = findViewById(R.id.tvFoodDetailDescription);
        tvCategory = findViewById(R.id.tvFoodDetailCategory);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        tvQuantity = findViewById(R.id.tvQuantity);
        toolbar = findViewById(R.id.toolbar);
        recyclerViewSideDishes = findViewById(R.id.recyclerViewSideDishes);

        // Cấu hình toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.product_detail);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Lấy ID món ăn từ intent
        int foodId = getIntent().getIntExtra("food_id", 0);
        if (foodId > 0) {
            loadFoodDetails(foodId);
            loadSideDishes();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
            finish();
        }

        userId = getIntent().getIntExtra("user_id", 0);

        // Xử lý sự kiện cho nút tăng/giảm số lượng
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    tvQuantity.setText(String.valueOf(quantity));
                    updateTotalPrice();
                }
            }
        });

        // Xử lý sự kiện cho nút thêm vào giỏ hàng
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }

    private void loadSideDishes() {
        // Thiết lập RecyclerView
        recyclerViewSideDishes.setLayoutManager(new LinearLayoutManager(this));

        // Lấy danh sách đồ ăn phụ
        List<SideDish> sideDishes = sideDishDao.getAll();

        // Khởi tạo adapter
        sideDishAdapter = new SideDishAdapter(this, sideDishes, this);
        recyclerViewSideDishes.setAdapter(sideDishAdapter);
    }

    private void loadFoodDetails(int foodId) {
        food = foodDao.getById(String.valueOf(foodId));

        if (food != null) {
            tvName.setText(food.getTenDoAn());

            // Định dạng giá tiền
            totalPrice = food.getGiaDoAn();
            updateTotalPrice();

            tvDescription.setText(food.getThongTin());

            // Hiển thị hình ảnh
            if (food.getLinkAnh() != null && !food.getLinkAnh().isEmpty()) {
                Picasso.get().load(food.getLinkAnh())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(imgFood);
            } else {
                imgFood.setImageResource(R.drawable.placeholder);
            }
        }
    }

    private void addToCart() {
        if (food != null) {
            CartItem cartItem = new CartItem();
            cartItem.setTenSanPham(food.getTenDoAn());
            cartItem.setMaTV(userId);
            cartItem.setGiaSanPham(calculateTotalPrice());
            cartItem.setSoLuong(quantity);
            cartItem.setAnhSanPham(food.getLinkAnh());

            StringBuilder sideDishInfo = new StringBuilder();

            for (SideDish sideDish : selectedSideDishes) {
                int sideDishQty = sideDishQuantities.containsKey(sideDish.getMaDoAnPhu()) ?
                        sideDishQuantities.get(sideDish.getMaDoAnPhu()) : 0;

                if (sideDishQty > 0) {
                    if (sideDishInfo.length() > 0) sideDishInfo.append(", ");
                    sideDishInfo.append(sideDish.getTenDoAnPhu())
                            .append(" (x")
                            .append(sideDishQty)
                            .append(")");
                }
            }

            if (sideDishInfo.length() > 0) {
                cartItem.setTenDoAnPhu(sideDishInfo.toString());
            }

            long result = cartDao.insert(cartItem);
            if (result > 0) {
                Toast.makeText(this, R.string.add_to_cart_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thêm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private double calculateTotalPrice() {
        // Giá cơ bản của món ăn
        double price = food.getGiaDoAn();

        // Thêm giá của các đồ ăn phụ với số lượng tương ứng
        for (SideDish sideDish : selectedSideDishes) {
            int sideDishQty = sideDishQuantities.containsKey(sideDish.getMaDoAnPhu()) ?
                    sideDishQuantities.get(sideDish.getMaDoAnPhu()) : 0;
            price += sideDish.getGia() * sideDishQty;
        }

        // Nhân với số lượng món ăn chính
        return price * quantity;
    }

    private void updateTotalPrice() {
        double price = calculateTotalPrice();

        // Cập nhật hiển thị giá tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(price);
        tvPrice.setText(formattedPrice);
    }

    @Override
    public void onSideDishQuantityChanged(List<SideDish> sideDishes, Map<Integer, Integer> quantities) {
        this.selectedSideDishes = sideDishes;
        this.sideDishQuantities = quantities;
        updateTotalPrice();
    }
}