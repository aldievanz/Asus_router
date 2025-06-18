package com.example.product_bottomnav.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.product_bottomnav.R;
import com.example.product_bottomnav.databinding.FragmentHomeBinding;
import com.example.product_bottomnav.ui.product.Product;
import com.example.product_bottomnav.ui.product.ProductAdapter;
import com.example.product_bottomnav.ui.product.RegisterAPI;
import com.example.product_bottomnav.ui.product.RetrofitClient;
import com.example.product_bottomnav.ui.product.SharedPrefManager;
import com.example.product_bottomnav.ui.product.User;
import com.example.product_bottomnav.ui.product.UserResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private SharedPrefManager sharedPrefManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        // Display user name or guest status
        displayUserInfo();

        // Setup image slider
        setupImageSlider();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup category filters
        setupCategoryFilters();

        // Setup search functionality
        setupSearch();

        // Fetch products
        fetchProducts();

        return root;
    }

    private void displayUserInfo() {
        if (sharedPrefManager.isLoggedIn()) {
            String userName = sharedPrefManager.getNama();
            if (userName != null && !userName.isEmpty()) {
                binding.tvUsername.setText(userName);
            } else {
                // If logged in but name not saved, fetch from server
                fetchUserProfile();
            }
        } else {
            binding.tvUsername.setText("Guest");
        }
    }

    private void fetchUserProfile() {
        String email = sharedPrefManager.getEmail();
        if (email == null || email.isEmpty()) {
            binding.tvUsername.setText("User");
            return;
        }

        RegisterAPI apiService = RetrofitClient.getRetrofitInstance().create(RegisterAPI.class);
        Call<UserResponse> call = apiService.getProfile(email);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    if (userResponse.isSuccess() && userResponse.getData() != null) {
                        User user = userResponse.getData();
                        // Save profile data including name
                        sharedPrefManager.saveProfile(
                                user.getNama(),
                                user.getAlamat(),
                                user.getKota(),
                                user.getProvinsi(),
                                user.getTelp(),
                                user.getKodepos()
                        );
                        binding.tvUsername.setText(user.getNama());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("HomeFragment", "Failed to fetch user profile", t);
            }
        });
    }

    private void setupImageSlider() {
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.s1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.s2, ScaleTypes.FIT));
        binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);
        binding.imageSlider.startSliding(2000);
    }

    private void setupRecyclerView() {
        recyclerView = binding.PopulerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    private void setupCategoryFilters() {
        binding.btnSemua.setOnClickListener(v -> filterByCategory("SEMUA"));
        binding.btngaming.setOnClickListener(v -> filterByCategory("GAMING"));
        binding.btnwifi6.setOnClickListener(v -> filterByCategory("WIFI6"));
        binding.btnwifi5.setOnClickListener(v -> filterByCategory("WIFI5"));
        binding.btnwifi4.setOnClickListener(v -> filterByCategory("WIFI4"));
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterByKeyword(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchProducts() {
        RegisterAPI apiService = RetrofitClient.getRetrofitInstance().create(RegisterAPI.class);
        Call<List<Product>> call = apiService.getProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts.clear();
                    allProducts.addAll(response.body());

                    // Initially show popular products (viewer >= 7) with STRICT limit 6
                    productList.clear();
                    int count = 0;
                    for (Product p : allProducts) {
                        if (p.getViewer() >= 7) {
                            if (count < 6) {
                                productList.add(p);
                                count++;
                            } else {
                                break; // Stop after 6 products
                            }
                        }
                    }

                    adapter = new ProductAdapter(getContext(), productList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByCategory(String kategori) {
        if (adapter == null || allProducts == null) return;

        List<Product> filtered = new ArrayList<>();
        int count = 0;

        if (kategori.equalsIgnoreCase("SEMUA")) {
            // All products with viewer >= 7 (limit 6)
            for (Product p : allProducts) {
                if (p.getViewer() >= 7 && count < 6) {
                    filtered.add(p);
                    count++;
                }
            }
        } else {
            // Category specific products with viewer >= 7 (limit 6)
            for (Product p : allProducts) {
                if (p.getKategori() != null
                        && p.getKategori().trim().equalsIgnoreCase(kategori.trim())
                        && p.getViewer() >= 7
                        && count < 6) {
                    filtered.add(p);
                    count++;
                }
            }
        }

        productList.clear();
        productList.addAll(filtered);
        adapter.setFilteredList(productList);
    }
    @Override
    public void onResume() {
        super.onResume();
        // Reset limit setiap kali fragment ditampilkan kembali
        if (adapter != null && !productList.isEmpty()) {
            if (productList.size() > 6) {
                productList = productList.subList(0, 6);
                adapter.setFilteredList(productList);
            }
        }
    }
    private void filterByKeyword(String keyword) {
        if (adapter == null || allProducts == null) return;

        String lowerKeyword = keyword.toLowerCase();
        List<Product> filteredList = new ArrayList<>();
        int count = 0;

        for (Product product : allProducts) {
            if (product.getMerk().toLowerCase().contains(lowerKeyword)) {  // Added missing parenthesis here
                if (count < 6) {
                    filteredList.add(product);
                    count++;
                } else {
                    break;
                }
            }
        }

        productList.clear();
        productList.addAll(filteredList);
        adapter.setFilteredList(productList);
    }
}