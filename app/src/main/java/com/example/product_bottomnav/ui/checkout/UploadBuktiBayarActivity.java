package com.example.product_bottomnav.ui.checkout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.product_bottomnav.R;
import com.example.product_bottomnav.ui.product.RegisterAPI;
import com.example.product_bottomnav.ui.product.ServerAPI;

import org.json.JSONObject;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadBuktiBayarActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgBuktiBayar;
    private Button btnPilihGambar, btnUpload;
    private TextView tvTransId, tvTotalBayar;
    private Uri imageUri;
    private int transId;
    private double totalBayar;
    String metodeBayar = "transfer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bukti_bayar);

        // Inisialisasi view
        imgBuktiBayar = findViewById(R.id.imgBuktiBayar);
        btnPilihGambar = findViewById(R.id.btnPilihGambar);
        btnUpload = findViewById(R.id.btnUpload);
        tvTransId = findViewById(R.id.tvTransId);
        tvTotalBayar = findViewById(R.id.tvTotalBayar);

        // Dapatkan data dari intent
        transId = getIntent().getIntExtra("TRANS_ID", 0);
        totalBayar = getIntent().getDoubleExtra("TOTAL_BAYAR", 0);

        // Tampilkan data transaksi
        tvTransId.setText("#" + transId);
        tvTotalBayar.setText(formatRupiah(totalBayar));

        // Setup listeners
        btnPilihGambar.setOnClickListener(v -> openGallery());
        btnUpload.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImage();
            } else {
                Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgBuktiBayar.setImageURI(imageUri);
            btnUpload.setEnabled(true);
        }
    }

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Mengupload bukti bayar...");
        pd.setCancelable(false);
        pd.show();

        String filePath = getRealPathFromURI(imageUri); // Mendapatkan path file gambar
        if (filePath == null) {
            pd.dismiss();
            Toast.makeText(this, "Gagal membaca file gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Membaca file gambar
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData(
                "bukti_bayar",  // Key yang digunakan di server
                file.getName(),
                requestFile
        );

        RequestBody transIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(transId)); // Menambahkan trans_id
        RequestBody metodeBayarBody = RequestBody.create(MediaType.parse("text/plain"), metodeBayar); // Menambahkan metode bayar

        // Membuat Retrofit instance untuk API call
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<ResponseBody> call = api.uploadBuktiBayar(transIdBody, body, metodeBayarBody); // Mengirimkan trans_id, gambar, dan metodeBayar

        // Eksekusi API call
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        JSONObject json = new JSONObject(jsonResponse);

                        if (json.getBoolean("status")) {
                            Toast.makeText(UploadBuktiBayarActivity.this,
                                    "Bukti bayar berhasil diupload",
                                    Toast.LENGTH_SHORT).show();

                            // Kirim result OK ke activity pemanggil jika perlu
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(UploadBuktiBayarActivity.this,
                                    "Gagal: " + json.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UploadBuktiBayarActivity.this,
                                "Error: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(UploadBuktiBayarActivity.this,
                            "Error parsing response",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(UploadBuktiBayarActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private String formatRupiah(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }
}