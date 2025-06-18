package com.example.product_bottomnav.ui.dashboard;

import android.os.Parcel;
import android.os.Parcelable;
import com.example.product_bottomnav.ui.product.Product;

public class OrderItem implements Parcelable {
    private String kode;
    private String merk;
    private String foto;
    private double hargajual;
    private int quantity;

    // Constructor from Product
    public OrderItem(Product product) {
        this.kode = product.getKode();
        this.merk = product.getMerk();
        this.foto = product.getFoto();
        this.hargajual = product.getHargajual();
        this.quantity = 1; // Default quantity when first added
    }

    // Regular constructor
    public OrderItem(String kode, String merk, String foto, double hargajual, int quantity) {
        this.kode = kode;
        this.merk = merk;
        this.foto = foto;
        this.hargajual = hargajual;
        this.quantity = quantity;
    }

    // Parcelable implementation
    protected OrderItem(Parcel in) {
        kode = in.readString();
        merk = in.readString();
        foto = in.readString();
        hargajual = in.readDouble();
        quantity = in.readInt();
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kode);
        dest.writeString(merk);
        dest.writeString(foto);
        dest.writeDouble(hargajual);
        dest.writeInt(quantity);
    }

    // Getters
    public String getKode() {
        return kode;
    }

    public String getMerk() {
        return merk;
    }

    public String getFoto() {
        return foto;
    }

    public double getHargajual() {
        return hargajual;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Helper methods
    public double getSubtotal() {
        return hargajual * quantity;
    }
}