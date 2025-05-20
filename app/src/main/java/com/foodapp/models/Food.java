package com.foodapp.models;

public class Food {
    private int maDoAn;
    private String tenDoAn;
    private double giaDoAn;
    private int maLoai;
    private String thongTin;
    private String linkAnh;

    public Food() {
    }

    public Food(int maDoAn, String tenDoAn, double giaDoAn, int maLoai, String thongTin, String linkAnh) {
        this.maDoAn = maDoAn;
        this.tenDoAn = tenDoAn;
        this.giaDoAn = giaDoAn;
        this.maLoai = maLoai;
        this.thongTin = thongTin;
        this.linkAnh = linkAnh;
    }

    public int getMaDoAn() {
        return maDoAn;
    }

    public void setMaDoAn(int maDoAn) {
        this.maDoAn = maDoAn;
    }

    public String getTenDoAn() {
        return tenDoAn;
    }

    public void setTenDoAn(String tenDoAn) {
        this.tenDoAn = tenDoAn;
    }

    public double getGiaDoAn() {
        return giaDoAn;
    }

    public void setGiaDoAn(double giaDoAn) {
        this.giaDoAn = giaDoAn;
    }

    public int getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(int maLoai) {
        this.maLoai = maLoai;
    }

    public String getThongTin() {
        return thongTin;
    }

    public void setThongTin(String thongTin) {
        this.thongTin = thongTin;
    }

    public String getLinkAnh() {
        return linkAnh;
    }

    public void setLinkAnh(String linkAnh) {
        this.linkAnh = linkAnh;
    }
}