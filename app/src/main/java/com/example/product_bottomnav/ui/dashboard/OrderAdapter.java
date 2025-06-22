package com.example.product_bottomnav.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.product_bottomnav.R;
import com.example.product_bottomnav.ui.product.ServerAPI;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<OrderItem> orderItems;
    private OrderHelper orderHelper;
    private OnTotalUpdatedListener listener;

    public OrderAdapter(List<OrderItem> orderItems, OrderHelper orderHelper, OnTotalUpdatedListener listener) {
        this.orderItems = orderItems;
        this.orderHelper = orderHelper;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);

        holder.tvProductName.setText(item.getMerk());
        holder.tvPrice.setText(String.format("Rp %,.0f", item.getHargajual()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Load gambar produk
        Glide.with(holder.itemView.getContext())
                .load(ServerAPI.BASE_URL_Image + item.getFoto())
                .placeholder(R.drawable.placeholder_product)
                .error(R.drawable.placeholder_product)
                .into(holder.imageProduct);

        // Tombol tambah jumlah
        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            orderHelper.updateQuantity(item.getKode(), newQuantity);
            notifyItemChanged(position);
            listener.onTotalUpdated(orderHelper.getTotal());
        });

        // Tombol kurang jumlah
        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                item.setQuantity(newQuantity);
                orderHelper.updateQuantity(item.getKode(), newQuantity);
                notifyItemChanged(position);
                listener.onTotalUpdated(orderHelper.getTotal());
            }
        });

        // Tombol hapus item
        holder.btnRemove.setOnClickListener(v -> {
            orderHelper.removeFromOrder(item.getKode());
            orderItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, orderItems.size());
            listener.onTotalUpdated(orderHelper.getTotal());
        });
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public interface OnTotalUpdatedListener {
        void onTotalUpdated(double total);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, tvQuantity;
        ImageView imageProduct;
        ImageButton btnIncrease, btnDecrease, btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}