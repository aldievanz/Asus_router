package com.example.product_bottomnav.ui.product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.product_bottomnav.R;

import com.example.product_bottomnav.ui.notifications.FragmentGuest;
import com.example.product_bottomnav.ui.notifications.KontakKamiFragment;

public class Profil extends Fragment {
    private String email;
    private SharedPrefManager sharedPrefManager;

    public Profil() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        sharedPrefManager = SharedPrefManager.getInstance(requireContext());

        try {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("login_pref", getContext().MODE_PRIVATE);
            email = sharedPreferences.getString("email", "");

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

            if (email.isEmpty()) {
                navController.navigate(R.id.navigation_guest);
                return root;
            }

            TextView tvUsername = root.findViewById(R.id.tvUsername);
            String namaUser = sharedPrefManager.getNama();
            tvUsername.setText(namaUser != null && !namaUser.isEmpty() ? namaUser : "User");

            Button btnEditProfile = root.findViewById(R.id.btnEditProfile);
            btnEditProfile.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
                startActivity(intent);
            });

            Button btnKontakKami = root.findViewById(R.id.btnKontakKami);
            btnKontakKami.setOnClickListener(v -> {
                KontakKamiFragment kontakKamiFragment = new KontakKamiFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, kontakKamiFragment)
                        .addToBackStack(null)
                        .commit();
            });

            Button btnLogout = root.findViewById(R.id.btnLogout);
            btnLogout.setOnClickListener(v -> {
                sharedPrefManager.logout();
                Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            });

        } catch (Exception e) {
            goToGuestFragment();
            return null;
        }

        return root;
    }

    private void goToGuestFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new FragmentGuest())
                .commit();
    }
}
