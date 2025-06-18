package com.example.product_bottomnav.ui.product;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.product_bottomnav.R;
import com.example.product_bottomnav.ui.dashboard.OrderHelper;
import com.example.product_bottomnav.ui.product.ServerAPI;
import com.example.product_bottomnav.ui.product.Product;
import com.example.product_bottomnav.ui.product.SharedPrefManager;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailProductActivity extends AppCompatActivity {

    private ImageView imageProduct;
    private TextView tvMerk, tvHarga, tvStok, tvRatingText, tvDeskripsi, tvkategori, tvViewer;
    private RatingBar ratingBar;
    private ImageButton btnKeranjang;

    private Product product;
    private OrderHelper orderHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        // Initialize helper with userId
        int userId = SharedPrefManager.getInstance(this).getUserId();
        orderHelper = new OrderHelper(this, userId); // Pass userId to OrderHelper

        // Initialize views
        initializeViews();

        // Get product data from Intent
        product = (Product) getIntent().getSerializableExtra("product");
        int viewerFromIntent = getIntent().getIntExtra("viewer", -1);

        if (product != null) {
            displayProductDetails(product, viewerFromIntent);
            setupAddToCartButton();
        } else {
            handleProductNotFound();
        }
    }

    private void initializeViews() {
        imageProduct = findViewById(R.id.imageProduct);
        tvMerk = findViewById(R.id.tvMerk);
        tvHarga = findViewById(R.id.tvHarga);
        tvStok = findViewById(R.id.tvStok);
        tvRatingText = findViewById(R.id.tvRatingText);
        ratingBar = findViewById(R.id.ratingBar);
        btnKeranjang = findViewById(R.id.btnKeranjang);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        tvkategori = findViewById(R.id.tvkategori);
        tvViewer = findViewById(R.id.tvViewer);
    }

    private void displayProductDetails(Product product, int viewerFromIntent) {
        // Format price to Indonesian Rupiah
        NumberFormat formatRupiah = NumberFormat.getInstance(new Locale("in", "ID"));
        String hargaFormatted = formatRupiah.format(product.getHargajual());

        // Display product details
        tvMerk.setText(product.getMerk());
        tvHarga.setText("Rp " + hargaFormatted);
        tvStok.setText("Stok: " + product.getStok());
        tvRatingText.setText("(" + product.getRating() + ")");
        ratingBar.setRating(product.getRating());
        tvDeskripsi.setText(product.getDeskripsi());
        tvkategori.setText("Kategori: " + product.getKategori());

        // Display viewer count
        if (viewerFromIntent != -1) {
            tvViewer.setText("Dilihat: " + viewerFromIntent + "x");
        } else {
            tvViewer.setText("Dilihat: " + product.getViewer() + "x");
        }

        // Load product image
        String imageUrl = ServerAPI.BASE_URL_Image + product.getFoto();
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_cart)
                .error(R.drawable.ic_cart)
                .into(imageProduct);
    }

    private void setupAddToCartButton() {
        if (product.getStok() == 0) {
            btnKeranjang.setEnabled(false);
            btnKeranjang.setAlpha(0.5f);
            Toast.makeText(this, "Stok produk habis", Toast.LENGTH_SHORT).show();
        } else {
            btnKeranjang.setOnClickListener(v -> {
                orderHelper.addToOrder(product);
                Toast.makeText(this, product.getMerk() + " ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void handleProductNotFound() {
        Toast.makeText(this, "Data produk tidak ditemukan", Toast.LENGTH_SHORT).show();
        finish();
    }
}
