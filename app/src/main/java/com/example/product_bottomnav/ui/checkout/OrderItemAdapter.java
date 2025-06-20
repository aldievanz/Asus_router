package com.example.product_bottomnav.ui.checkout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.product_bottomnav.R;
import com.example.product_bottomnav.ui.dashboard.OrderItem;
import com.example.product_bottomnav.ui.product.ServerAPI;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private List<OrderItem> daftarItem;
    private NumberFormat formatRupiah;

    // Constructor
    public OrderItemAdapter(List<OrderItem> daftarItem) {
        this.daftarItem = daftarItem != null ? daftarItem : new ArrayList<>();
        this.formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        this.formatRupiah.setMaximumFractionDigits(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = daftarItem.get(position);
        holder.tampilkanData(item);
    }

    @Override
    public int getItemCount() {
        return daftarItem.size();
    }

    // Method untuk mendapatkan daftar item
    public List<OrderItem> getOrderItems() {
        return new ArrayList<>(daftarItem); // Return copy untuk menghindari modifikasi langsung
    }

    // Method untuk memperbarui data
    public void updateData(List<OrderItem> dataBaru) {
        this.daftarItem.clear();
        if (dataBaru != null) {
            this.daftarItem.addAll(dataBaru);
        }
        notifyDataSetChanged();
    }

    // Kelas ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivGambarProduk;
        private final TextView tvNamaProduk;
        private final TextView tvJumlah;
        private final TextView tvHarga;
        private final NumberFormat formatRupiah;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGambarProduk = itemView.findViewById(R.id.ivProductImage);
            tvNamaProduk = itemView.findViewById(R.id.tvProductName);
            tvJumlah = itemView.findViewById(R.id.tvQuantity);
            tvHarga = itemView.findViewById(R.id.tvPrice);

            formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            formatRupiah.setMaximumFractionDigits(0);
        }

        public void tampilkanData(OrderItem item) {
            tvNamaProduk.setText(item.getMerk());
            tvJumlah.setText("Jumlah: " + item.getQuantity());
            tvHarga.setText(formatRupiah.format(item.getSubtotal()));

            // Load gambar dengan Glide
            Glide.with(itemView.getContext())
                    .load(ServerAPI.BASE_URL_Image + item.getFoto())
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .into(ivGambarProduk);
        }
    }
}