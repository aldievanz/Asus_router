package com.example.product_bottomnav.ui.product;

import static com.example.product_bottomnav.ui.product.ServerAPI.BASE_URL_Image_Avatar;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.product_bottomnav.R;
import com.example.product_bottomnav.ui.notifications.FragmentGuest;
import com.example.product_bottomnav.ui.notifications.KontakKamiFragment;

public class Profil extends Fragment {
    private String email;
    private SharedPrefManager sharedPrefManager;
    private ImageView ivProfilePic;

    public Profil() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Menginisialisasi SharedPrefManager dan ImageView
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        ivProfilePic = root.findViewById(R.id.ivProfilePic);

        try {
            // Mengambil email dari SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("login_pref", getContext().MODE_PRIVATE);
            email = sharedPreferences.getString("email", "");

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

            // Jika email kosong, arahkan ke fragment tamu
            if (email.isEmpty()) {
                navController.navigate(R.id.navigation_guest);
                return root;
            }

            // Mengambil nama pengguna dan menampilkannya
            TextView tvUsername = root.findViewById(R.id.tvUsername);
            String namaUser = sharedPrefManager.getNama();
            tvUsername.setText(namaUser != null && !namaUser.isEmpty() ? namaUser : "User");

            // Ambil URL gambar profil dari SharedPreferences
            String imageUrl = sharedPrefManager.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                loadImageFromServer(imageUrl);  // Memuat gambar dari server
            } else {
                loadDefaultImage();  // Jika URL gambar kosong, tampilkan gambar default
            }

            // Menangani klik tombol Edit Profil
            Button btnEditProfile = root.findViewById(R.id.btnEditProfile);
            btnEditProfile.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
                startActivity(intent);
            });

            // Menangani klik tombol Kontak Kami
            Button btnKontakKami = root.findViewById(R.id.btnKontakKami);
            btnKontakKami.setOnClickListener(v -> {
                KontakKamiFragment kontakKamiFragment = new KontakKamiFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, kontakKamiFragment)
                        .addToBackStack(null)
                        .commit();
            });

            // Menangani klik tombol Logout
            Button btnLogout = root.findViewById(R.id.btnLogout);
            btnLogout.setOnClickListener(v -> {
                sharedPrefManager.logout();
                Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            });

            // Menangani klik pada tombol History Pesanan
            Button btnHistoryPesanan = root.findViewById(R.id.btnHistoryPesanan);
            btnHistoryPesanan.setOnClickListener(v -> {
                // Mendapatkan NavController
                navController.navigate(R.id.orderHistoryFragment);  // Arahkan ke OrderHistoryFragment
            });

            // Menangani klik pada tombol Edit Password
            Button btnEditPW = root.findViewById(R.id.btnEditPW);
            btnEditPW.setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), EditPassword.class);
                startActivity(intent);
            });


        } catch (Exception e) {
            goToGuestFragment();
            return null;
        }

        return root;
    }

    // Memuat gambar dari server menggunakan Glide
    private void loadImageFromServer(String imageUrl) {
        String fullUrl = imageUrl.startsWith("http") ? imageUrl : BASE_URL_Image_Avatar + imageUrl;

        Glide.with(this)
                .load(fullUrl)
                .circleCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.asus)
                .error(R.drawable.asus)
                .into(ivProfilePic);
    }

    // Memuat gambar default jika URL tidak ditemukan
    private void loadDefaultImage() {
        Glide.with(this)
                .load(R.drawable.asus)
                .circleCrop()
                .into(ivProfilePic);
    }

    // Jika terjadi kesalahan, arahkan pengguna ke fragment tamu
    private void goToGuestFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, new FragmentGuest())
                .commit();
    }
}
