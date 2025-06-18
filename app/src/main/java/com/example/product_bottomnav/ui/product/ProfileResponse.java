package com.example.product_bottomnav.ui.product;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {
    @SerializedName("kode")
    private int kode;

    @SerializedName("pesan")
    private String pesan;

    @SerializedName("nama")
    private String nama;

    @SerializedName("alamat")
    private String alamat;

    @SerializedName("kota")
    private String kota;

    @SerializedName("provinsi")
    private String provinsi;

    @SerializedName("telp")
    private String telp;

    @SerializedName("kodepos")
    private String kodepos;

    @SerializedName("profile_image")
    private String profileImage;

    // Getter methods
    public int getKode() {
        return kode;
    }

    public String getPesan() {
        return pesan;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getKota() {
        return kota;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public String getTelp() {
        return telp;
    }

    public String getKodepos() {
        return kodepos;
    }

    public String getProfileImage() {
        return profileImage;
    }
}