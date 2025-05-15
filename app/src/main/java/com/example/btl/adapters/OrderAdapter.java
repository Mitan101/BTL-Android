package com.example.btl.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.interfaces.OnItemClickListener;
import com.example.btl.models.Order;
import com.example.btl.utils.Constants;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnItemClickListener<Order> listener;
    private OnActionButtonClickListener actionButtonListener;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList != null ? orderList : new ArrayList<>();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText(String.format("#%d", order.getMaHoaDon()));

        // Định dạng giá tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedAmount = formatter.format(order.getTongTien());
        holder.tvOrderAmount.setText(formattedAmount);

        holder.tvOrderDate.setText(order.getNgayDat());
        holder.tvOrderStatus.setText(order.getTrangThai());

        // Đặt màu cho trạng thái đơn hàng
        switch (order.getTrangThai()) {
            case Constants.STATUS_COMPLETED:
                holder.tvOrderStatus.setTextColor(Color.parseColor("#4CAF50")); // Xanh lá
                break;
            case Constants.STATUS_CANCELLED:
                holder.tvOrderStatus.setTextColor(Color.parseColor("#F44336")); // Đỏ
                break;
            case Constants.STATUS_COOKING:
                holder.tvOrderStatus.setTextColor(Color.parseColor("#FF9800")); // Cam
                break;
            case Constants.STATUS_DELIVERING:
                holder.tvOrderStatus.setTextColor(Color.parseColor("#2196F3")); // Xanh dương
                break;
            default:
                holder.tvOrderStatus.setTextColor(Color.parseColor("#757575")); // Xám
                break;
        }

        // Nếu trạng thái là "Đang chờ xử lý" hoặc "Đang chế biến", hiển thị nút "Cập nhật trạng thái"
        if (order.getTrangThai().equals(Constants.STATUS_PENDING) ||
                order.getTrangThai().equals(Constants.STATUS_COOKING)) {
            holder.btnAction.setVisibility(View.VISIBLE);

            if (order.getTrangThai().equals(Constants.STATUS_PENDING)) {
                holder.btnAction.setText(R.string.start_cooking);
            } else if (order.getTrangThai().equals(Constants.STATUS_COOKING)) {
                holder.btnAction.setText(R.string.complete_order);
            }

            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (actionButtonListener != null) {
                        actionButtonListener.onActionButtonClick(order, holder.getAdapterPosition());
                    }
                }
            });
        } else {
            holder.btnAction.setVisibility(View.GONE);
        }

        // Xử lý sự kiện click vào cardView
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(order, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // Cập nhật dữ liệu
    public void updateData(List<Order> newData) {
        this.orderList.clear();
        this.orderList.addAll(newData);
        notifyDataSetChanged();
    }

    // Set listener cho sự kiện click vào item
    public void setOnItemClickListener(OnItemClickListener<Order> listener) {
        this.listener = listener;
    }

    // Set listener cho sự kiện click vào nút action
    public void setOnActionButtonClickListener(OnActionButtonClickListener listener) {
        this.actionButtonListener = listener;
    }

    // ViewHolder cho item đơn hàng
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvOrderId, tvOrderAmount, tvOrderDate, tvOrderStatus;
        Button btnAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewOrder);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderAmount = itemView.findViewById(R.id.tvOrderAmount);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            btnAction = itemView.findViewById(R.id.btnOrderAction);
        }
    }

    // Interface cho sự kiện click vào nút action
    public interface OnActionButtonClickListener {
        void onActionButtonClick(Order order, int position);
    }
}