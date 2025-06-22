package com.example.product_bottomnav.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.product_bottomnav.R;
import com.example.product_bottomnav.ui.model.OrderModel;
import com.example.product_bottomnav.ui.product.ServerAPI;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<OrderModel> orderList;
    private final OnUploadClickListener uploadClickListener;

    public interface OnUploadClickListener {
        void onPilihGambarClicked(OrderModel order);
    }

    public OrderHistoryAdapter(Context context, List<OrderModel> orderList, OnUploadClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.uploadClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        holder.tvNamaMobil.setText(order.getMerk());
        holder.tvAlamat.setText(order.getAlamat_kirim() + ", " + order.getKota_kirim() + ", " + order.getProvinsi_kirim());
        holder.tvEstimasi.setText("Estimasi: " + order.getLama_kirim() + " hari");
        holder.tvTanggalOrder.setText("Tanggal Order: " + order.getTgl_order());
        holder.tvSubtotal.setText("Subtotal: " + formatRupiah(order.getSubtotal()));
        holder.tvOngkir.setText("Ongkir: " + formatRupiah(order.getOngkir()));
        holder.tvTotalBayar.setText("Total: " + formatRupiah(order.getTotal_bayar()));
        holder.tvStatus.setText("Status: " + order.getStatus());
        holder.tvMetodeBayar.setText(order.getMetode_bayar().toUpperCase());

        // Logika tampilan upload bukti bayar
        if (order.getMetode_bayar().equalsIgnoreCase("cod")) {
            holder.layoutUpload.setVisibility(View.GONE);
            holder.imgBuktiBayar.setVisibility(View.GONE);
        } else {
            holder.tvMetodePembayaran.setVisibility(View.VISIBLE);
            holder.tvMetodeBayar.setVisibility(View.VISIBLE);

            if (order.getBukti_bayar() == null || order.getBukti_bayar().isEmpty()) {
                holder.layoutUpload.setVisibility(View.VISIBLE);
                holder.imgBuktiBayar.setVisibility(View.GONE);
                holder.btnPilihGambar.setOnClickListener(v -> uploadClickListener.onPilihGambarClicked(order));
            } else {
                holder.layoutUpload.setVisibility(View.GONE);
                holder.imgBuktiBayar.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(ServerAPI.BASE_URL + "images/" + order.getBukti_bayar())
                        .placeholder(R.drawable.asus)
                        .into(holder.imgBuktiBayar);
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaMobil, tvAlamat, tvEstimasi, tvTanggalOrder, tvSubtotal,
                tvOngkir, tvTotalBayar, tvStatus, tvMetodePembayaran, tvMetodeBayar;
        LinearLayout layoutUpload;
        Button btnPilihGambar;
        ImageView imgBuktiBayar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaMobil = itemView.findViewById(R.id.tvNamaMobil);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvEstimasi = itemView.findViewById(R.id.tvEstimasi);
            tvTanggalOrder = itemView.findViewById(R.id.tvTanggalOrder);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            tvOngkir = itemView.findViewById(R.id.tvOngkir);
            tvTotalBayar = itemView.findViewById(R.id.tvTotalBayar);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMetodePembayaran = itemView.findViewById(R.id.tvMetodePembayaran);
            tvMetodeBayar = itemView.findViewById(R.id.tvMetodeBayar);
            layoutUpload = itemView.findViewById(R.id.layoutUpload);
            btnPilihGambar = itemView.findViewById(R.id.btnPilihGambar);
            imgBuktiBayar = itemView.findViewById(R.id.imgBuktiBayar);
        }
    }

    private String formatRupiah(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }
}