package com.foodapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.models.CartItem;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemActionListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Hiển thị thông tin sản phẩm
        holder.tvName.setText(item.getTenSanPham());

        // Hiển thị đồ ăn phụ nếu có
        if (item.getTenDoAnPhu() != null && !item.getTenDoAnPhu().isEmpty()) {
            holder.tvSideDish.setVisibility(View.VISIBLE);
            holder.tvSideDish.setText("+ " + item.getTenDoAnPhu());
        } else {
            holder.tvSideDish.setVisibility(View.GONE);
        }

        // Định dạng giá tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(item.getGiaSanPham());
        holder.tvPrice.setText(formattedPrice);

        // Hiển thị số lượng
        holder.tvQuantity.setText(String.valueOf(item.getSoLuong()));

        // Hiển thị hình ảnh nếu có
        if (item.getAnhSanPham() != null && !item.getAnhSanPham().isEmpty()) {
            Picasso.get().load(item.getAnhSanPham())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder);
        }

        // Sự kiện tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncreaseQuantity(item, position);
            }
        });

        // Sự kiện giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            if (listener != null && item.getSoLuong() > 1) {
                listener.onDecreaseQuantity(item, position);
            }
        });

        // Sự kiện xóa sản phẩm
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // Cập nhật dữ liệu mới
    public void updateData(List<CartItem> newItems) {
        this.cartItems.clear();
        this.cartItems.addAll(newItems);
        notifyDataSetChanged();
    }

    // Thiết lập listener
    public void setOnCartItemActionListener(OnCartItemActionListener listener) {
        this.listener = listener;
    }

    // ViewHolder cho item giỏ hàng
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvSideDish, tvPrice, tvQuantity;
        ImageButton btnIncrease, btnDecrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgCartItem);
            tvName = itemView.findViewById(R.id.tvCartItemName);
            tvSideDish = itemView.findViewById(R.id.tvCartItemSideDish);
            tvPrice = itemView.findViewById(R.id.tvCartItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartItemQuantity);
            btnIncrease = itemView.findViewById(R.id.btnCartItemIncrease);
            btnDecrease = itemView.findViewById(R.id.btnCartItemDecrease);
            btnRemove = itemView.findViewById(R.id.btnCartItemRemove);
        }
    }

    // Interface cho sự kiện giỏ hàng
    public interface OnCartItemActionListener {
        void onIncreaseQuantity(CartItem item, int position);

        void onDecreaseQuantity(CartItem item, int position);

        void onRemoveItem(CartItem item, int position);
    }
}