# Giao diện Admin

Thư mục này dành cho các Activity riêng của Admin.

Hiện tại, tất cả giao diện Admin được quản lý thông qua các Fragment trong `/fragments/admin/`:

- AdminCategoryFragment.java - Quản lý danh mục
- AdminDashboardFragment.java - Bảng điều khiển admin
- AdminOrderFragment.java - Quản lý đơn hàng
- AdminProductFragment.java - Quản lý sản phẩm
- AdminRevenueFragment.java - Quản lý doanh thu
- AdminSideDishFragment.java - Quản lý đồ ăn phụ
- AdminUserFragment.java - Quản lý người dùng

Các fragment này được hiển thị thông qua HomeActivity trong gói `common`.