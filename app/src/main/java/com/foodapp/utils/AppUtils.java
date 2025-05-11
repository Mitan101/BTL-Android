package com.foodapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Lớp tiện ích chứa các phương thức dùng chung trong ứng dụng
 */
public class AppUtils {

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    /**
     * Định dạng số tiền theo VND
     *
     * @param amount Số tiền cần định dạng
     * @return Chuỗi tiền VND đã định dạng
     */
    public static String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }

    /**
     * Lấy ngày hiện tại theo định dạng dd/MM/yyyy
     *
     * @return Chuỗi ngày hiện tại
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Lấy thời gian hiện tại theo định dạng HH:mm:ss
     *
     * @return Chuỗi thời gian hiện tại
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Lấy ngày và thời gian hiện tại theo định dạng dd/MM/yyyy HH:mm:ss
     *
     * @return Chuỗi ngày và thời gian hiện tại
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Kiểm tra kết nối internet
     *
     * @param context Context của ứng dụng
     * @return true nếu có kết nối internet, false nếu không
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Ẩn bàn phím ảo
     *
     * @param activity Activity hiện tại
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Hiển thị dialog xác nhận
     *
     * @param context      Context của ứng dụng
     * @param title        Tiêu đề dialog
     * @param message      Nội dung dialog
     * @param positiveText Nội dung nút xác nhận
     * @param negativeText Nội dung nút hủy
     * @param listener     Listener xử lý sự kiện click
     */
    public static void showConfirmDialog(Context context, String title, String message,
                                         String positiveText, String negativeText,
                                         DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, listener);
        builder.setNegativeButton(negativeText, null);
        builder.show();
    }

    /**
     * Hiện thông báo nhanh
     *
     * @param context Context của ứng dụng
     * @param message Nội dung thông báo
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Hiển thị dialog thông báo
     *
     * @param context Context của ứng dụng
     * @param title   Tiêu đề dialog
     * @param message Nội dung dialog
     */
    public static void showInfoDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}