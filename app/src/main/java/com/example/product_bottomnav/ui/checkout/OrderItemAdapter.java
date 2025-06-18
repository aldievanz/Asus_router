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

import java.util.ArrayList;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private ArrayList<OrderItem> orderItems;

    public OrderItemAdapter(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
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
        OrderItem item = orderItems.get(position);

        holder.tvProductName.setText(item.getMerk());
        holder.tvQuantity.setText("Qty: " + item.getQuantity());
        holder.tvPrice.setText("Rp " + String.format("%,.0f", item.getSubtotal()));

        // Load image with Glide
        Glide.with(holder.itemView.getContext())
                .load(ServerAPI.BASE_URL_Image + item.getFoto())
                .placeholder(R.drawable.placeholder_product)
                .error(R.drawable.placeholder_product)
                .into(holder.ivProductImage);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvQuantity, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}