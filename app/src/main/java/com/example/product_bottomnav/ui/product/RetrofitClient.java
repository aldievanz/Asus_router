package com.example.product_bottomnav.ui.product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static final String BASE_URL = ServerAPI.BASE_URL; // Ganti dengan URL yang benar

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()  // Mengaktifkan mode lenient untuk JSON yang lebih fleksibel
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)  // Gunakan BASE_URL yang benar
                    .addConverterFactory(GsonConverterFactory.create(gson))  // Menggunakan Gson untuk konversi JSON
                    .build();
        }
        return retrofit;
    }

    public static RegisterAPI getApiService() {
        return getRetrofitInstance().create(RegisterAPI.class);
    }
}
