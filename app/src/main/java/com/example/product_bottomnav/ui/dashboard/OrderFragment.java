package com.example.product_bottomnav.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_bottomnav.R;
import com.example.product_bottomnav.databinding.FragmentOrderBinding;
import com.example.product_bottomnav.ui.checkout.activity_checkout;
import com.example.product_bottomnav.ui.product.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {
    private FragmentOrderBinding binding;
    private OrderHelper orderHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the logged-in user's userId
        int userId = SharedPrefManager.getInstance(requireContext()).getUserId();

        // Initialize OrderHelper with the userId
        orderHelper = new OrderHelper(requireContext(), userId);

        binding.recyclerViewOrders.setNestedScrollingEnabled(false);
        setupRecyclerView();

        binding.btnCheckout.setOnClickListener(v -> {
            List<OrderItem> orderItems = orderHelper.getOrderItems();

            if (orderItems.isEmpty()) {
                Toast.makeText(requireContext(), "Keranjang belanja kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(requireActivity(), activity_checkout.class);
            intent.putParcelableArrayListExtra("ORDER_ITEMS", new ArrayList<>(orderItems));
            startActivity(intent);
        });

        return root;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewOrders;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<OrderItem> orderItems = orderHelper.getOrderItems();
        OrderAdapter adapter = new OrderAdapter(orderItems, orderHelper, total -> {
            binding.tvTotal.setText(String.format("Total Bayar: Rp %,.0f", total));
        });
        recyclerView.setAdapter(adapter);
        binding.tvTotal.setText(String.format("Total Bayar: Rp %,.0f", orderHelper.getTotal()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}