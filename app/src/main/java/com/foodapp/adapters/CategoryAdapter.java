package com.foodapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.activities.CategoryProductsActivity;
import com.foodapp.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList != null ? categoryList : new ArrayList<>();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.tvCategoryName.setText(category.getTenLoai());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình danh sách sản phẩm theo danh mục
                Intent intent = new Intent(context, CategoryProductsActivity.class);
                intent.putExtra("category_id", category.getMaLoai());
                intent.putExtra("category_name", category.getTenLoai());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // Cập nhật dữ liệu
    public void updateData(List<Category> newData) {
        this.categoryList.clear();
        this.categoryList.addAll(newData);
        notifyDataSetChanged();
    }

    // ViewHolder cho item danh mục
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}