package com.example.product_bottomnav.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.product_bottomnav.ui.product.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderHelper {
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "order_prefs";
    private static final String ORDER_KEY_PREFIX = "order_items_user_";
    private Gson gson = new Gson();
    private int userId;

    public OrderHelper(Context context, int userId) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.userId = userId;
    }

    // Menambahkan produk ke pesanan
    public void addToOrder(Product product) {
        List<OrderItem> orderItems = getOrderItems();
        boolean exists = false;

        // Cek jika produk sudah ada, tinggal update jumlahnya
        for (OrderItem item : orderItems) {
            if (item.getKode().equals(product.getKode())) {
                item.setQuantity(item.getQuantity() + 1);
                exists = true;
                break;
            }
        }

        // Jika belum ada, tambahkan produk baru
        if (!exists) {
            orderItems.add(new OrderItem(product));
        }

        saveOrderItems(orderItems);
    }

    // Menghapus produk dari pesanan
    public void removeFromOrder(String productCode) {
        List<OrderItem> orderItems = getOrderItems();
        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).getKode().equals(productCode)) {
                orderItems.remove(i);
                break;
            }
        }
        saveOrderItems(orderItems);
    }

    // Update jumlah produk dalam pesanan
    public void updateQuantity(String productCode, int quantity) {
        List<OrderItem> orderItems = getOrderItems();
        for (OrderItem item : orderItems) {
            if (item.getKode().equals(productCode)) {
                item.setQuantity(quantity);
                break;
            }
        }
        saveOrderItems(orderItems);
    }

    // Mendapatkan semua item pesanan
    public List<OrderItem> getOrderItems() {
        String json = sharedPreferences.getString(ORDER_KEY_PREFIX + userId, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<OrderItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // Menghitung total harga pesanan
    public double getTotal() {
        double total = 0;
        for (OrderItem item : getOrderItems()) {
            total += item.getSubtotal();
        }
        return total;
    }

    // Menyimpan pesanan ke SharedPreferences
    private void saveOrderItems(List<OrderItem> items) {
        String json = gson.toJson(items);
        sharedPreferences.edit().putString(ORDER_KEY_PREFIX + userId, json).apply();
    }

    // Membersihkan semua pesanan (dipanggil setelah checkout berhasil)
    public void clearOrder() {
        sharedPreferences.edit().remove(ORDER_KEY_PREFIX + userId).apply();
    }
}