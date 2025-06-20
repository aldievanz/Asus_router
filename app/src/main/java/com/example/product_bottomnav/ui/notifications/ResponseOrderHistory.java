package com.example.product_bottomnav.ui.notifications;

import com.example.product_bottomnav.ui.model.OrderModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseOrderHistory {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<OrderModel> data;

    @SerializedName("error")
    private boolean error;

    @SerializedName("error_message")
    private String errorMessage;

    public boolean isStatus() {
        return status && !error;
    }

    public String getMessage() {
        return message != null ? message : errorMessage;
    }

    public List<OrderModel> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ResponseOrderHistory{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + (data != null ? data.size() : "null") +
                ", error=" + error +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}