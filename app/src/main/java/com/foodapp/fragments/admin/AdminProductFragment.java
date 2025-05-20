package com.foodapp.fragments.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodapp.R;
import com.foodapp.adapters.AdminProductAdapter;
import com.foodapp.database.dao.CategoryDao;
import com.foodapp.database.dao.FoodDao;
import com.foodapp.models.Category;
import com.foodapp.models.Food;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AdminProductFragment extends Fragment implements AdminProductAdapter.OnProductClickListener {
    private RecyclerView recyclerView;
    private List<Food> foodList;
    private FoodDao foodDao;
    private CategoryDao categoryDao;
    private List<Category> categoryList;
    private AdminProductAdapter adapter;
    private FloatingActionButton fabAdd;
    private static final int REQUEST_IMAGE_PICK = 100;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Uri selectedImageUri;
    private String selectedUrl;
    private ImageView ivProductImage;
    private AlertDialog currentDialog; // Dialog hiện tại đang mở

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewAdminProducts);
        fabAdd = view.findViewById(R.id.fabAddProduct);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        foodDao = new FoodDao(getContext());
        categoryDao = new CategoryDao(getContext());


        checkPermissions();

        try {
            categoryList = categoryDao.getAll();
        } catch (Exception e) {
            categoryList = new ArrayList<>();
            Toast.makeText(getContext(), "Không thể tải danh sách loại sản phẩm", Toast.LENGTH_SHORT).show();
        }

        loadFoodList();

        fabAdd.setOnClickListener(v -> showAddProductDialog());
        return view;
    }

    private void loadFoodList() {
        Log.d("AdminProductFragment", "loadFoodList: " + (foodDao == null));
        if (foodDao == null) {
            return;  // Không làm gì nếu DAO chưa khởi tạo
        }

        try {
            // Lấy danh sách từ database
            foodList = foodDao.getAll();

            // Tạo và gắn adapter cho RecyclerView
            if (adapter == null) {
                adapter = new AdminProductAdapter(getContext(), foodList);
                adapter.setOnProductClickListener(this);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.setFoodList(foodList);
                recyclerView.setAdapter(adapter);
            }
        } catch (Exception e) {
            Log.e("AdminProductFragment", "Error loading food list", e);
        }
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm sản phẩm mới");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        // Reset các giá trị
        selectedImageUri = null;
        selectedUrl = null;

        ivProductImage = dialogView.findViewById(R.id.ivProductImage);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnEnterUrl = dialogView.findViewById(R.id.btnEnterUrl);
        final TextInputEditText etName = dialogView.findViewById(R.id.etProductName);
        final TextInputEditText etPrice = dialogView.findViewById(R.id.etProductPrice);
        final TextInputEditText etDescription = dialogView.findViewById(R.id.etProductDescription);
        final Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);

        etDescription.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(v);
                return true;
            }
            return false;
        });

        List<String> categoryNames = new ArrayList<>();
        if (categoryList == null || categoryList.isEmpty()) {
            Category defaultCategory = new Category();
            defaultCategory.setMaLoai(1);
            defaultCategory.setTenLoai("Mặc định");


            categoryList = new ArrayList<>();
            categoryList.add(defaultCategory);

            // Lưu loại mặc định vào database
            try {
                categoryDao.insert(defaultCategory);
            } catch (Exception e) {
                // Bỏ qua nếu đã tồn tại
            }
        }

        for (Category category : categoryList) {
            categoryNames.add(category.getTenLoai());
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        btnEnterUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUrlInputDialog();
            }
        });

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = etName.getText().toString();
                        String priceStr = etPrice.getText().toString();
                        String description = etDescription.getText().toString();
                        int selectedCategoryPosition = spinnerCategory.getSelectedItemPosition();

                        if (name.isEmpty() || priceStr.isEmpty()) {
                            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            double price = Double.parseDouble(priceStr);
                            int categoryId = 1;
                            if (selectedCategoryPosition >= 0 && selectedCategoryPosition < categoryList.size()) {
                                categoryId = categoryList.get(selectedCategoryPosition).getMaLoai();
                            }
                            String imagePath = "";

                            if (selectedImageUri != null) {
                                imagePath = saveImageToStorage(selectedImageUri);
                            } else if (selectedUrl != null && !selectedUrl.isEmpty()) {
                                imagePath = selectedUrl;
                            }

                            Food newFood = new Food();
                            newFood.setTenDoAn(name);
                            newFood.setGiaDoAn(price);
                            newFood.setMaLoai(categoryId);
                            newFood.setThongTin(description);
                            newFood.setLinkAnh(imagePath);

                            long result = foodDao.insert(newFood);

                            if (result > 0) {
                                loadFoodList();
                                Toast.makeText(getContext(), "Đã thêm sản phẩm mới", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
        currentDialog = dialog;
    }

    private void showEditProductDialog(Food food) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa sản phẩm");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        // Reset các giá trị
        selectedImageUri = null;
        selectedUrl = null;

        final ImageView imageView = dialogView.findViewById(R.id.ivProductImage);
        ivProductImage = imageView;
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnEnterUrl = dialogView.findViewById(R.id.btnEnterUrl);
        final TextInputEditText etName = dialogView.findViewById(R.id.etProductName);
        final TextInputEditText etPrice = dialogView.findViewById(R.id.etProductPrice);
        final TextInputEditText etDescription = dialogView.findViewById(R.id.etProductDescription);
        final Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);

        etDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });

        etName.setText(food.getTenDoAn());
        etPrice.setText(String.valueOf(food.getGiaDoAn()));
        etDescription.setText(food.getThongTin());

        if (food.getLinkAnh() != null && !food.getLinkAnh().isEmpty()) {
            try {
                // Kiểm tra nếu đường dẫn là file trong thiết bị
                File imgFile = new File(food.getLinkAnh());
                if (imgFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivProductImage.setImageBitmap(bitmap);
                } else {
                    // Kiểm tra nếu đường dẫn là URL internet
                    if (food.getLinkAnh().startsWith("http://") || food.getLinkAnh().startsWith("https://")) {
                        // Sử dụng Picasso để load URL
                        Picasso.get()
                                .load(food.getLinkAnh())
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .into(ivProductImage);
                    } else {
                        // Thử parse như Uri
                        try {
                            Uri uri = Uri.parse(food.getLinkAnh());
                            Picasso.get()
                                    .load(uri)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .into(ivProductImage);
                        } catch (Exception uriException) {
                            ivProductImage.setImageResource(R.drawable.placeholder);
                            Log.e("ProductImage", "Không thể hiển thị ảnh từ đường dẫn: " + food.getLinkAnh(), uriException);
                        }
                    }
                }
            } catch (Exception e) {
                ivProductImage.setImageResource(R.drawable.placeholder);
                Log.e("ProductImage", "Lỗi hiển thị ảnh: ", e);
            }
        } else {
            ivProductImage.setImageResource(R.drawable.placeholder);
        }

        List<String> categoryNames = new ArrayList<>();
        for (Category category : categoryList) {
            categoryNames.add(category.getTenLoai());
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        int currentCategoryIndex = 0;
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getMaLoai() == food.getMaLoai()) {
                currentCategoryIndex = i;
                break;
            }
        }
        spinnerCategory.setSelection(currentCategoryIndex);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageUri = null;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });

        btnEnterUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUrlInputDialog();
            }
        });

        builder.setPositiveButton("Lưu", null);
        builder.setNegativeButton("Hủy", null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = etName.getText().toString();
                        String priceStr = etPrice.getText().toString();
                        String description = etDescription.getText().toString();
                        int selectedCategoryPosition = spinnerCategory.getSelectedItemPosition();

                        if (name.isEmpty() || priceStr.isEmpty()) {
                            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            double price = Double.parseDouble(priceStr);
                            int categoryId = 1;
                            if (selectedCategoryPosition >= 0 && selectedCategoryPosition < categoryList.size()) {
                                categoryId = categoryList.get(selectedCategoryPosition).getMaLoai();
                            }

                            food.setTenDoAn(name);
                            food.setGiaDoAn(price);
                            food.setMaLoai(categoryId);
                            food.setThongTin(description);

                            if (selectedImageUri != null) {
                                String imagePath = saveImageToStorage(selectedImageUri);
                                food.setLinkAnh(imagePath);
                            } else if (selectedUrl != null && !selectedUrl.isEmpty()) {
                                food.setLinkAnh(selectedUrl);
                            }

                            int result = foodDao.update(food);

                            if (result > 0) {
                                loadFoodList();
                                Toast.makeText(getContext(), "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "Cập nhật sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
        currentDialog = dialog;
    }

    private void showUrlInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Nhập URL ảnh");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_input_url, null);
        builder.setView(dialogView);

        final TextInputEditText etUrl = dialogView.findViewById(R.id.etUrl);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = etUrl.getText().toString();
                if (url != null && !url.isEmpty()) {
                    try {
                        Picasso.get()
                                .load(url)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .into(ivProductImage);
                        selectedImageUri = null; // Reset URI
                        // Gán URL vào biến để lưu
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            url = "https://" + url;
                        }
                        selectedUrl = url;
                        Toast.makeText(getContext(), "Ảnh từ URL đã được chọn", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Lỗi hiển thị ảnh từ URL", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setNegativeButton("Hủy", null);

        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                selectedUrl = null;
                try {
                    // Hiển thị ảnh đã chọn trong ImageView (nếu nó tồn tại)
                    if (ivProductImage != null && currentDialog != null && currentDialog.isShowing()) {
                        Bitmap bitmap = getBitmapFromUri(selectedImageUri);
                        if (bitmap != null) {
                            ivProductImage.setImageBitmap(bitmap);
                        } else {
                            Toast.makeText(getContext(), "Không thể đọc ảnh", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Lưu URI để sử dụng sau này nếu ImageView không sẵn sàng
                        Toast.makeText(getContext(), "Đã chọn ảnh, nhưng không thể hiển thị ngay bây giờ", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Lỗi hiển thị ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void checkPermissions() {
        String[] permissions;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{android.Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        boolean needRequest = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                needRequest = true;
                break;
            }
        }

        if (needRequest) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                Toast.makeText(getContext(), "Cần quyền truy cập để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToStorage(Uri imageUri) throws IOException {
        try {
            // Tạo thư mục trong bộ nhớ ứng dụng (không cần quyền)
            File directory = new File(getContext().getFilesDir(), "product_images");
            if (!directory.exists()) {
                boolean dirCreated = directory.mkdirs();
                if (!dirCreated) {
                    Toast.makeText(getContext(), "Không thể tạo thư mục", Toast.LENGTH_SHORT).show();
                }
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageName = "PRODUCT_" + timeStamp + ".jpg";
            File imageFile = new File(directory, imageName);

            // Tạo bitmap từ uri với kích thước phù hợp để giảm bộ nhớ sử dụng
            Bitmap bitmap = getBitmapFromUri(imageUri);
            if (bitmap != null) {
                // Nén và lưu ảnh
                FileOutputStream fos = new FileOutputStream(imageFile);
                boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();

                if (!success) {
                    Toast.makeText(getContext(), "Nén ảnh thất bại", Toast.LENGTH_SHORT).show();
                }

                // Kiểm tra xem file có tồn tại không
                if (imageFile.exists()) {
                    String absolutePath = imageFile.getAbsolutePath();
                    Log.d("ProductImage", "Lưu ảnh thành công: " + absolutePath);
                    Toast.makeText(getContext(), "Lưu ảnh thành công", Toast.LENGTH_SHORT).show();
                    return absolutePath;
                } else {
                    Toast.makeText(getContext(), "File không tồn tại sau khi lưu", Toast.LENGTH_SHORT).show();
                    throw new IOException("File not created");
                }
            } else {
                throw new IOException("Cannot decode bitmap from URI");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            throw e;
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            // Đọc kích thước ảnh để tính toán tỷ lệ thu nhỏ
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            InputStream input = getContext().getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(input, null, options);
            if (input != null) input.close();

            // Tính toán tỷ lệ thu nhỏ
            int maxDimension = 1200; // Kích thước tối đa cho chiều dài hoặc rộng
            int width = options.outWidth;
            int height = options.outHeight;
            int scaleFactor = Math.max(1, Math.min(width / maxDimension, height / maxDimension));

            // Đọc bitmap với tỷ lệ thu nhỏ
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

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onProductClick(Food food) {
        Toast.makeText(getContext(), "Đã chọn " + food.getTenDoAn(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Food food) {
        showEditProductDialog(food);
    }

    @Override
    public void onDeleteClick(Food food) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa sản phẩm " + food.getTenDoAn() + "?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int result = foodDao.delete(String.valueOf(food.getMaDoAn()));
                        if (result > 0) {
                            loadFoodList();
                            Toast.makeText(getContext(), "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại danh sách sản phẩm mỗi khi Fragment trở nên hiển thị
        loadFoodList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Dọn dẹp tham chiếu
        ivProductImage = null;
        currentDialog = null;
    }
}