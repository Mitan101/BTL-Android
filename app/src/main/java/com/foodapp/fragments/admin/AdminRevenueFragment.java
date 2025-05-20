package com.foodapp.fragments.admin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.activities.user.OrderDetailActivity;
import com.foodapp.adapters.OrderStatisticsAdapter;
import com.foodapp.database.dao.OrderDao;
import com.foodapp.models.Order;
import com.foodapp.utils.Constants;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminRevenueFragment extends Fragment {

    private TextView tvTotalRevenue;
    private TextView tvFilterInfo;
    private RecyclerView rvOrderStatistics;
    private MaterialButtonToggleGroup toggleDateType;
    private Button btnFilterByDay, btnFilterByMonth, btnSelectDate, btnShowAll;

    private OrderDao orderDao;
    private OrderStatisticsAdapter adapter;
    private List<Order> allOrders = new ArrayList<>();

    private Calendar selectedDate = Calendar.getInstance();
    private boolean isFilteringByDay = true; // Default filter is by day
    private boolean isFilteringEnabled = false;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_revenue, container, false);

        // Initialize views
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        tvFilterInfo = view.findViewById(R.id.tvFilterInfo);
        rvOrderStatistics = view.findViewById(R.id.rvOrderStatistics);
        toggleDateType = view.findViewById(R.id.toggleDateType);
        btnFilterByDay = view.findViewById(R.id.btnFilterByDay);
        btnFilterByMonth = view.findViewById(R.id.btnFilterByMonth);
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        btnShowAll = view.findViewById(R.id.btnShowAll);

        orderDao = new OrderDao(requireContext());

        rvOrderStatistics.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderStatisticsAdapter();
        rvOrderStatistics.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                requireContext(), DividerItemDecoration.VERTICAL);
        rvOrderStatistics.addItemDecoration(dividerItemDecoration);

        adapter.setOnOrderClickListener(order -> {
            Intent intent = new Intent(requireContext(), OrderDetailActivity.class);
            int orderId = order.getMaHoaDon();
            Log.d("AdminRevenueFragment", "Opening order detail with ID: " + orderId);
            intent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
            startActivity(intent);
        });

        setupClickListeners();

        toggleDateType.check(R.id.btnFilterByDay);

        loadOrderData();

        return view;
    }

    private void setupClickListeners() {
        toggleDateType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                isFilteringByDay = checkedId == R.id.btnFilterByDay;
                updateDateButtonText();
            }
        });

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnShowAll.setOnClickListener(v -> {
            isFilteringEnabled = false;
            tvFilterInfo.setText("Hiển thị tất cả đơn hàng");
            loadOrderData();
        });
    }

    private void updateDateButtonText() {
        if (isFilteringByDay) {
            btnSelectDate.setText(Html.fromHtml("<b>Chọn ngày</b>"));
        } else {
            btnSelectDate.setText(Html.fromHtml("<b>Chọn tháng</b>"));
        }
    }

    private void showDatePicker() {
        if (isFilteringByDay) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        isFilteringEnabled = true;
                        String formattedDate = dateFormat.format(selectedDate.getTime());
                        tvFilterInfo.setText("Đơn hàng ngày: " + formattedDate);
                        filterOrdersByDate();
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        } else {
            showMonthYearPicker();
        }
    }

    private void showMonthYearPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.month_year_picker, null);
        builder.setView(dialogView);

        final NumberPicker monthPicker = dialogView.findViewById(R.id.picker_month);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(selectedDate.get(Calendar.MONTH) + 1);

        final NumberPicker yearPicker = dialogView.findViewById(R.id.picker_year);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(currentYear - 10);
        yearPicker.setMaxValue(currentYear);
        yearPicker.setValue(selectedDate.get(Calendar.YEAR));

        builder.setPositiveButton("OK", (dialog, which) -> {
            selectedDate.set(Calendar.YEAR, yearPicker.getValue());
            selectedDate.set(Calendar.MONTH, monthPicker.getValue() - 1);

            isFilteringEnabled = true;
            String formattedMonth = monthYearFormat.format(selectedDate.getTime());
            tvFilterInfo.setText("Đơn hàng tháng: " + formattedMonth);

            filterOrdersByMonth();
        });

        builder.setNegativeButton("Hủy", null);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadOrderData() {
        // Get all orders
        allOrders = orderDao.getAll();

        // Update adapter with all orders if no filter is applied
        if (!isFilteringEnabled) {
            adapter.updateData(allOrders);
        } else {
            // Apply current filter
            if (isFilteringByDay) {
                filterOrdersByDate();
            } else {
                filterOrdersByMonth();
            }
        }

        // Calculate total revenue from all orders
        calculateTotalRevenue(allOrders);
    }

    private void filterOrdersByDate() {
        List<Order> filteredOrders = new ArrayList<>();
        String selectedDateStr = dateFormat.format(selectedDate.getTime());

        for (Order order : allOrders) {
            if (order.getNgayDat().contains(selectedDateStr)) {
                filteredOrders.add(order);
            }
        }

        adapter.updateData(filteredOrders);
        calculateTotalRevenue(filteredOrders);
    }

    private void filterOrdersByMonth() {
        List<Order> filteredOrders = new ArrayList<>();
        String selectedMonthYear = monthYearFormat.format(selectedDate.getTime());

        for (Order order : allOrders) {
            try {
                Date orderDate = dateFormat.parse(order.getNgayDat());
                if (orderDate != null) {
                    String orderMonthYear = monthYearFormat.format(orderDate);
                    if (orderMonthYear.equals(selectedMonthYear)) {
                        filteredOrders.add(order);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        adapter.updateData(filteredOrders);
        calculateTotalRevenue(filteredOrders);
    }

    private void calculateTotalRevenue(List<Order> orders) {
        double totalRevenue = 0.0;
        for (Order order : orders) {
            totalRevenue += order.getTongTien();
        }
        tvTotalRevenue.setText(currencyFormat.format(totalRevenue));
    }
}