package com.example.product_bottomnav.ui.product;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.product_bottomnav.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPassword extends AppCompatActivity {

    private EditText newPasswordEditText, confirmNewPasswordEditText;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        // Menginisialisasi view
        newPasswordEditText = findViewById(R.id.newPassword);
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPassword);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        // Logika saat tombol ganti password ditekan
        changePasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString();
            String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

            if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(EditPassword.this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(EditPassword.this, "Password baru dan konfirmasi tidak cocok", Toast.LENGTH_SHORT).show();
            } else {
                // Mengirim request untuk mengubah password
                changePassword(newPassword);
            }
        });
    }

    private void changePassword(String newPassword) {
        // Ambil data pengguna yang sedang login
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        String userEmail = sharedPrefManager.getEmail();

        // Menggunakan Retrofit untuk membuat permintaan API
        RegisterAPI api = RetrofitClient.getRetrofitInstance().create(RegisterAPI.class);
        Call<ChangePasswordResponse> call = api.changePassword(userEmail, newPassword);  // Hanya dua parameter: email dan password baru

        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChangePasswordResponse res = response.body();
                    if (res.getResult() == 1) {
                        // Password berhasil diubah
                        Toast.makeText(EditPassword.this, "Password berhasil diubah", Toast.LENGTH_SHORT).show();
                    } else {
                        // Gagal update password
                        Toast.makeText(EditPassword.this, "Gagal mengubah password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Gagal mendapatkan response dari server
                    Toast.makeText(EditPassword.this, "Gagal menghubungi server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                // Error pada permintaan API
                Toast.makeText(EditPassword.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
