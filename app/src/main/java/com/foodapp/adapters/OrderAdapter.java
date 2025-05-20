package com.foodapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.interfaces.OnItemClickListener;
import com.foodapp.models.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnItemClickListener<Order> listener;
    private OnActionButtonClickListener actionButtonListener;
    private boolean showDetails;

    public OrderAdapter(Context context, List<Order> orderList, boolean showDetails) {
        this.context = context;
        this.orderList = orderList != null ? orderList : new ArrayList<>();
        this.showDetails = showDetails;
    }

    // Constructor with default value for showDetails
    public OrderAdapter(Context context, List<Order> orderList) {
        this(context, orderList, true);
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

        holder.tvOrderStatus.setVisibility(View.VISIBLE);

        holder.tvCustomerName.setText(context.getString(R.string.customer_name, order.getHoTen()));
        holder.tvCustomerPhone.setText(context.getString(R.string.customer_phone, order.getSdt()));
        holder.tvCustomerAddress.setText(context.getString(R.string.customer_address, order.getDiaChi()));

        holder.tvOrderItems.setText(order.getThucDon());

        if (showDetails) {
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText(R.string.view_details);
            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(order, holder.getAdapterPosition());
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
        TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress, tvOrderItems;
        Button btnAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewOrder);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderAmount = itemView.findViewById(R.id.tvOrderAmount);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            btnAction = itemView.findViewById(R.id.btnOrderAction);
        }
    }

    // Interface cho sự kiện click vào nút action
    public interface OnActionButtonClickListener {
        void onActionButtonClick(Order order, int position);
    }
}