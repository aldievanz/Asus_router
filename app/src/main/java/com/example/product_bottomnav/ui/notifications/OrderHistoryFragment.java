package com.example.product_bottomnav.ui.notifications;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.util.List;

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

    private RecyclerView rvOrder;
    private OrderHistoryAdapter adapter;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;
    private OrderModel selectedOrder;
    private int userId;
    private String metodeBayar = "transfer";  // Menambahkan metode pembayaran default

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        // Mendapatkan userId dari SharedPreferences
        userId = SharedPrefManager.getInstance(getContext()).getUserId();

        rvOrder = view.findViewById(R.id.rvOrderHistory);
        rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));

        loadOrderHistory(); // Memuat riwayat pesanan saat view dibuka

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

                    adapter = new OrderHistoryAdapter(getContext(), orderList, new OrderHistoryAdapter.OnUploadClickListener() {
                        @Override
                        public void onPilihGambarClicked(OrderModel order) {
                            selectedOrder = order;
                            openGallery(); // Buka galeri untuk memilih gambar
                        }
                    });

                    rvOrder.setAdapter(adapter); // Set adapter ke RecyclerView
                } else {
                    Toast.makeText(getContext(), "Gagal: " + (response.body() != null ? response.body().getMessage() : "Null response"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseOrderHistory> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Menunggu gambar yang dipilih
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Mendapatkan URI gambar yang dipilih
            uploadBuktiBayar(selectedOrder.getTrans_id(), imageUri, metodeBayar); // Mengirimkan metodeBayar
        }
    }

    private void uploadBuktiBayar(int transId, Uri imageUri, String metodeBayar) {
        String filePath = getRealPathFromURI(imageUri); // Mendapatkan path file gambar
        if (filePath == null) {
            Toast.makeText(getContext(), "Gagal membaca file gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("bukti_bayar", file.getName(), requestFile);

        RequestBody transIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(transId));
        RequestBody metodeBayarBody = RequestBody.create(MediaType.parse("text/plain"), metodeBayar);  // Mengirimkan metodeBayar

        // Retrofit untuk mengirimkan data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<ResponseBody> call = api.uploadBuktiBayar(transIdBody, body, metodeBayarBody);  // Menambahkan metodeBayar

        // Eksekusi API call
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Upload berhasil", Toast.LENGTH_SHORT).show();
                    loadOrderHistory(); // Refresh list setelah upload berhasil
                } else {
                    Toast.makeText(getContext(), "Upload gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Upload gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(colIndex);
            cursor.close();
            return result;
        }
        return null;
    }
}
