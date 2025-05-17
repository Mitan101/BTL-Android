package com.foodapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.models.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderStatisticsAdapter extends RecyclerView.Adapter<OrderStatisticsAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private NumberFormat currencyFormat;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public OrderStatisticsAdapter() {
        this.orderList = new ArrayList<>();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    public void updateData(List<Order> newOrders) {
        this.orderList.clear();
        if (newOrders != null) {
            this.orderList.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_statistics, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set order ID
        holder.tvOrderId.setText("#" + order.getMaHoaDon());

        // Set customer name
        holder.tvCustomerName.setText(order.getHoTen());

        // Set order items
        holder.tvOrderItems.setText(order.getThucDon());

        // Set order date
        holder.tvOrderDate.setText(order.getNgayDat());

        // Set price with currency format
        holder.tvOrderPrice.setText(currencyFormat.format(order.getTongTien()));

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvOrderItems, tvOrderPrice, tvOrderDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvOrderPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
        }
    }
}