package com.example.product_bottomnav.ui.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.product_bottomnav.MainActivity;
import com.example.product_bottomnav.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;
    private EditText editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Cek jika user sudah login
        if (sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        inisialisasiView();
        setupListeners();
    }

    private void inisialisasiView() {
        editEmail = findViewById(R.id.editemail);
        editPassword = findViewById(R.id.editPassword);
    }

    private void setupListeners() {
        // Listener untuk login sebagai guest
        Button btnGuest = findViewById(R.id.btnguest);
        btnGuest.setOnClickListener(v -> {
            sharedPrefManager.loginUser(
                    "guest", "", "0", "", "", "Guest", "", "", "", "", ""
            );
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

        // Listener untuk pindah ke halaman register
        TextView tvRegister = findViewById(R.id.registerd);
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Listener untuk tombol login
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> prosesLogin());

        // Listener untuk "Lupa Password?"
        TextView changePasswordLink = findViewById(R.id.changePasswordLink);
        changePasswordLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, EditPassword.class));  // Pindah ke EditPasswordActivity
        });
    }

    private void prosesLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginAPI api = RetrofitClient.getRetrofitInstance().create(LoginAPI.class);
        Call<LoginResponse> call = api.login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleLoginResponse(response.body());
                } else {
                    Toast.makeText(LoginActivity.this, "Gagal mendapatkan respon dari server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleLoginResponse(LoginResponse response) {
        if (response.getResult() == 1) {
            LoginResponse.User userData = response.getData();
            if (userData != null && userData.getId() != null) {
                // Simpan data user yang login
                sharedPrefManager.loginUser(
                        userData.getEmail(), // username
                        editPassword.getText().toString(), // password
                        userData.getId(), // userId
                        userData.getEmail(), // email
                        userData.getNama(), // name
                        userData.getProfile_image(),
                        "", "", "", "", ""  // Data pengguna tambahan
                );

                Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Data pengguna tidak valid", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show();
        }
    }
}