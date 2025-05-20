package com.foodapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.database.dao.CategoryDao;
import com.foodapp.models.Category;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.ViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private CategoryDao categoryDao;

    public AdminCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList != null ? categoryList : new ArrayList<>();
        this.categoryDao = new CategoryDao(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getTenLoai());

        // Sự kiện nút sửa
        holder.btnEdit.setOnClickListener(v -> {
            showEditCategoryDialog(category);
        });

        // Sự kiện nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(category);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // Cập nhật danh sách
    public void updateData(List<Category> newData) {
        this.categoryList.clear();
        if (newData != null) {
            this.categoryList.addAll(newData);
        }
        notifyDataSetChanged();
    }

    // Hiển thị dialog sửa loại sản phẩm
    private void showEditCategoryDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa loại sản phẩm");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etCategoryName);
        etName.setText(category.getTenLoai());

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = etName.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập tên loại sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            category.setTenLoai(name);
            int result = categoryDao.update(category);

            if (result > 0) {
                notifyDataSetChanged();
                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }

    // Hiển thị dialog xác nhận xóa
    private void showDeleteConfirmationDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc muốn xóa loại sản phẩm này?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            int result = categoryDao.delete(String.valueOf(category.getMaLoai()));

            if (result > 0) {
                categoryList.remove(category);
                notifyDataSetChanged();
                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        ImageButton btnEdit, btnDelete;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            btnEdit = itemView.findViewById(R.id.btnEditCategory);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}