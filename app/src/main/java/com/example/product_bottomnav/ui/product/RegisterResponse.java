package com.example.product_bottomnav.ui.product;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("result")
    private int result;

    @SerializedName("message")
    private String message;

    public int getStatus() {
        return status;
    }

    public int getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
