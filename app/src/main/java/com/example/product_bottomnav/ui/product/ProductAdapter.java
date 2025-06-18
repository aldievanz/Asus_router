package com.example.product_bottomnav.ui.product;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.product_bottomnav.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {

    private final Context context;
    private List<Product> productList;
    private List<Product> productListFull;
    private int viewer;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);
    }

    public void updateList(List<Product> newList) {
        productList = new ArrayList<>(newList);
        productListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
    public void setViewer(int viewer) {
        this.viewer = viewer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvMerk.setText(product.getMerk());
        holder.tvHarga.setText(formatRupiah(product.getHargajual()));
        holder.tvStok.setText("Stok: " + product.getStok());
        holder.ratingBar.setRating(product.getRating());
        holder.tvRatingText.setText(String.valueOf(product.getRating()));
        holder.tvViewer.setText(product.getViewer() + "x"); // ⬅️ Tampilkan viewer

        // Load gambar
        String imageUrl = ServerAPI.BASE_URL_Image + product.getFoto();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_cart)
                .into(holder.imageProduct);

        // Klik item → buka detail dan update viewer
        holder.itemView.setOnClickListener(v -> {
            // Update viewer ke server
            RegisterAPI apiService = RetrofitClient.getRetrofitInstance().create(RegisterAPI.class);
            apiService.updateViewer(product.getKode()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // Jika sukses, tambah viewer lokal
                    product.setViewer(product.getViewer() + 1);
                    notifyItemChanged(holder.getAdapterPosition()); // ⬅️ refresh item ini
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Gagal koneksi, bisa di-log atau ditampilkan toast
                }
            });

            Intent intent = new Intent(context, DetailProductActivity.class);
            intent.putExtra("product", product);
            intent.putExtra("viewer", product.getViewer());  // kirim viewer terbaru
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public int getViewer() {
        return viewer;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView tvMerk, tvHarga, tvStok, tvRatingText, tvViewer;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            tvMerk = itemView.findViewById(R.id.tvMerk);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvStok = itemView.findViewById(R.id.tvStok);
            tvRatingText = itemView.findViewById(R.id.tvRatingText);
            tvViewer = itemView.findViewById(R.id.tvViewer); // ⬅️ Inisialisasi viewer
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
    public void setFilteredList(List<Product> filteredList) {
        this.productList = filteredList;
        notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Product> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(productListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Product item : productListFull) {
                        if (item.getMerk().toLowerCase().contains(filterPattern) ||
                                String.valueOf(item.getRating()).contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productList.clear();
                if (results.values != null) {
                    productList.addAll((List<Product>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }

    // Format ke "Rp xxx.xxx"
    private String formatRupiah(double number) {
        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(new Locale("in", "ID"));
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("Rp ");
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        formatter.setMaximumFractionDigits(0);
        return formatter.format(number);
    }
}
