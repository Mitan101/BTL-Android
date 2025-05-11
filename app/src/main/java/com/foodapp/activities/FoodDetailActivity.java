package com.foodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.foodapp.R;
import com.foodapp.database.dao.CartDao;
import com.foodapp.database.dao.FoodDao;
import com.foodapp.models.CartItem;
import com.foodapp.models.Food;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView imgFood;
    private TextView tvName, tvPrice, tvDescription, tvCategory;
    private Button btnAddToCart, btnOrderNow, btnMinus, btnPlus;
    private TextView tvQuantity;
    private Toolbar toolbar;

    private FoodDao foodDao;
    private CartDao cartDao;
    private Food food;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // Khởi tạo DAO
        foodDao = new FoodDao(this);
        cartDao = new CartDao(this);

        // Ánh xạ view
        imgFood = findViewById(R.id.imgFoodDetail);
        tvName = findViewById(R.id.tvFoodDetailName);
        tvPrice = findViewById(R.id.tvFoodDetailPrice);
        tvDescription = findViewById(R.id.tvFoodDetailDescription);
        tvCategory = findViewById(R.id.tvFoodDetailCategory);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnOrderNow = findViewById(R.id.btnOrderNow);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        tvQuantity = findViewById(R.id.tvQuantity);
        toolbar = findViewById(R.id.toolbar);

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
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Xử lý sự kiện cho nút tăng/giảm số lượng
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    tvQuantity.setText(String.valueOf(quantity));
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

        // Xử lý sự kiện cho nút đặt ngay
        btnOrderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
                Intent intent = new Intent(FoodDetailActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadFoodDetails(int foodId) {
        food = foodDao.getById(String.valueOf(foodId));

        if (food != null) {
            tvName.setText(food.getTenDoAn());

            // Định dạng giá tiền
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedPrice = formatter.format(food.getGiaDoAn());
            tvPrice.setText(formattedPrice);

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

            // TODO: Lấy tên danh mục từ mã danh mục
            // tvCategory.setText(categoryName);
        }
    }

    private void addToCart() {
        if (food != null) {
            CartItem cartItem = new CartItem();
            cartItem.setTenSanPham(food.getTenDoAn());
            cartItem.setGiaSanPham(food.getGiaDoAn());
            cartItem.setSoLuong(quantity);
            cartItem.setAnhSanPham(food.getLinkAnh());

            long result = cartDao.insert(cartItem);
            if (result > 0) {
                Toast.makeText(this, R.string.add_to_cart_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thêm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }
}