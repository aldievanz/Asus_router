package com.example.product_bottomnav.ui.product;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private User data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getData() {
        return data;
    }
}
