package com.foodapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.foodapp.utils.Constants;
import com.google.android.material.navigation.NavigationView;
import com.foodapp.R;
import com.foodapp.database.dao.UserDao;
import com.foodapp.fragments.admin.AdminCategoryFragment;
import com.foodapp.fragments.admin.AdminOrderFragment;
import com.foodapp.fragments.admin.AdminProductFragment;
import com.foodapp.fragments.admin.AdminRevenueFragment;
import com.foodapp.fragments.admin.AdminSideDishFragment;
import com.foodapp.fragments.admin.AdminUserFragment;
import com.foodapp.fragments.user.ChangePasswordFragment;
import com.foodapp.fragments.user.UserCategoryFragment;
import com.foodapp.fragments.user.UserProductFragment;
import com.foodapp.models.User;
import com.foodapp.utils.SharedPreferencesManager;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FragmentContainerView fragmentContainer;
    private FragmentManager fragmentManager;
    private View headerView;
    private UserDao userDao;
    private TextView nameTextView;
    private String userId;
    private SharedPreferencesManager prefsManager;

    // Khởi tạo các fragment một lần để tái sử dụng
    private AdminProductFragment adminProductFragment;
    private AdminOrderFragment adminOrderFragment;
    private AdminSideDishFragment adminSideDishFragment;
    private AdminCategoryFragment adminCategoryFragment;
    private AdminUserFragment adminUserFragment;
    private AdminRevenueFragment adminRevenueFragment;
    private UserProductFragment userProductFragment;
    private UserCategoryFragment userCategoryFragment;
    private ChangePasswordFragment changePasswordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ view
        drawerLayout = findViewById(R.id.home_all);
        toolbar = findViewById(R.id.home_toobal);
        fragmentContainer = findViewById(R.id.home_fragment);

        // Khởi tạo SharedPreferencesManager
        prefsManager = new SharedPreferencesManager(this);

        // Cấu hình Toolbar và DrawerLayout
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);

        // Khởi tạo các fragments
        adminRevenueFragment = new AdminRevenueFragment();
        adminOrderFragment = new AdminOrderFragment();
        adminCategoryFragment = new AdminCategoryFragment();
        adminUserFragment = new AdminUserFragment();
        adminProductFragment = new AdminProductFragment();
        adminSideDishFragment = new AdminSideDishFragment();

        userProductFragment = new UserProductFragment();
        userCategoryFragment = new UserCategoryFragment();
        changePasswordFragment = new ChangePasswordFragment();

        // Cấu hình NavigationView và headerView
        navigationView = findViewById(R.id.main_nav_view);
        headerView = navigationView.getHeaderView(0);
        nameTextView = headerView.findViewById(R.id.nav_header_name);

        // Lấy thông tin người dùng từ SharedPreferencesManager
        userDao = new UserDao(this);
        userId = prefsManager.getUserId();
        User user = userDao.getById(userId);
        if (user != null) {
            nameTextView.setText("Chào mừng " + user.getHoTen() + "!");
        } else {
            nameTextView.setText("Chào mừng!");
            // Kiểm tra nếu đã đăng nhập nhưng không tìm thấy thông tin người dùng
            if (prefsManager.isLoggedIn()) {
                nameTextView.setText("Chào mừng " + prefsManager.getFullName() + "!");
            } else {
                // Nếu không đăng nhập, chuyển về màn hình login
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }

        // Hiện thị menu theo loại người dùng
        fragmentManager = getSupportFragmentManager();

        String userRole = user != null ? user.getLoaiTaiKhoan() : null;
        if (userRole != null) {
            if (userRole.equalsIgnoreCase(Constants.USER_TYPE_ADMIN)) {
                // Hiển thị menu cho admin
                navigationView.getMenu().findItem(R.id.admin).setVisible(true);
                navigationView.getMenu().findItem(R.id.doanhthu).setVisible(true);

                // Chọn mặc định mục quản lý sản phẩm
                MenuItem menuItem = navigationView.getMenu().findItem(R.id.home_admin_themSP);
                if (menuItem != null) {
                    selectNavigationItem(menuItem);
                    fragmentManager.beginTransaction().replace(R.id.home_fragment, adminProductFragment).commitNowAllowingStateLoss();
                }
            } else {
                // Hiển thị menu cho người dùng
                navigationView.getMenu().findItem(R.id.user).setVisible(true);

                // Chọn mặc định mục danh sách sản phẩm
                MenuItem menuItem = navigationView.getMenu().findItem(R.id.home_user_danhsachSP);
                if (menuItem != null) {
                    selectNavigationItem(menuItem);
                    fragmentManager.beginTransaction().replace(R.id.home_fragment, userProductFragment).commit();
                }
            }
        }

        // Xử lý sự kiện chọn item trên navigation drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home_admin_doanhthu) {
                fragmentManager.beginTransaction().replace(R.id.home_fragment, adminRevenueFragment).commit();
            } else if (itemId == R.id.home_admin_donhang) {
                fragmentManager.beginTransaction().replace(R.id.home_fragment, adminOrderFragment).commit();
            } else if (itemId == R.id.home_admin_ND) {
                fragmentManager.beginTransaction().replace(R.id.home_fragment, adminUserFragment).commit();
            } else if (itemId == R.id.home_admin_loaiSP) {
                fragmentManager.beginTransaction().replace(R.id.home_fragment, adminCategoryFragment).commit();
            } else if (itemId == R.id.home_admin_SP_phu || itemId == R.id.home_admin_themSP) {
                fragmentManager.beginTransaction().replace(R.id.home_fragment,
                                itemId == R.id.home_admin_SP_phu ? adminSideDishFragment : adminProductFragment)
                        .commit();
            } else if (itemId == R.id.home_user_danhsachSP) {
                fragmentManager.beginTransaction().replace(R.id.home_fragment, userProductFragment).commit();
            } else if (itemId == R.id.home_user_giohang) {
                Intent intentToCart = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intentToCart);
            } else if (itemId == R.id.home_user_hoadon) {
                Intent intentToOrder = new Intent(HomeActivity.this, OrderHistoryActivity.class);
                startActivity(intentToOrder);
            } else if (itemId == R.id.home_user_loaiSP) {
                fragmentManager.beginTransaction().replace(R.id.home_fragment, userCategoryFragment).commit();
            } else if (itemId == R.id.home_taikhoan) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent1 = new Intent(HomeActivity.this, UserProfileActivity.class);
                startActivity(intent1);
            } else if (itemId == R.id.home_doimatkhau) {
                fragmentManager.beginTransaction().replace(R.id.home_fragment, changePasswordFragment).commit();
            } else if (itemId == R.id.home_Dangxuat) {
                // Xóa thông tin đăng nhập nếu không lưu "ghi nhớ"
                if (!prefsManager.isRemembered()) {
                    prefsManager.clearAll();
                } else {
                    prefsManager.clearLoginDetails();
                }

                Intent intent1 = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
            }

            // Đánh dấu item được chọn
            selectNavigationItem(item);

            // Đóng drawer
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    // Phương thức để đánh dấu item được chọn trong NavigationView
    private void selectNavigationItem(MenuItem item) {
        // Đặt lại tất cả các item về trạng thái không chọn
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            MenuItem menuItem = navigationView.getMenu().getItem(i);
            if (menuItem.hasSubMenu()) {
                for (int j = 0; j < menuItem.getSubMenu().size(); j++) {
                    menuItem.getSubMenu().getItem(j).setChecked(false);
                }
            }
        }

        // Đặt item hiện tại là được chọn
        item.setChecked(true);

        // Cập nhật tiêu đề
        toolbar.setTitle(item.getTitle());
    }

    // Phương thức để lấy userId cho các fragment
    public String getUserId() {
        return userId;
    }
}