package com.foodapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.activities.FoodDetailActivity;
import com.foodapp.models.Food;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private Context context;
    private List<Food> foodList;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList != null ? foodList : new ArrayList<>();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);

        if (food == null) {
            return;
        }

        holder.tvName.setText(food.getTenDoAn());
        holder.tvPrice.setText(String.format("%,d VNĐ", (int) food.getGiaDoAn()));

        if (food.getLinkAnh() != null && !food.getLinkAnh().isEmpty()) {
            Picasso.get().load(food.getLinkAnh()).error(R.drawable.placeholder).into(holder.imgFood);
        } else {
            holder.imgFood.setImageResource(R.drawable.placeholder);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FoodDetailActivity.class);
                intent.putExtra("food_id", food.getMaDoAn());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // Cập nhật dữ liệu mới và thông báo adapter
    public void updateData(List<Food> newData) {
        this.foodList.clear();
        this.foodList.addAll(newData);
        notifyDataSetChanged();
    }

    // View Holder cho mỗi item trong danh sách
    public class FoodViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgFood;
        TextView tvName, tvPrice;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
        }
    }
}