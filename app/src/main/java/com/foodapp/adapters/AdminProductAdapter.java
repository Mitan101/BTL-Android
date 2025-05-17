package com.foodapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.models.Food;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Food> foodList;
    private OnProductClickListener listener;

    public AdminProductAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
        notifyDataSetChanged();
    }

    public interface OnProductClickListener {
        void onProductClick(Food food);

        void onEditClick(Food food);

        void onDeleteClick(Food food);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.tvProductName.setText(food.getTenDoAn());

        // Format price with currency
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvProductPrice.setText(currencyFormat.format(food.getGiaDoAn()));

        // Tải ảnh từ đường dẫn lưu trong bộ nhớ nội bộ
        if (food.getLinkAnh() != null && !food.getLinkAnh().isEmpty()) {
            try {
                File imgFile = new File(food.getLinkAnh());
                if (imgFile.exists()) {
                    // Cách 1: Sử dụng BitmapFactory
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    holder.imgProduct.setImageBitmap(bitmap);
                } else {
                    // Cách 2: Thử với Picasso nếu là URI
                    try {
                        Picasso.get()
                                .load(Uri.parse(food.getLinkAnh()))
                                .placeholder(R.drawable.placeholder)
                                .into(holder.imgProduct);
                    } catch (Exception e) {
                        holder.imgProduct.setImageResource(R.drawable.placeholder);
                    }
                }
            } catch (Exception e) {
                holder.imgProduct.setImageResource(R.drawable.placeholder);
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return foodList == null ? 0 : foodList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgEdit, imgDelete;
        TextView tvProductName, tvProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onProductClick(foodList.get(getAdapterPosition()));
                }
            });

            imgEdit.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onEditClick(foodList.get(getAdapterPosition()));
                }
            });

            imgDelete.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(foodList.get(getAdapterPosition()));
                }
            });
        }
    }
}