package com.example.product_bottomnav.ui.product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerAPI {
    // URL base untuk API dan gambar yang bisa diganti dengan mudah
//     public static String BASE_URL = "https://aldievanz.my.id/android/";
    public static String BASE_URL = "http://192.168.1.15/android/";  // Gantilah dengan IP atau domain baru
    public static String BASE_URL_Image = BASE_URL + "images/";
    public static final String BASE_URL_Image_Avatar = BASE_URL + "images/avatars/";
    // URL gambar default
    public static String DEFAULT_IMAGE_URL = BASE_URL_Image + "default_profile.jpg";
    private static Retrofit retrofit;
    public static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
