package com.example.product_bottomnav.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
 // Pastikan ini benar sesuai package LoginActivity kamu
import com.example.product_bottomnav.R;
import com.example.product_bottomnav.ui.product.LoginActivity;

public class FragmentGuest extends Fragment {

    public FragmentGuest() {
        // Konstruktor kosong diperlukan oleh Fragment
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guest, container, false);

        Button btnLogin = rootView.findViewById(R.id.btnLoginGuest);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hapus semua SharedPreferences sebelum pindah ke LoginActivity
                SharedPreferences sharedPref = getActivity().getSharedPreferences("login_pref", Context.MODE_PRIVATE);
                sharedPref.edit().clear().apply();

                // Pindah ke LoginActivity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
