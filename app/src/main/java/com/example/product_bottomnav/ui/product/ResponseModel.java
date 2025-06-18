package com.example.product_bottomnav.ui.product;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseModel implements Parcelable {
    private boolean success;
    private String message;
    private JSONObject data;

    public ResponseModel() {
    }

    public ResponseModel(boolean success, String message, JSONObject data) {
        this.success = success;
        this.message = message != null ? message : "";
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public JSONObject getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message != null ? message : "";
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    // Parcelable implementation
    protected ResponseModel(Parcel in) {
        success = in.readByte() != 0;
        message = in.readString();
        try {
            String jsonString = in.readString();
            data = jsonString != null ? new JSONObject(jsonString) : null;
        } catch (JSONException e) {
            e.printStackTrace();
            data = null;
        }
    }

    public static final Creator<ResponseModel> CREATOR = new Creator<ResponseModel>() {
        @Override
        public ResponseModel createFromParcel(Parcel in) {
            return new ResponseModel(in);
        }

        @Override
        public ResponseModel[] newArray(int size) {
            return new ResponseModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
        dest.writeString(data != null ? data.toString() : null);
    }
}
