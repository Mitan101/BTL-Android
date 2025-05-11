package com.foodapp.fragments.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.database.dao.SideDishDao;
import com.foodapp.models.SideDish;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminSideDishFragment extends Fragment {
    private RecyclerView rc_doanphu;
    private FloatingActionButton dap_float_add;
    private SideDishDao sideDishDao;
    private SideDishAdapter adapter;
    private List<SideDish> sideDishList;
    private static final int REQUEST_IMAGE_PICK = 100;
    private Uri selectedImageUri;
    private String selectedUrl;
    private ImageView ivSideDishImage;
    private Dialog currentDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_sp_phu, container, false);

        rc_doanphu = view.findViewById(R.id.rc_doanphu);
        dap_float_add = view.findViewById(R.id.dap_float_add);

        // Thiết lập RecyclerView
        rc_doanphu.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo DAO
        sideDishDao = new SideDishDao(getContext());

        // Lấy dữ liệu
        loadSideDishes();

        // Sự kiện cho nút thêm
        dap_float_add.setOnClickListener(v -> showAddSideDishDialog());

        return view;
    }

    private void loadSideDishes() {
        try {
            sideDishList = sideDishDao.getAll();
            adapter = new SideDishAdapter(sideDishList);
            rc_doanphu.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddSideDishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_themsp_phu, null);
        builder.setView(view);

        // Reset giá trị
        selectedImageUri = null;
        selectedUrl = null;

        // Ánh xạ view
        ivSideDishImage = view.findViewById(R.id.iv_spPhu_image);
        Button btnSelectImage = view.findViewById(R.id.btn_selectImage);
        Button btnEnterUrl = view.findViewById(R.id.btn_enterUrl);
        TextInputEditText etName = view.findViewById(R.id.ed_themspphu);
        TextInputEditText etPrice = view.findViewById(R.id.ed_giaspphu);
        Button btnAdd = view.findViewById(R.id.btn_themspphu);
        Button btnCancel = view.findViewById(R.id.btn_huythemspphu);

        AlertDialog dialog = builder.create();

        // Sự kiện chọn ảnh
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        // Sự kiện nhập URL
        btnEnterUrl.setOnClickListener(v -> showUrlInputDialog());

        // Sự kiện thêm
        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên sản phẩm phụ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập giá sản phẩm phụ", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);

                SideDish sideDish = new SideDish();
                sideDish.setTenDoAnPhu(name);
                sideDish.setGia(price);

                String imagePath = "";
                if (selectedImageUri != null) {
                    imagePath = saveImageToStorage(selectedImageUri);
                } else if (selectedUrl != null && !selectedUrl.isEmpty()) {
                    imagePath = selectedUrl;
                }
                sideDish.setAnh(imagePath);

                long result = sideDishDao.insert(sideDish);
                if (result > 0) {
                    Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                    loadSideDishes();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện hủy
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        currentDialog = dialog;
    }

    private void showEditSideDishDialog(SideDish sideDish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_themsp_phu, null);
        builder.setView(view);

        // Reset giá trị
        selectedImageUri = null;
        selectedUrl = null;

        // Ánh xạ view
        TextView tvTitle = view.findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText("Sửa đồ ăn phụ");
        }

        ivSideDishImage = view.findViewById(R.id.iv_spPhu_image);
        Button btnSelectImage = view.findViewById(R.id.btn_selectImage);
        Button btnEnterUrl = view.findViewById(R.id.btn_enterUrl);
        TextInputEditText etName = view.findViewById(R.id.ed_themspphu);
        TextInputEditText etPrice = view.findViewById(R.id.ed_giaspphu);
        Button btnAdd = view.findViewById(R.id.btn_themspphu);
        Button btnCancel = view.findViewById(R.id.btn_huythemspphu);

        // Thiết lập dữ liệu
        etName.setText(sideDish.getTenDoAnPhu());
        etPrice.setText(String.valueOf(sideDish.getGia()));
        btnAdd.setText("Cập nhật");

        // Hiển thị ảnh nếu có
        if (sideDish.getAnh() != null && !sideDish.getAnh().isEmpty()) {
            try {
                File imgFile = new File(sideDish.getAnh());
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivSideDishImage.setImageBitmap(bitmap);
                } else if (sideDish.getAnh().startsWith("http://") || sideDish.getAnh().startsWith("https://")) {
                    Picasso.get()
                            .load(sideDish.getAnh())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(ivSideDishImage);
                }
            } catch (Exception e) {
                ivSideDishImage.setImageResource(R.drawable.placeholder);
            }
        }

        AlertDialog dialog = builder.create();

        // Sự kiện chọn ảnh
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        // Sự kiện nhập URL
        btnEnterUrl.setOnClickListener(v -> showUrlInputDialog());

        // Sự kiện cập nhật
        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên sản phẩm phụ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (priceStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập giá sản phẩm phụ", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);

                sideDish.setTenDoAnPhu(name);
                sideDish.setGia(price);

                if (selectedImageUri != null) {
                    String imagePath = saveImageToStorage(selectedImageUri);
                    sideDish.setAnh(imagePath);
                } else if (selectedUrl != null && !selectedUrl.isEmpty()) {
                    sideDish.setAnh(selectedUrl);
                }

                int result = sideDishDao.update(sideDish);
                if (result > 0) {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    loadSideDishes();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện hủy
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        currentDialog = dialog;
    }

    private void showDeleteConfirmDialog(SideDish sideDish) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa sản phẩm phụ này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    try {
                        int result = sideDishDao.delete(String.valueOf(sideDish.getMaDoAnPhu()));
                        if (result > 0) {
                            Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                            loadSideDishes();
                        } else {
                            Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showUrlInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Nhập URL ảnh");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input_url, null);
        builder.setView(dialogView);

        final EditText etUrl = dialogView.findViewById(R.id.etUrl);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String url = etUrl.getText().toString();
            if (url != null && !url.isEmpty()) {
                try {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "https://" + url;
                    }

                    Picasso.get()
                            .load(url)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(ivSideDishImage);

                    selectedImageUri = null;
                    selectedUrl = url;

                    Toast.makeText(getContext(), "Ảnh từ URL đã được chọn", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Lỗi hiển thị ảnh từ URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                selectedUrl = null;

                try {
                    if (ivSideDishImage != null) {
                        Bitmap bitmap = getBitmapFromUri(selectedImageUri);
                        if (bitmap != null) {
                            ivSideDishImage.setImageBitmap(bitmap);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Lỗi hiển thị ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            InputStream input = getContext().getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            if (input != null) input.close();

            int maxDimension = 1200;
            int width = options.outWidth;
            int height = options.outHeight;
            int scaleFactor = Math.max(1, Math.min(width / maxDimension, height / maxDimension));

            options = new BitmapFactory.Options();
            options.inSampleSize = scaleFactor;
            options.inJustDecodeBounds = false;

            input = getContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            if (input != null) input.close();

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String saveImageToStorage(Uri imageUri) throws IOException {
        try {
            File directory = new File(getContext().getFilesDir(), "side_dish_images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageName = "SIDE_DISH_" + timeStamp + ".jpg";
            File imageFile = new File(directory, imageName);

            Bitmap bitmap = getBitmapFromUri(imageUri);
            if (bitmap != null) {
                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();

                return imageFile.getAbsolutePath();
            } else {
                throw new IOException("Cannot decode bitmap from URI");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSideDishes();
    }

    // Adapter cho RecyclerView
    private class SideDishAdapter extends RecyclerView.Adapter<SideDishAdapter.ViewHolder> {
        private List<SideDish> sideDishes;

        public SideDishAdapter(List<SideDish> sideDishes) {
            this.sideDishes = sideDishes;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_doanphu, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SideDish sideDish = sideDishes.get(position);

            holder.tvMa.setText("Mã: " + sideDish.getMaDoAnPhu());
            holder.tvTen.setText(sideDish.getTenDoAnPhu());
            holder.tvGia.setText(String.format(Locale.getDefault(), "%,.0f đ", sideDish.getGia()));

            if (sideDish.getAnh() != null && !sideDish.getAnh().isEmpty()) {
                try {
                    File imgFile = new File(sideDish.getAnh());
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        holder.imgSideDish.setImageBitmap(bitmap);
                    } else if (sideDish.getAnh().startsWith("http://") || sideDish.getAnh().startsWith("https://")) {
                        Picasso.get()
                                .load(sideDish.getAnh())
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .into(holder.imgSideDish);
                    }
                } catch (Exception e) {
                    holder.imgSideDish.setImageResource(R.drawable.placeholder);
                }
            } else {
                holder.imgSideDish.setImageResource(R.drawable.placeholder);
            }

            // Sự kiện sửa
            holder.btnEdit.setOnClickListener(v -> showEditSideDishDialog(sideDish));

            // Sự kiện xóa
            holder.btnDelete.setOnClickListener(v -> showDeleteConfirmDialog(sideDish));
        }

        @Override
        public int getItemCount() {
            return sideDishes == null ? 0 : sideDishes.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvMa, tvTen, tvGia;
            ImageView imgSideDish;
            ImageView btnEdit, btnDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMa = itemView.findViewById(R.id.dap_ma);
                tvTen = itemView.findViewById(R.id.dap_ten);
                tvGia = itemView.findViewById(R.id.dap_gia);
                imgSideDish = itemView.findViewById(R.id.dap_image);
                btnEdit = itemView.findViewById(R.id.dap_edit);
                btnDelete = itemView.findViewById(R.id.dap_delete);
            }
        }
    }
}
