package com.example.btl.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.btl.utils.Constants;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Bảng người dùng
        String sql_nguoidung = "CREATE TABLE " + Constants.TABLE_USER + " (\n" +
                "    maTV INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    MaND    TEXT NOT NULL,\n" +
                "    HoTen   TEXT NOT NULL,\n" +
                "    MatKhau TEXT NOT NULL,\n" +
                "    Email   TEXT NOT NULL,\n" +
                "    NamSinh TEXT ,\n" +
                "    SDT     TEXT,\n" +
                "    LoaiTaiKhoan TEXT\n" +
                ");\n";
        sqLiteDatabase.execSQL(sql_nguoidung);
        String mauUSer = "INSERT INTO " + Constants.TABLE_USER + " " +
                "VALUES (1,'admin','Quản lý','admin','helo@gmail.com','2004','012345678','" + Constants.USER_TYPE_ADMIN + "')," +
                "(2,'nguoidung01','Người dùng','nguoidung01','nguoidung@gmail.com','2005','123456789','" + Constants.USER_TYPE_USER + "')," +
                "(3,'daubep','Đầu bếp','daubep','daubep@gmail.com','2002','123456789','" + Constants.USER_TYPE_CHEF + "')";
        sqLiteDatabase.execSQL(mauUSer);

        // Bảng loại đồ ăn
        String sql_Loai = "CREATE TABLE " + Constants.TABLE_CATEGORY + " (maloai integer primary key not null, tenloai text not null)";
        sqLiteDatabase.execSQL(sql_Loai);
        String mauLoai = "INSERT INTO " + Constants.TABLE_CATEGORY + " values('1','cơm'),('2','bún'),('3','xôi'),('4','cháo')";
        sqLiteDatabase.execSQL(mauLoai);

        // Bảng đồ ăn
        String sql_doan = "CREATE TABLE " + Constants.TABLE_FOOD +
                "(madoan integer primary key not null ,tendoan text not null,giadoan integer not null, " +
                "maloai integer references " + Constants.TABLE_CATEGORY + "(maloai) not null,thongtin text not null,anh text )";
        sqLiteDatabase.execSQL(sql_doan);
        String doan_1 = "INSERT INTO " + Constants.TABLE_FOOD + " values('1','cơm rang',30000,1,'Cơm rất ngon','https://lh3.googleusercontent.com/CB-FFqhq6t5UbEnTKo0Rw6fX1gtO89k4ZPDZLHDNW09Gv9JH89xeaqohwsq6xzfuEHAooiFLhMbDgl_zkKrRP8fBLZk=w622')";
        sqLiteDatabase.execSQL(doan_1);
        String doan_2 = "INSERT INTO " + Constants.TABLE_FOOD + " values('2','bún chả ',30000,2,'Bún rất ngon','https://bizweb.dktcdn.net/100/442/328/products/bun-cha-ha-noi.jpg?v=1644892472637')";
        sqLiteDatabase.execSQL(doan_2);
        String doan_3 = "INSERT INTO " + Constants.TABLE_FOOD + " values('3','Xôi ngô',15000,3,'Xôi rất dẻo và thơm','https://i-giadinh.vnecdn.net/2022/02/25/Thanh-pham-1-6778-1645781140.jpg')";
        sqLiteDatabase.execSQL(doan_3);
        String doan_4 = "INSERT INTO " + Constants.TABLE_FOOD + " values('4','Cháo thịt',10000,4,'Cháo rất giàu dinh dưỡng','https://cdn.tgdd.vn/Files/2021/08/19/1376391/cach-nau-chao-coi-so-diep-don-gian-dinh-duong-tot-cho-suc-khoe-ca-nha-202201191541013201.jpg')";
        sqLiteDatabase.execSQL(doan_4);

        // Bảng đồ ăn phụ
        String sql_doanphu = "CREATE TABLE " + Constants.TABLE_SIDE_DISH + " (\n" +
                "    MaDoAnPhu  INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    TenDoAnPhu TEXT NOT NULL,\n" +
                "    anh TEXT,\n" +
                "    Gia REAL DEFAULT 0\n" +
                ");\n";
        sqLiteDatabase.execSQL(sql_doanphu);
        String doanphu = "INSERT INTO " + Constants.TABLE_SIDE_DISH + "(TenDoAnPhu, Gia) VALUES ('Giò', 15000),('Chả', 20000),('Nem', 18000),('Trứng', 10000)";
        sqLiteDatabase.execSQL(doanphu);

        // Bảng hóa đơn
        String sql_hoadon = "CREATE TABLE " + Constants.TABLE_ORDER +
                "(mahoadon integer primary key,Email text not null,hoten text,SDT text not null," +
                "diachinhan text not null, thucdon text ,ngaydathang text,tongtien integer ,thanhtoan text,trangthai text)";
        sqLiteDatabase.execSQL(sql_hoadon);

        // Bảng giỏ hàng
        String giohang = "CREATE TABLE " + Constants.TABLE_CART +
                "(masp integer primary key not null ,tensp text not null,tendoanphu text," +
                "giasp integer not null,soluong integer,anhsp text, table_info text)";
        sqLiteDatabase.execSQL(giohang);

        // Bảng bàn ăn
        String sql_ban = "CREATE TABLE " + Constants.TABLE_TABLE + "(maban integer primary key not null ,tenban text not null)";
        sqLiteDatabase.execSQL(sql_ban);
        String ban = "INSERT INTO " + Constants.TABLE_TABLE + "(tenban) VALUES ('Bàn số 1'),('Bàn số 2'),('Bàn số 3'),('Bàn số 4')";
        sqLiteDatabase.execSQL(ban);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // For major version changes, recreate the database
            sqLiteDatabase.execSQL("DROP TABLE if exists " + Constants.TABLE_USER);
            sqLiteDatabase.execSQL("DROP TABLE if exists " + Constants.TABLE_FOOD);
            sqLiteDatabase.execSQL("DROP TABLE if exists " + Constants.TABLE_CATEGORY);
            sqLiteDatabase.execSQL("DROP TABLE if exists " + Constants.TABLE_SIDE_DISH);
            sqLiteDatabase.execSQL("DROP TABLE if exists " + Constants.TABLE_ORDER);
            sqLiteDatabase.execSQL("DROP TABLE if exists " + Constants.TABLE_CART);
            sqLiteDatabase.execSQL("DROP TABLE if exists " + Constants.TABLE_TABLE);
            onCreate(sqLiteDatabase);

    }
}