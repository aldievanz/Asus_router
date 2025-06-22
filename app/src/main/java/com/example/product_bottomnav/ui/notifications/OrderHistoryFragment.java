package com.example.product_bottomnav.ui.notifications;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_bottomnav.R;
import com.example.product_bottomnav.ui.model.OrderModel;
import com.example.product_bottomnav.ui.product.RegisterAPI;
import com.example.product_bottomnav.ui.product.ServerAPI;
import com.example.product_bottomnav.ui.product.SharedPrefManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderHistoryFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private RecyclerView rvOrder;
    private OrderHistoryAdapter adapter;
    private Uri imageUri;
    private OrderModel selectedOrder;
    private int userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        userId = SharedPrefManager.getInstance(requireContext()).getUserId();
        rvOrder = view.findViewById(R.id.rvOrderHistory);
        rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        loadOrderHistory();
        return view;
    }

    private void loadOrderHistory() {
        RegisterAPI api = ServerAPI.getClient().create(RegisterAPI.class);
        Call<ResponseOrderHistory> call = api.getOrderHistory(userId);

        call.enqueue(new Callback<ResponseOrderHistory>() {
            @Override
            public void onResponse(Call<ResponseOrderHistory> call, Response<ResponseOrderHistory> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    List<OrderModel> orderList = response.body().getData();
                    adapter = new OrderHistoryAdapter(requireContext(), orderList, order -> {
                        selectedOrder = order;
                        openGallery();
                    });
                    rvOrder.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat riwayat pesanan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseOrderHistory> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                uploadBuktiBayar(selectedOrder.getTrans_id(), imageUri);
            }
        }
    }

    private void uploadBuktiBayar(int transId, Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            String fileName = getFileName(uri);

            // Jika nama file tidak ditemukan, gunakan nama acak
            if (fileName == null || fileName.isEmpty()) {
                fileName = "bukti_bayar_" + UUID.randomUUID().toString() + ".jpg";
            }

            File file = new File(requireContext().getCacheDir(), fileName);
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
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Upload berhasil", Toast.LENGTH_SHORT).show();
                        loadOrderHistory();
                    } else {
                        Toast.makeText(requireContext(), "Upload gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;

        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    // Periksa apakah kolom ada sebelum mencoba mengaksesnya
                    if (columnIndex >= 0) {
                        result = cursor.getString(columnIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Jika masih null, coba dapatkan dari path
        if (result == null && uri.getPath() != null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }
}