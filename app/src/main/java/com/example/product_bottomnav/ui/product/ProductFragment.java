package com.example.product_bottomnav.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_bottomnav.databinding.FragmentProductBinding;

import java.util.ArrayList;
import java.util.List;
import com.example.product_bottomnav.ui.product.ServerAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {
    private List<Product> allProducts;
    private ProductAdapter adapter;
    private FragmentProductBinding binding;
    private SearchView searchView;
    private RecyclerView recyclerView;

    private Button btnSemua, btngaming, btnwifi6, btnwifi5, btnwifi4;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        searchView = binding.searchView;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        btnSemua = binding.btnSemua;
        btngaming = binding.btngaming;
        btnwifi6 = binding.btnwifi6;
        btnwifi5 = binding.btnwifi5;
        btnwifi4 = binding.btnwifi4;

        btnSemua.setOnClickListener(v -> filterByCategory("SEMUA"));
        btngaming.setOnClickListener(v -> filterByCategory("GAMING"));
        btnwifi6.setOnClickListener(v -> filterByCategory("WIFI6"));
        btnwifi5.setOnClickListener(v -> filterByCategory("WIFI5"));
        btnwifi4.setOnClickListener(v -> filterByCategory("WIFI4"));

        fetchProduct();
        return root;
    }

    private void fetchProduct() {
        RegisterAPI apiService = RetrofitClient.getRetrofitInstance().create(RegisterAPI.class);
        Call<List<Product>> call = apiService.getProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    adapter = new ProductAdapter(getContext(), allProducts);
                    recyclerView.setAdapter(adapter);
                    setupSearch();
                } else {
                    Toast.makeText(getContext(), "Data kosong atau gagal diambil!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal mengambil data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void filterByCategory(String kategori) {
        if (adapter == null || allProducts == null) return;

        if (kategori.equals("SEMUA")) {
            adapter.updateList(allProducts);
        } else {
            List<Product> filtered = new ArrayList<>();
            for (Product p : allProducts) {
                if (p.getKategori().equalsIgnoreCase(kategori)) {
                    filtered.add(p);
                }
            }
            adapter.updateList(filtered);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
