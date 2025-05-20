package com.foodapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foodapp.utils.SharedPreferencesManager;

/**
 * Fragment cơ sở cung cấp các phương thức hỗ trợ chung cho tất cả các Fragment khác
 */
public abstract class BaseFragment extends Fragment {

    protected SharedPreferencesManager prefsManager;
    private AlertDialog loadingDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Khởi tạo SharedPreferencesManager
        prefsManager = new SharedPreferencesManager(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Gọi phương thức khởi tạo dữ liệu cho Fragment
        initData();
    }

    /**
     * Hiển thị thông báo ngắn
     *
     * @param message Nội dung thông báo
     */
    protected void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hiển thị dialog loading
     */
    protected void showLoading() {
        if (getContext() != null && loadingDialog == null) {
            ProgressBar progressBar = new ProgressBar(getContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(progressBar);
            builder.setCancelable(false);
            loadingDialog = builder.create();
            loadingDialog.show();
        }
    }

    /**
     * Ẩn dialog loading
     */
    protected void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    /**
     * Phương thức khởi tạo dữ liệu, các Fragment kế thừa sẽ override
     */
    protected abstract void initData();
}