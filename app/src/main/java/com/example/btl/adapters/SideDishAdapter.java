package com.example.btl.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.models.SideDish;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SideDishAdapter extends RecyclerView.Adapter<SideDishAdapter.SideDishViewHolder> {

    private Context context;
    private List<SideDish> sideDishList;
    private Map<Integer, Integer> sideDishQuantities;
    private OnSideDishQuantityChangeListener listener;

    public interface OnSideDishQuantityChangeListener {
        void onSideDishQuantityChanged(List<SideDish> sideDishes, Map<Integer, Integer> quantities);
    }

    public SideDishAdapter(Context context, List<SideDish> sideDishList, OnSideDishQuantityChangeListener listener) {
        this.context = context;
        this.sideDishList = sideDishList;
        this.listener = listener;
        this.sideDishQuantities = new HashMap<>();

        // Initialize quantities to 0
        for (SideDish sideDish : sideDishList) {
            sideDishQuantities.put(sideDish.getMaDoAnPhu(), 0);
        }
    }

    @NonNull
    @Override
    public SideDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_side_dish, parent, false);
        return new SideDishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SideDishViewHolder holder, int position) {
        SideDish sideDish = sideDishList.get(position);

        // Set name
        holder.textViewName.setText(sideDish.getTenDoAnPhu());

        // Format price
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(sideDish.getGia());
        holder.textViewPrice.setText(formattedPrice);

        // Set quantity
        int quantity = sideDishQuantities.containsKey(sideDish.getMaDoAnPhu()) ? sideDishQuantities.get(sideDish.getMaDoAnPhu()) : 0;
        holder.tvQuantity.setText(String.valueOf(quantity));

        // Enable/disable minus button based on quantity
        holder.btnMinus.setEnabled(quantity > 0);

        // Handle checkbox based on quantity
        holder.checkbox.setChecked(quantity > 0);
        holder.checkbox.setOnClickListener(v -> {
            if (holder.checkbox.isChecked()) {
                // Set quantity to 1 if checked and was 0
                if (sideDishQuantities.get(sideDish.getMaDoAnPhu()) == 0) {
                    sideDishQuantities.put(sideDish.getMaDoAnPhu(), 1);
                    holder.tvQuantity.setText("1");
                    holder.btnMinus.setEnabled(true);
                }
            } else {
                // Set quantity to 0 if unchecked
                sideDishQuantities.put(sideDish.getMaDoAnPhu(), 0);
                holder.tvQuantity.setText("0");
                holder.btnMinus.setEnabled(false);
            }
            notifyQuantityChanged();
        });

        // Load image if available
        if (sideDish.getAnh() != null && !sideDish.getAnh().isEmpty()) {
            Picasso.get().load(sideDish.getAnh())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder);
        }

        // Setup quantity controls
        holder.btnPlus.setOnClickListener(v -> {
            int currentQty = sideDishQuantities.get(sideDish.getMaDoAnPhu());
            currentQty++;
            sideDishQuantities.put(sideDish.getMaDoAnPhu(), currentQty);
            holder.tvQuantity.setText(String.valueOf(currentQty));
            holder.checkbox.setChecked(true);
            holder.btnMinus.setEnabled(true);
            notifyQuantityChanged();
        });

        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = sideDishQuantities.get(sideDish.getMaDoAnPhu());
            if (currentQty > 0) {
                currentQty--;
                sideDishQuantities.put(sideDish.getMaDoAnPhu(), currentQty);
                holder.tvQuantity.setText(String.valueOf(currentQty));

                // Update checkbox state when quantity becomes 0
                if (currentQty == 0) {
                    holder.checkbox.setChecked(false);
                    holder.btnMinus.setEnabled(false);
                }
                notifyQuantityChanged();
            }
        });
    }

    private void notifyQuantityChanged() {
        if (listener != null) {
            List<SideDish> selectedDishes = new ArrayList<>();
            for (SideDish sideDish : sideDishList) {
                if (sideDishQuantities.containsKey(sideDish.getMaDoAnPhu()) && sideDishQuantities.get(sideDish.getMaDoAnPhu()) > 0) {
                    selectedDishes.add(sideDish);
                }
            }
            listener.onSideDishQuantityChanged(selectedDishes, sideDishQuantities);
        }
    }

    @Override
    public int getItemCount() {
        return sideDishList != null ? sideDishList.size() : 0;
    }

    public Map<Integer, Integer> getSideDishQuantities() {
        return sideDishQuantities;
    }

    static class SideDishViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        ImageView imageView;
        TextView textViewName, textViewPrice, tvQuantity;
        Button btnMinus, btnPlus;

        public SideDishViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkBoxSideDish);
            imageView = itemView.findViewById(R.id.imageViewSideDish);
            textViewName = itemView.findViewById(R.id.textViewSideDishName);
            textViewPrice = itemView.findViewById(R.id.textViewSideDishPrice);
            tvQuantity = itemView.findViewById(R.id.tvSideDishQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinusSideDish);
            btnPlus = itemView.findViewById(R.id.btnPlusSideDish);
        }
    }
}