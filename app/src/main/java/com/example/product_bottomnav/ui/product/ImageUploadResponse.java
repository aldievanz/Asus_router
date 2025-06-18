package com.example.product_bottomnav.ui.product;

import com.google.gson.annotations.SerializedName;

public class ImageUploadResponse {

    @SerializedName("kode")
    private int kode;

    @SerializedName("pesan")
    private String pesan;

    @SerializedName("image_url")
    private String image_url;

    public int getKode() {
        return kode;
    }

    public String getPesan() {
        return pesan;
    }

    public String getImageUrl() {
        return image_url; // ✅ gunakan nama field yg sama dengan deklarasi
    }
}
