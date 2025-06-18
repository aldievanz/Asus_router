package com.example.product_bottomnav.ui.product;

import java.io.Serializable;

public class Product implements Serializable {
    private String kategori;
    private String kode;
    private String merk;
    private int id;
    private double hargajual;
    private int stok;
    private String foto;
    private float rating;
    private int total_review;
    private String deskripsi;
    private int viewer;

    // diperbaiki jadi String

    // Konstruktor lengkap termasuk deskripsi
    public Product(String kategori, String kode, String merk, double hargajual, int stok,
                   String foto, float rating, int total_review, String deskripsi) {
        this.kategori = kategori;
        this.kode = kode;
        this.merk = merk;
        this.hargajual = hargajual;
        this.stok = stok;
        this.foto = foto;
        this.rating = rating;
        this.total_review = total_review;
        this.deskripsi = deskripsi;
    }

    // Getter
    public String getKategori() { return kategori; }
    public String getKode() { return kode; }
    public String getMerk() { return merk; }
    public double getHargajual() { return hargajual; }
    public int getStok() { return stok; }
    public String getFoto() { return foto; }
    public float getRating() { return rating; }
    public int getTotalReview() { return total_review; }
    public String getDeskripsi() { return deskripsi; }
    public int getViewer() {
        return viewer;
    }
    public int getId() {
        return id;
    }

    // Setter
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setKode(String kode) { this.kode = kode; }
    public void setMerk(String merk) { this.merk = merk; }
    public void setHargajual(double hargajual) { this.hargajual = hargajual; }
    public void setStok(int stok) { this.stok = stok; }
    public void setFoto(String foto) { this.foto = foto; }
    public void setRating(float rating) { this.rating = rating; }
    public void setTotalReview(int totalReview) { this.total_review = totalReview; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setId(int id) {
        this.id = id;
    }
    public void setViewer(int viewer) {
        this.viewer = viewer;
    }



}
