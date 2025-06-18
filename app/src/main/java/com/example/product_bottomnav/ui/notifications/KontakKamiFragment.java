package com.example.product_bottomnav.ui.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.product_bottomnav.R;

public class KontakKamiFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.kontak_kami, container, false);

        // Inisialisasi dan set onClickListener untuk tiap tombol
        ImageButton btnMaps = view.findViewById(R.id.btnMaps);
        ImageButton btnInstagram = view.findViewById(R.id.btnInstagram);
        ImageButton btnWhatsApp = view.findViewById(R.id.btnWhatsApp);

        btnMaps.setOnClickListener(v -> openMaps());
        btnInstagram.setOnClickListener(v -> openInstagram());
        btnWhatsApp.setOnClickListener(v -> openWhatsApp());

        return view;
    }

    private void openMaps() {
        // Link lokasi, bisa disesuaikan dengan titik lokasi yang diinginkan
        Uri uri = Uri.parse("https://maps.app.goo.gl/QRXvejDRK6GV45YU9?g_st=aw"); // <- Ganti koordinat atau nama tempat

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps"); // Prefer Google Maps

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Fallback ke browser jika Google Maps tidak tersedia
            Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(fallbackIntent);
            Toast.makeText(getActivity(), "Google Maps tidak ditemukan, membuka lewat browser", Toast.LENGTH_SHORT).show();
        }
    }


    private void openInstagram() {
        Uri uri = Uri.parse("https://www.instagram.com/asusid?igsh=MWR4eWwwdGl5enB0dQ==   "); // Ganti dengan link asli
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.instagram.android");

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, uri)); // Fallback ke browser
        }
    }

    // Hapus parameter View dari sini
    private void openWhatsApp() {
        String phoneNumber = "6282245400182"; // Pakai kode negara
        String message = "Halo, saya tertarik dengan produk Anda";

        try {
            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(message));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Terjadi kesalahan saat membuka WhatsApp", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}
