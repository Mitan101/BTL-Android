package com.foodapp.models;

public class Order {
    private int maHoaDon;
    private String email;
    private String hoTen;
    private String sdt;
    private String diaChi;
    private String thucDon;
    private String ngayDat;
    private double tongTien;
    private String thanhToan;
    private String trangThai;

    public Order() {
    }

    public Order(int maHoaDon, String email, String hoTen, String sdt, String diaChi, String thucDon, String ngayDat, double tongTien, String thanhToan, String trangThai) {
        this.maHoaDon = maHoaDon;
        this.email = email;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.thucDon = thucDon;
        this.ngayDat = ngayDat;
        this.tongTien = tongTien;
        this.thanhToan = thanhToan;
        this.trangThai = trangThai;
    }

    public int getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(int maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getThucDon() {
        return thucDon;
    }

    public void setThucDon(String thucDon) {
        this.thucDon = thucDon;
    }

    public String getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(String ngayDat) {
        this.ngayDat = ngayDat;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public String getThanhToan() {
        return thanhToan;
    }

    public void setThanhToan(String thanhToan) {
        this.thanhToan = thanhToan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}