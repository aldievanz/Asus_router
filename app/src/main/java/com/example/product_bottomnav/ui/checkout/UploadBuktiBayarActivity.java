package com.example.product_bottomnav.ui.checkout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.product_bottomnav.R;
import com.example.product_bottomnav.ui.product.RegisterAPI;
import com.example.product_bottomnav.ui.product.ServerAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bukti_bayar);

        imgBuktiBayar = findViewById(R.id.imgBuktiBayar);
        btnPilihGambar = findViewById(R.id.btnPilihGambar);
        btnUpload = findViewById(R.id.btnUpload);
        tvTransId = findViewById(R.id.tvTransId);
        tvTotalBayar = findViewById(R.id.tvTotalBayar);

        transId = getIntent().getIntExtra("TRANS_ID", 0);
        totalBayar = getIntent().getDoubleExtra("TOTAL_BAYAR", 0);

        tvTransId.setText("#" + transId);
        tvTotalBayar.setText(formatRupiah(totalBayar));

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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
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

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            String fileName = getFileName(imageUri);
            File file = new File(getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("bukti_bayar", file.getName(), requestFile);
            RequestBody transIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(transId));
            RequestBody metodeBayarBody = RequestBody.create(MediaType.parse("text/plain"), "transfer");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ServerAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RegisterAPI api = retrofit.create(RegisterAPI.class);
            Call<ResponseBody> call = api.uploadBuktiBayar(transIdBody, body, metodeBayarBody);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    pd.dismiss();
                    if (response.isSuccessful()) {
                        Toast.makeText(UploadBuktiBayarActivity.this, "Bukti bayar berhasil diupload", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(UploadBuktiBayarActivity.this, "Upload gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(UploadBuktiBayarActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            pd.dismiss();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String formatRupiah(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }
}