package com.example.product_bottomnav.ui.product;

import com.google.gson.annotations.SerializedName;

public class DataModel {
    @SerializedName("id")
    private String id;

    @SerializedName("nama")
    private String nama;

    @SerializedName("email")
    private String email;

    // Getter
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }

    // Setter (kalau perlu)
    public void setId(String id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setEmail(String email) { this.email = email; }
}