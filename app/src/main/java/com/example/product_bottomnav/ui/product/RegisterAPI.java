package com.example.product_bottomnav.ui.product;

import com.example.product_bottomnav.ui.notifications.ResponseOrderHistory;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RegisterAPI {
    // Authentication Endpoints
    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> register(
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("update_password.php")
    Call<ChangePasswordResponse> changePassword(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("get_login.php")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password
    );

    // Profile Endpoints
    @GET("get_profile.php")
    Call<UserResponse> getProfile(@Query("email") String email);

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

    @Multipart
    @POST("upload_profile_image.php")
    Call<ImageUploadResponse> uploadProfileImageWithEmail(
            @Part MultipartBody.Part image,
            @Part("email") RequestBody email
    );

    // Product Endpoints
    @GET("get_product.php")
    Call<List<Product>> getProducts();

    @FormUrlEncoded
    @POST("update_viewer.php")
    Call<ResponseBody> updateViewer(@Field("kode") String kode);

    // Shipping Endpoints
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

    // Order Endpoints
    @GET("getOrderHistory.php")
    Call<ResponseOrderHistory> getOrderHistory(
            @Query("id") int idPelanggan
    );

    @Multipart
    @POST("create_order.php")
    Call<ResponseBody> createOrder(
            @Part("id") RequestBody idPelanggan,
            @Part("nama_kirim") RequestBody namaKirim,
            @Part("email_kirim") RequestBody emailKirim,
            @Part("telp_kirim") RequestBody telpKirim,
            @Part("alamat_kirim") RequestBody alamatKirim,
            @Part("kota_kirim") RequestBody kotaKirim,
            @Part("provinsi_kirim") RequestBody provinsiKirim,
            @Part("kodepos_kirim") RequestBody kodeposKirim,
            @Part("lama_kirim") RequestBody lamaKirim,
            @Part("subtotal") RequestBody subtotal,
            @Part("ongkir") RequestBody ongkir,
            @Part("total_bayar") RequestBody totalBayar,
            @Part("metode_bayar") RequestBody metodeBayar,
            @Part("status") RequestBody status,
            @Part List<MultipartBody.Part> orderDetails
    );

    @Multipart
    @POST("upload_bukti_bayar.php")
    Call<ResponseBody> uploadBuktiBayar(
            @Part("trans_id") RequestBody transId,
            @Part MultipartBody.Part buktiBayar,
            @Part("metode_bayar") RequestBody metodeBayar // Tambahkan parameter ini
    );

}