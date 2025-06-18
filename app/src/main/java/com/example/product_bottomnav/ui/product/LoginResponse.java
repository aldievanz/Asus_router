package com.example.product_bottomnav.ui.product;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("result")
    private int result;

    @SerializedName("data")
    private User data;

    public int getResult() {
        return result;
    }

    public User getData() {
        return data;
    }

    public static class User {
        @SerializedName("id")
        private String id;
        @SerializedName("nama")
        private String nama;
        @SerializedName("email")
        private String email;
        @SerializedName("password")
        private String password;
        @SerializedName("profile_image")
        private String profile_image;


        public String getId() { return id; }
        public String getNama() { return nama; }
        public String getEmail() { return email; }
        public String getProfile_image() { return profile_image; }
        public String getPassword() { return password; }
        public void setId(String id) { this.id = id; }
        public void setNama(String nama) { this.nama = nama; }
        public void setEmail(String email) { this.email = email; }
        public void setProfile_image(String profile_image) { this.profile_image = profile_image; }
        public void setPassword(String password) { this.password = password; }
    }
}