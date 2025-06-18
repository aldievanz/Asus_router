package com.example.product_bottomnav.ui.product;

import static com.example.product_bottomnav.ui.product.ServerAPI.BASE_URL_Image_Avatar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.product_bottomnav.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CROP = 2;

    private TextInputEditText etNama, etAlamat, etKota, etProvinsi, etTelepon, etKodePos;
    private Button btnSubmit, btnLogout, btnChoosePhoto;
    private TextView tvUsername;
    private ImageView ivProfile;
    private Uri selectedImageUri;

    private SharedPrefManager sharedPrefManager;
    private RegisterAPI apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        sharedPrefManager = SharedPrefManager.getInstance(this);
        apiInterface = RetrofitClient.getRetrofitInstance().create(RegisterAPI.class);

        initViews();
        setupListeners();
        loadProfileData();
    }

    private void initViews() {
        ivProfile = findViewById(R.id.imageView);
        tvUsername = findViewById(R.id.tvUsername);
        etNama = findViewById(R.id.etProfile_Nama);
        etAlamat = findViewById(R.id.etProfile_alamat);
        etKota = findViewById(R.id.etProfile_kota);
        etProvinsi = findViewById(R.id.etProfile_province);
        etTelepon = findViewById(R.id.etProfile_telp);
        etKodePos = findViewById(R.id.etProfile_kodepos);
        btnSubmit = findViewById(R.id.tvProfileSubmit);
        btnLogout = findViewById(R.id.tvLogout);
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
    }

    private void setupListeners() {
        btnChoosePhoto.setOnClickListener(v -> openImageChooser());
        btnSubmit.setOnClickListener(v -> validateAndUpdateProfile());
        btnLogout.setOnClickListener(v -> logoutUser());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar Profil"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            cropImage(selectedImageUri);
        } else if (requestCode == REQUEST_IMAGE_CROP && data != null) {
            // Handle hasil crop image
            Bundle extras = data.getExtras();
            if (extras != null) {
                loadImage(selectedImageUri);
                uploadImageToServer(selectedImageUri);
            }
        }
    }

    private void cropImage(Uri imageUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(imageUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 500);
            cropIntent.putExtra("outputY", 500);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
        } catch (Exception e) {
            // Jika tidak ada aplikasi crop, langsung upload
            loadImage(imageUri);
            uploadImageToServer(imageUri);
        }
    }

    private void loadImage(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.asus)
                .into(ivProfile);
    }

    private void loadProfileData() {
        // Ambil data dari SharedPreferences
        etNama.setText(sharedPrefManager.getNama());
        etAlamat.setText(sharedPrefManager.getAlamat());
        etKota.setText(sharedPrefManager.getKota());
        etProvinsi.setText(sharedPrefManager.getProvinsi());
        etTelepon.setText(sharedPrefManager.getTelp());
        etKodePos.setText(sharedPrefManager.getKodepos());
        tvUsername.setText("Halo, " + sharedPrefManager.getNama());

        String imageUrl = sharedPrefManager.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadImageFromServer(imageUrl);
        } else {
            loadDefaultImage();
        }
    }

    private void loadImageFromServer(String imageUrl) {
        String fullUrl = imageUrl.startsWith("http") ? imageUrl : BASE_URL_Image_Avatar + imageUrl;

        Glide.with(this)
                .load(fullUrl)
                .circleCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.asus)
                .error(R.drawable.asus)
                .into(ivProfile);
    }

    private void loadDefaultImage() {
        Glide.with(this)
                .load(R.drawable.asus)
                .circleCrop()
                .into(ivProfile);
    }

    private void validateAndUpdateProfile() {
        String nama = etNama.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String kota = etKota.getText().toString().trim();
        String provinsi = etProvinsi.getText().toString().trim();
        String telp = etTelepon.getText().toString().trim();
        String kodepos = etKodePos.getText().toString().trim();

        int userId = sharedPrefManager.getUserId();
        if (userId <= 0) {
            showToast("User ID tidak valid");
            return;
        }

        Call<ResponseModel> call = apiInterface.updateProfileFields(
                String.valueOf(userId), nama, alamat, kota, provinsi, telp, kodepos);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sharedPrefManager.saveProfile(nama, alamat, kota, provinsi, telp, kodepos);
                    tvUsername.setText("Halo, " + nama);
                    showToast("Profil berhasil diperbarui");
                } else {
                    showToast("Gagal memperbarui profil");
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void uploadImageToServer(Uri imageUri) {
        String email = sharedPrefManager.getEmail();
        if (email == null || email.isEmpty()) {
            showToast("Email tidak tersedia");
            return;
        }

        try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), bytes);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("imageupload", "profile.jpg", requestFile);
            RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), email);

            Call<ImageUploadResponse> call = apiInterface.uploadProfileImageWithEmail(imagePart, emailPart);
            call.enqueue(new Callback<ImageUploadResponse>() {
                @Override
                public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ImageUploadResponse responseModel = response.body();
                        if (responseModel.getKode() == 1) {
                            String imageUrl = responseModel.getImageUrl();
                            String imageUrlWithTimestamp = imageUrl + (imageUrl.contains("?") ? "&" : "?") + "t=" + System.currentTimeMillis();

                            sharedPrefManager.saveImageUrl(imageUrl);
                            loadImageFromServer(imageUrlWithTimestamp);
                            showToast("Foto profil berhasil diunggah");
                        } else {
                            showToast(responseModel.getPesan() != null ? responseModel.getPesan() : "Gagal upload foto profil");
                        }
                    } else {
                        showToast("Gagal mendapatkan respons dari server: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                    showToast("Gagal terhubung ke server");
                    Log.e("Upload Error", "Error: " + t.getMessage());
                }
            });
        } catch (IOException e) {
            showToast("Terjadi kesalahan saat membaca gambar");
            Log.e("Upload Error", "IOException: " + e.getMessage());
        }
    }

    private void logoutUser() {
        sharedPrefManager.logout();
        showToast("Logout berhasil");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}