package com.foodapp.models;

public class CartItem {
    private int maSanPham;
    private String tenSanPham;
    private String tenDoAnPhu;
    private double giaSanPham;
    private int soLuong;
    private String anhSanPham;
    private boolean isSelected;
    private int maTV;

    public CartItem() {
    }

    public CartItem(int maSanPham,int maTV, String tenSanPham, String tenDoAnPhu, double giaSanPham, int soLuong, String anhSanPham) {
        this.maSanPham = maSanPham;
        this.maTV = maTV;
        this.tenSanPham = tenSanPham;
        this.tenDoAnPhu = tenDoAnPhu;
        this.giaSanPham = giaSanPham;
        this.soLuong = soLuong;
        this.anhSanPham = anhSanPham;
        this.isSelected = false;
    }

    public int getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(int maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getTenDoAnPhu() {
        return tenDoAnPhu;
    }

    public void setTenDoAnPhu(String tenDoAnPhu) {
        this.tenDoAnPhu = tenDoAnPhu;
    }

    public double getGiaSanPham() {
        return giaSanPham;
    }

    public void setGiaSanPham(double giaSanPham) {
        this.giaSanPham = giaSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getAnhSanPham() {
        return anhSanPham;
    }

    public void setAnhSanPham(String anhSanPham) {
        this.anhSanPham = anhSanPham;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Tính tổng tiền cho sản phẩm (giá * số lượng)
    public double getTotalPrice() {
        return giaSanPham * soLuong;
    }

    public int getMaTV() {
        return maTV;
    }
    public void setMaTV(int maTV) {
        this.maTV = maTV;
    }

}