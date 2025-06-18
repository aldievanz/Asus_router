package com.example.product_bottomnav.ui.product;

import com.google.gson.annotations.SerializedName;

public class User {

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

    @SerializedName("foto_profil")
    private String fotoProfil;

    // Getter

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

    public String getFotoProfil() {
        return fotoProfil;
    }
}
