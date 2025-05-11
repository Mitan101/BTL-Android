# Hướng dẫn tái cấu trúc dự án Food Order App

## Cấu trúc đã được tạo

Dự án đã được khởi tạo với cấu trúc mới như sau:

```
com.foodapp/
  ├── activities/
  │   ├── MainActivity.java
  │   ├── LoginActivity.java
  │   └── HomeActivity.java
  ├── fragments/
  ├── adapters/
  │   └── FoodAdapter.java
  ├── models/
  │   ├── User.java
  │   ├── Food.java
  │   ├── Category.java
  │   ├── Order.java
  │   └── CartItem.java
  ├── database/
  │   ├── DatabaseHelper.java
  │   └── dao/
  │       ├── UserDao.java
  │       ├── FoodDao.java
  │       ├── CategoryDao.java
  │       ├── OrderDao.java
  │       └── CartDao.java
  └── utils/
      └── SharedPreferencesManager.java
```

## Các bước cần thực hiện để hoàn thiện

### 1. Tạo cấu trúc thư mục Fragment

```powershell
mkdir -Force app\src\main\java\com\foodapp\fragments\admin
mkdir -Force app\src\main\java\com\foodapp\fragments\user  
mkdir -Force app\src\main\java\com\foodapp\fragments\chef
```

### 2. Tạo các Fragment cần thiết

Chuyển các Fragment từ cấu trúc cũ sang cấu trúc mới và đổi tên phù hợp:

- **Admin Fragments**:
    - `Fragment_Admin_DoanhThu` → `AdminRevenueFragment`
    - `Fragment_Admin_DonHang` → `AdminOrderFragment`
    - `Fragment_Admin_QL_ND` → `AdminUserFragment`
    - `Fragment_Admin_QL_LoaiSP` → `AdminCategoryFragment`
    - `Fragment_Admin_SP_phu` → `AdminProductFragment`
    - `Fragment_Admin_ThemSP` → `AdminDashboardFragment`
    - `Fragment_Admin_BanAn` → `AdminTableFragment`

- **User Fragments**:
    - `Fragment_User_DanhSachSP` → `UserProductFragment`
    - `Fragment_User_LoaiSP` → `UserCategoryFragment`
    - `Fragment_DoiMatKhau` → `ChangePasswordFragment`

- **Chef Fragments**:
    - `FragmentDonHangDauBep` → `ChefOrderFragment`

### 3. Tạo các Activity thiếu

- `FoodDetailActivity` (từ `ChiTietSPActivity`)
- `OrderHistoryActivity` (từ `HoaDonUserActivity`)
- `UserProfileActivity` (từ `TaiKhoanNDActivity`)
- `SignUpActivity` (từ `SingUpActivity`)
- `ForgotPasswordActivity` (từ `QuenMKActivity`)
- `CartActivity` (từ `GioHangActivity`)
- `CheckoutActivity` (từ `DienThongTinActivity`)
- `CartCheckoutActivity` (từ `DienThongTinTuGHActivity`)

### 4. Tạo các resource cần thiết

#### Cập nhật và chỉnh sửa các file layout

Các file layout cần được sửa để tham chiếu đến các ID chính xác:

```xml
<!-- Ví dụ: activity_main.xml -->
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- Các thành phần UI -->
    
</RelativeLayout>
```

#### Cập nhật các file menu, string, drawable

Thêm các chuỗi cần thiết vào `strings.xml`:

```xml
<resources>
    <string name="app_name">Food App</string>
    <string name="open">Open</string>
    <string name="close">Close</string>
    <!-- Thêm các chuỗi khác -->
</resources>
```

### 5. Kiểm tra và sửa lỗi

Sau khi di chuyển tất cả các file, cần kiểm tra và sửa lỗi:

1. Đảm bảo tất cả các import đề cập đến `com.foodapp.*` thay vì `android.project.order.*`
2. Cập nhật các tham chiếu đến `R` để sử dụng `com.foodapp.R`
3. Xử lý các lỗi không tìm thấy các lớp hoặc resource

### 6. Cập nhật AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.FoodApp"
        tools:targetApi="31">
        <!-- Khai báo các Activity mới -->
        <activity android:name=".activities.MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".activities.LoginActivity" android:exported="false" />
        <activity android:name=".activities.HomeActivity" android:exported="false" />
        <!-- Thêm các Activity khác -->
    </application>

</manifest>
```

## Quy tắc đặt tên và phong cách lập trình

1. **Tên lớp**: PascalCase (vd: FoodAdapter)
2. **Tên biến và phương thức**: camelCase (vd: getUserById)
3. **Hằng số**: UPPER_SNAKE_CASE (vd: MAX_LOGIN_ATTEMPTS)
4. **ID trong XML**: snake_case (vd: button_login)
5. **Sử dụng các hậu tố để chỉ rõ loại lớp**: Activity, Fragment, Adapter, Dao

## Lưu ý quan trọng

- Đảm bảo build.gradle đã được cập nhật với namespace và applicationId mới: `com.foodapp`
- Khi gặp lỗi về resources, kiểm tra xem đã di chuyển tất cả các resource cần thiết chưa
- Sử dụng các phương pháp tiêu chuẩn của Android như RecyclerView thay vì ListView, Fragments cho UI
  linh hoạt
- Hãy xem xét sử dụng ViewModel và LiveData để cải thiện kiến trúc (có thể thêm vào trong tương lai)