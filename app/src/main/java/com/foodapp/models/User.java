package com.foodapp.models;

public class User {
    private int maTV;
    private String maND;
    private String hoTen;
    private String matKhau;
    private String email;
    private String namSinh;
    private String sdt;
    private String loaiTaiKhoan;

    public User() {
    }

    public User(int maTV, String maND, String hoTen, String matKhau, String email, String namSinh, String sdt, String loaiTaiKhoan) {
        this.maTV = maTV;
        this.maND = maND;
        this.hoTen = hoTen;
        this.matKhau = matKhau;
        this.email = email;
        this.namSinh = namSinh;
        this.sdt = sdt;
        this.loaiTaiKhoan = loaiTaiKhoan;
    }

    public User(String maND, String hoTen, String matKhau, String email, String namSinh, String sdt) {
        this.maND = maND;
        this.hoTen = hoTen;
        this.matKhau = matKhau;
        this.email = email;
        this.namSinh = namSinh;
        this.sdt = sdt;
    }

    public int getMaTV() {
        return maTV;
    }

    public void setMaTV(int maTV) {
        this.maTV = maTV;
    }

    public String getMaND() {
        return maND;
    }

    public void setMaND(String maND) {
        this.maND = maND;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNamSinh() {
        return namSinh;
    }

    public void setNamSinh(String namSinh) {
        this.namSinh = namSinh;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getLoaiTaiKhoan() {
        return loaiTaiKhoan;
    }

    public void setLoaiTaiKhoan(String loaiTaiKhoan) {
        this.loaiTaiKhoan = loaiTaiKhoan;
    }
}