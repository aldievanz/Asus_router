package com.example.product_bottomnav.ui.product;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "login_pref";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAMA = "nama";
    private static final String KEY_ALAMAT = "alamat";
    private static final String KEY_KOTA = "kota";
    private static final String KEY_PROVINSI = "provinsi";
    private static final String KEY_TELP = "telp";
    private static final String KEY_KODEPOS = "kodepos";
    private static final String KEY_IMAGE_URL = "profile_image";

    private static SharedPrefManager instance;
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private Context context;

    // Constructor untuk SharedPrefManager
    private SharedPrefManager(Context context) {
        this.context = context.getApplicationContext();
        pref = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Singleton pattern untuk memastikan hanya ada satu instance SharedPrefManager
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    // Fungsi untuk login dan menyimpan data pengguna
    public void loginUser(String username, String password, String userId, String email,
                          String nama, String profile_image, String alamat, String kota, String provinsi,
                          String telp, String kodepos) {
        if (isLoggedIn()) {
            clearUserSessionData();  // Hapus data session sebelumnya
        }

        // Menyimpan data login
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAMA, nama);
        editor.putString(KEY_IMAGE_URL, profile_image);
        try {
            editor.putInt(KEY_USER_ID, Integer.parseInt(userId));  // Menyimpan ID pengguna
        } catch (NumberFormatException e) {
            editor.putInt(KEY_USER_ID, -1);  // ID tidak valid
        }
        saveProfile(nama, alamat, kota, provinsi, telp, kodepos);  // Menyimpan profil pengguna
        editor.apply();
    }


    // Menghapus data session pengguna yang telah login
    private void clearUserSessionData() {
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_NAMA);
        editor.remove(KEY_ALAMAT);
        editor.remove(KEY_KOTA);
        editor.remove(KEY_PROVINSI);
        editor.remove(KEY_TELP);
        editor.remove(KEY_KODEPOS);
        editor.remove(KEY_IMAGE_URL);
        editor.apply();
    }

    // Menyimpan URL gambar profil
    public void saveImageUrl(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty() && (imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
            editor.putString(KEY_IMAGE_URL, imageUrl);
            editor.apply();
        }
    }

    // Mengambil URL gambar profil
    public String getImageUrl() {
        return pref.getString(KEY_IMAGE_URL, "");
    }

    // Menghapus URL gambar profil
    public void clearImageUrl() {
        editor.remove(KEY_IMAGE_URL);
        editor.apply();
    }

    // Menyimpan data profil pengguna
    public void saveProfile(String nama, String alamat, String kota,
                            String provinsi, String telp, String kodepos) {
        editor.putString(KEY_NAMA, nama);
        editor.putString(KEY_ALAMAT, alamat);
        editor.putString(KEY_KOTA, kota);
        editor.putString(KEY_PROVINSI, provinsi);
        editor.putString(KEY_TELP, telp);
        editor.putString(KEY_KODEPOS, kodepos);
        editor.apply();
    }


    // Mengecek apakah pengguna sudah login
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Mengambil nama pengguna
    public String getNama() {
        return pref.getString(KEY_NAMA, "");
    }

    // Mengambil username pengguna
    public String getUsername() {
        return pref.getString(KEY_USERNAME, "User");
    }

    // Mengambil password pengguna
    public String getPassword() {
        return pref.getString(KEY_PASSWORD, "");
    }

    // Mengambil ID pengguna
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    // Mengambil email pengguna
    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    // Mengambil alamat pengguna
    public String getAlamat() {
        return pref.getString(KEY_ALAMAT, "");
    }

    // Mengambil kota pengguna
    public String getKota() {
        return pref.getString(KEY_KOTA, "");
    }

    // Mengambil provinsi pengguna
    public String getProvinsi() {
        return pref.getString(KEY_PROVINSI, "");
    }

    // Mengambil nomor telepon pengguna
    public String getTelp() {
        return pref.getString(KEY_TELP, "");
    }

    // Mengambil kode pos pengguna
    public String getKodepos() {
        return pref.getString(KEY_KODEPOS, "");
    }

    // Fungsi untuk logout dan menghapus data pengguna
    public void logout() {
        clearUserSessionData();
    }
}
