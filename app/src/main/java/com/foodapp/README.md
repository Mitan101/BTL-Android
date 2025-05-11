# Hướng dẫn cấu trúc dự án Android Food App

## Cấu trúc thư mục

Dự án được tổ chức theo cấu trúc package chuẩn cho ứng dụng Android:

```
com.foodapp/
  ├── activities/       # Chứa các Activity
  │   ├── MainActivity.java
  │   ├── LoginActivity.java
  │   └── ...
  ├── fragments/        # Chứa các Fragment
  │   ├── admin/
  │   │   └── ...
  │   └── user/
  │       └── ...
  ├── adapters/         # Chứa các Adapter
  │   └── ...
  ├── models/           # Chứa các model dữ liệu
  │   ├── User.java
  │   ├── Food.java
  │   └── ...
  ├── database/         # Chứa các thành phần liên quan đến database
  │   ├── DatabaseHelper.java
  │   └── dao/
  │       ├── UserDao.java
  │       ├── FoodDao.java
  │       └── ...
  └── utils/            # Chứa các lớp tiện ích
      ├── SharedPreferencesManager.java
      └── ...
```

## Hướng dẫn chuyển đổi từ cấu trúc cũ

### 1. Thay đổi package

- Đã thay đổi package từ `android.project.order` thành `com.foodapp`
- Cập nhật `namespace` và `applicationId` trong file `build.gradle`

### 2. Đổi tên class

- Đã đổi tên các class theo quy tắc đặt tên chuẩn:
    - `User_DTO` -> `User`
    - `DoAn_DTO` -> `Food`
    - `User_DAO` -> `UserDao`
    - `DoAn_DAO` -> `FoodDao`
    - `MyDbHelper` -> `DatabaseHelper`

### 3. Tổ chức lại code

- Đã tách các lớp vào các package tương ứng
- Thêm Javadoc và comment để giải thích code

## Hướng dẫn tiếp tục chuyển đổi

1. Di chuyển các Activity còn lại vào package `activities`
2. Di chuyển các Fragment vào package `fragments`
3. Tạo các Adapter trong package `adapters`
4. Di chuyển các model vào package `models`
5. Di chuyển các DAO vào package `database/dao`
6. Tạo các lớp tiện ích trong package `utils`

**Lưu ý**: Khi di chuyển file, cần cập nhật:

- Package tại đầu file
- Import các class đã di chuyển
- Cập nhật tham chiếu đến R (Resource) với package mới

## Quy tắc đặt tên

- **Class**: PascalCase (ví dụ: MainActivity, UserDao)
- **Biến**: camelCase (ví dụ: userName, passwordEditText)
- **Hằng số**: UPPER_SNAKE_CASE (ví dụ: MAX_LOGIN_ATTEMPTS)
- **Layout**: snake_case (ví dụ: activity_main.xml, fragment_user_profile.xml)

## Tổ chức resource

- **layout**: Đặt tên theo quy tắc `type_name.xml`
    - `activity_main.xml`
    - `fragment_user_profile.xml`
    - `item_food.xml`
- **drawable**: Đặt tên theo quy tắc `type_name.xml`
    - `ic_back.xml`
    - `bg_button.xml`
- **values**: Phân loại resource
    - `colors.xml`
    - `strings.xml`
    - `styles.xml`
    - `dimens.xml`