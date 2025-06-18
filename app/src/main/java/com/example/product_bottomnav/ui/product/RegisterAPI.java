// Updated RegisterAPI.java with correct uploadProfileImageWithEmail method
package com.example.product_bottomnav.ui.product;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RegisterAPI {
    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> register(
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("get_login.php")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("get_profile.php")
    Call<UserResponse> getProfile(@Query("email") String email);

    @GET("get_provinsi.php")
    Call<ResponseBody> getProvinsi();

    @GET("get_kota.php")
    Call<ResponseBody> getKota(@Query("province_id") int provinceId);

    @FormUrlEncoded
    @POST("api_ongkir.php")
    Call<ResponseBody> cekOngkir(
            @Field("origin") String origin,
            @Field("destination") String destination,
            @Field("weight") int weight,
            @Field("courier") String courier
    );

    @GET("get_product.php")
    Call<List<Product>> getProducts();

    @FormUrlEncoded
    @POST("update_viewer.php")
    Call<ResponseBody> updateViewer(@Field("kode") String kode);

    @GET("get_product_by_id.php")
    Call<Product> getProductById(@Query("kode") String kode);

    @FormUrlEncoded
    @POST("get_user_data.php")
    Call<ResponseBody> getUserData(@Field("user_id") int userId);

    @FormUrlEncoded
    @POST("update_profile_fields.php")
    Call<ResponseModel> updateProfileFields(
            @Field("user_id") String userId,
            @Field("nama") String nama,
            @Field("alamat") String alamat,
            @Field("kota") String kota,
            @Field("provinsi") String provinsi,
            @Field("telp") String telp,
            @Field("kodepos") String kodepos
    );

    @GET("get_user.php")
    Call<UserModel> getUserById(@Query("id") int id);

    // ✅ Updated version with email & imageupload support, and ensures correct JSON structure
    @Multipart
    @POST("upload_profile_image.php")
    Call<ImageUploadResponse> uploadProfileImageWithEmail(
            @Part MultipartBody.Part image,
            @Part("email") RequestBody email
    );
}
