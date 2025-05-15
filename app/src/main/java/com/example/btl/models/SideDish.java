package com.example.btl.models;

public class SideDish {
    private int maDoAnPhu;
    private String tenDoAnPhu;
    private String anh;
    private double gia;

    public SideDish() {
    }

    public SideDish(int maDoAnPhu, String tenDoAnPhu) {
        this.maDoAnPhu = maDoAnPhu;
        this.tenDoAnPhu = tenDoAnPhu;
    }

    public SideDish(int maDoAnPhu, String tenDoAnPhu, String anh, double gia) {
        this.maDoAnPhu = maDoAnPhu;
        this.tenDoAnPhu = tenDoAnPhu;
        this.anh = anh;
        this.gia = gia;
    }

    public int getMaDoAnPhu() {
        return maDoAnPhu;
    }

    public void setMaDoAnPhu(int maDoAnPhu) {
        this.maDoAnPhu = maDoAnPhu;
    }

    public String getTenDoAnPhu() {
        return tenDoAnPhu;
    }

    public void setTenDoAnPhu(String tenDoAnPhu) {
        this.tenDoAnPhu = tenDoAnPhu;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }
}
