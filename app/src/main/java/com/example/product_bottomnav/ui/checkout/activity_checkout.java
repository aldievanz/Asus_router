package com.example.product_bottomnav.ui.checkout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.product_bottomnav.R;
import com.example.product_bottomnav.ui.dashboard.OrderItem;
import com.example.product_bottomnav.ui.product.RegisterAPI;
import com.example.product_bottomnav.ui.product.ServerAPI;
import com.example.product_bottomnav.ui.product.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

public class activity_checkout extends AppCompatActivity {

    // Declare UI components
    private TextView tvUserName;
    private EditText etShippingAddress, etShippingPhone, etKodePos;
    private Spinner spinnerProvince, spinnerCity, spinnerCourier;
    private RadioGroup radioPaymentMethod;
    private Button btnCheckShipping, btnPayNow;
    private TextView tvSubtotal, tvShippingCost, tvShippingEstimation, tvTotalPayment;
    private RecyclerView recyclerViewOrderItems;
    private OrderItemAdapter orderItemAdapter;

    // Province and city data
    private ArrayList<String> provinceNames = new ArrayList<>();
    private ArrayList<Integer> provinceIds = new ArrayList<>();
    private ArrayList<String> cityNames = new ArrayList<>();
    private ArrayList<Integer> cityIds = new ArrayList<>();

    // Shipping and payment data
    private int selectedCityId = 0;
    private double subtotal = 0;
    private double shippingCost = 0;
    private String shippingEstimation = "";
    private String selectedCourier = "";
    private String originCityId = "399"; // Origin city ID (can be adjusted)

    // User data
    private int userId;
    private String userName = "";
    private String userAddress = "";
    private String userPhone = "";
    private String userCity = "";
    private String userProvince = "";
    private String userPostalCode = "";

    // Currency format
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize currency format
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        currencyFormat.setMaximumFractionDigits(0);

        initUI();
        loadUserData();
        setupRecyclerView();
        setupCourierSpinner();
        setupListeners();
    }

    private void initUI() {
        tvUserName = findViewById(R.id.tvUserName);
        etShippingAddress = findViewById(R.id.etShippingAddress);
        etShippingPhone = findViewById(R.id.etShippingPhone);
        etKodePos = findViewById(R.id.etKodePos);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerCourier = findViewById(R.id.spinnerCourier);
        btnCheckShipping = findViewById(R.id.btnCheckShipping);
        radioPaymentMethod = findViewById(R.id.radioPaymentMethod);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingCost = findViewById(R.id.tvShippingCost);
        tvShippingEstimation = findViewById(R.id.tvShippingEstimation);
        tvTotalPayment = findViewById(R.id.tvTotalPayment);
        recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        btnPayNow = findViewById(R.id.btnPayNow);
    }

    private void setupRecyclerView() {
        ArrayList<OrderItem> orderItems = getIntent().getParcelableArrayListExtra("ORDER_ITEMS");

        if (orderItems == null) {
            orderItems = new ArrayList<>();
            Log.e("CHECKOUT_DEBUG", "Order items list is empty");
        }

        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrderItems.setHasFixedSize(true);

        orderItemAdapter = new OrderItemAdapter(orderItems);
        recyclerViewOrderItems.setAdapter(orderItemAdapter);

        calculateSubtotal(orderItems);
    }

    private void calculateSubtotal(List<OrderItem> orderItems) {
        subtotal = 0;
        for (OrderItem item : orderItems) {
            subtotal += item.getSubtotal();
        }
        tvSubtotal.setText(currencyFormat.format(subtotal));
        updateTotalPayment();
    }

    private void setupCourierSpinner() {
        List<String> couriers = Arrays.asList("Pilih Kurir", "jne", "tiki", "pos");
        ArrayAdapter<String> courierAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, couriers);
        courierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourier.setAdapter(courierAdapter);

        spinnerCourier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedCourier = parent.getItemAtPosition(position).toString();
                    resetShippingInfo();
                } else {
                    selectedCourier = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void resetShippingInfo() {
        shippingCost = 0;
        shippingEstimation = "";
        tvShippingCost.setText("-");
        tvShippingEstimation.setText("-");
        updateTotalPayment();
    }

    private void setupListeners() {
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position < provinceIds.size()) {
                    loadCities(provinceIds.get(position));
                    resetShippingInfo();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position < cityIds.size()) {
                    selectedCityId = cityIds.get(position);
                    resetShippingInfo();
                } else {
                    selectedCityId = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnCheckShipping.setOnClickListener(v -> {
            if (validateBeforeCheckShipping()) {
                checkShippingCost(originCityId, String.valueOf(selectedCityId), 1000, selectedCourier);
            }
        });

        btnPayNow.setOnClickListener(v -> processPayment());
    }

    private void loadUserData() {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        userId = sharedPrefManager.getUserId();
        userName = sharedPrefManager.getNama();
        userAddress = sharedPrefManager.getAlamat();
        userPhone = sharedPrefManager.getTelp();
        userCity = sharedPrefManager.getKota();
        userProvince = sharedPrefManager.getProvinsi();
        userPostalCode = sharedPrefManager.getKodepos();

        tvUserName.setText(userName);
        etShippingAddress.setText(userAddress);
        etShippingPhone.setText(userPhone);
        etKodePos.setText(userPostalCode);

        loadProvinces();
    }

    private void loadProvinces() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Memuat data provinsi...");
        pd.setCancelable(false);
        pd.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<ResponseBody> call = api.getProvinsi();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                if (!response.isSuccessful()) {
                    handleError("Gagal memuat provinsi: " + response.code(), null);
                    return;
                }

                try {
                    String jsonResponse = response.body().string();
                    JSONObject json = new JSONObject(jsonResponse);
                    JSONObject rajaOngkir = json.getJSONObject("rajaongkir");
                    JSONArray results = rajaOngkir.getJSONArray("results");

                    provinceNames.clear();
                    provinceIds.clear();

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject province = results.getJSONObject(i);
                        provinceNames.add(province.getString("province"));
                        provinceIds.add(province.getInt("province_id"));
                    }

                    provinceNames.add(0, "Pilih Provinsi");
                    provinceIds.add(0, 0);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(activity_checkout.this,
                            android.R.layout.simple_spinner_item, provinceNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProvince.setAdapter(adapter);

                    if (!userProvince.isEmpty()) {
                        for (int i = 0; i < provinceNames.size(); i++) {
                            if (provinceNames.get(i).equalsIgnoreCase(userProvince)) {
                                spinnerProvince.setSelection(i);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    handleError("Gagal memproses data provinsi", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                handleError("Error koneksi saat memuat provinsi", t);
            }
        });
    }

    private void loadCities(int provinceId) {
        if (provinceId == 0) {
            cityNames.clear();
            cityNames.add("Pilih Kota");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, cityNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCity.setAdapter(adapter);
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Memuat data kota...");
        pd.setCancelable(false);
        pd.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<ResponseBody> call = api.getKota(provinceId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                if (!response.isSuccessful()) {
                    handleError("Gagal memuat kota: " + response.code(), null);
                    return;
                }

                try {
                    String jsonResponse = response.body().string();
                    JSONObject json = new JSONObject(jsonResponse);
                    JSONObject rajaOngkir = json.getJSONObject("rajaongkir");
                    JSONArray results = rajaOngkir.getJSONArray("results");

                    cityNames.clear();
                    cityIds.clear();

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject city = results.getJSONObject(i);
                        cityNames.add(city.getString("city_name"));
                        cityIds.add(city.getInt("city_id"));
                    }

                    cityNames.add(0, "Pilih Kota");
                    cityIds.add(0, 0);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(activity_checkout.this,
                            android.R.layout.simple_spinner_item, cityNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCity.setAdapter(adapter);

                    if (!userCity.isEmpty()) {
                        for (int i = 0; i < cityNames.size(); i++) {
                            if (cityNames.get(i).equalsIgnoreCase(userCity)) {
                                spinnerCity.setSelection(i);
                                break;
                            }
                        }
                    }
                } catch (IOException | JSONException e) {
                    handleError("Gagal memproses data kota", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                handleError("Error koneksi saat memuat kota", t);
            }
        });
    }

    private boolean validateBeforeCheckShipping() {
        if (selectedCityId == 0) {
            Toast.makeText(this, "Pilih kota tujuan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedCourier.isEmpty()) {
            Toast.makeText(this, "Pilih kurir terlebih dahulu", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void checkShippingCost(String origin, String destination, int weight, String courier) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Menghitung ongkos kirim...");
        pd.setCancelable(false);
        pd.show();

        if (weight < 1000) {
            weight = 1000;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<ResponseBody> call = api.cekOngkir(origin, destination, weight, courier);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                if (!response.isSuccessful()) {
                    showShippingError(courier, "Gagal mendapatkan ongkir: " + response.code());
                    return;
                }

                try {
                    String jsonResponse = response.body().string();
                    JSONObject json = new JSONObject(jsonResponse);
                    JSONObject rajaOngkir = json.getJSONObject("rajaongkir");
                    JSONObject status = rajaOngkir.getJSONObject("status");

                    if (status.getInt("code") != 200) {
                        showShippingError(courier, status.getString("description"));
                        return;
                    }

                    if (!rajaOngkir.has("results") || rajaOngkir.getJSONArray("results").length() == 0) {
                        showShippingError(courier, "Layanan tidak tersedia untuk rute ini");
                        return;
                    }

                    JSONArray results = rajaOngkir.getJSONArray("results");
                    JSONObject result = results.getJSONObject(0);

                    if (!result.has("costs") || result.getJSONArray("costs").length() == 0) {
                        showShippingError(courier, "Tidak ada layanan pengiriman tersedia");
                        return;
                    }

                    JSONArray costs = result.getJSONArray("costs");
                    processAvailableServices(courier, costs);

                } catch (IOException | JSONException e) {
                    handleError("Gagal memproses ongkir " + courier.toUpperCase(), e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                handleError("Error koneksi saat cek ongkir", t);
            }
        });
    }

    private void processAvailableServices(String courier, JSONArray costs) throws JSONException {
        List<String> preferredServices = new ArrayList<>();
        String serviceNameKey = "service";

        switch (courier.toLowerCase()) {
            case "jne":
                preferredServices.addAll(Arrays.asList("REG", "OKE", "YES"));
                break;
            case "tiki":
                preferredServices.addAll(Arrays.asList("REG", "ECO", "ONS"));
                break;
            case "pos":
                preferredServices.addAll(Arrays.asList("Paket Kilat Khusus", "Express Next Day Barang"));
                break;
        }

        JSONObject selectedService = null;
        JSONObject fallbackService = null;

        for (int i = 0; i < costs.length(); i++) {
            JSONObject service = costs.getJSONObject(i);
            String serviceName = service.getString(serviceNameKey).toUpperCase();

            if (fallbackService == null) {
                fallbackService = service;
            }

            for (String preferred : preferredServices) {
                if (serviceName.contains(preferred.toUpperCase())) {
                    selectedService = service;
                    break;
                }
            }
            if (selectedService != null) break;
        }

        if (selectedService == null && fallbackService != null) {
            selectedService = fallbackService;
        }

        if (selectedService == null) {
            showShippingError(courier, "Tidak ada layanan pengiriman yang tersedia");
            return;
        }

        JSONArray costDetails = selectedService.getJSONArray("cost");
        if (costDetails.length() > 0) {
            JSONObject costDetail = costDetails.getJSONObject(0);
            shippingCost = costDetail.getDouble("value");

            String etd = costDetail.getString("etd");
            if (etd.contains("HARI") || etd.contains("DAY")) {
                shippingEstimation = etd;
            } else {
                shippingEstimation = etd + " Hari";
            }

            runOnUiThread(() -> {
                tvShippingCost.setText(currencyFormat.format(shippingCost));
                tvShippingEstimation.setText(shippingEstimation);
                updateTotalPayment();

                Toast.makeText(activity_checkout.this,
                        "Ongkir " + courier.toUpperCase() + " berhasil dihitung",
                        Toast.LENGTH_SHORT).show();
            });
        } else {
            showShippingError(courier, "Tidak ada detail biaya untuk layanan ini");
        }
    }

    private void showShippingError(String courier, String message) {
        runOnUiThread(() -> {
            Toast.makeText(activity_checkout.this,
                    courier.toUpperCase() + ": " + message,
                    Toast.LENGTH_LONG).show();

            tvShippingCost.setText("-");
            tvShippingEstimation.setText("-");
            shippingCost = 0;
            updateTotalPayment();
        });
    }

    private void updateTotalPayment() {
        double total = subtotal + shippingCost;
        tvTotalPayment.setText(currencyFormat.format(total));
    }

    private boolean validatePayment() {
        String address = etShippingAddress.getText().toString().trim();
        String phone = etShippingPhone.getText().toString().trim();
        String postalCode = etKodePos.getText().toString().trim();

        if (address.isEmpty()) {
            etShippingAddress.setError("Alamat pengiriman harus diisi");
            etShippingAddress.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etShippingPhone.setError("Nomor telepon harus diisi");
            etShippingPhone.requestFocus();
            return false;
        }

        if (!phone.matches("^[0-9]{10,13}$")) {
            etShippingPhone.setError("Nomor telepon tidak valid");
            etShippingPhone.requestFocus();
            return false;
        }

        if (postalCode.isEmpty()) {
            etKodePos.setError("Kode pos harus diisi");
            etKodePos.requestFocus();
            return false;
        }

        if (!postalCode.matches("^[0-9]{5}$")) {
            etKodePos.setError("Kode pos harus 5 digit angka");
            etKodePos.requestFocus();
            return false;
        }

        if (selectedCityId == 0) {
            Toast.makeText(this, "Pilih kota tujuan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (radioPaymentMethod.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Pilih metode pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (shippingCost <= 0) {
            Toast.makeText(this, "Harap cek ongkos kirim terlebih dahulu", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void processPayment() {
        if (!validatePayment()) return;

        RadioButton selectedPayment = findViewById(radioPaymentMethod.getCheckedRadioButtonId());
        String paymentMethod = selectedPayment.getText().toString();

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Memproses pesanan...");
        pd.setCancelable(false);
        pd.show();

        // Implement saving order to database or API
        saveOrderToDatabase(paymentMethod);
    }

    private void saveOrderToDatabase(String paymentMethod) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Memproses pesanan...");
        pd.setCancelable(false);
        pd.show();

        // Get data from form
        String namaKirim = tvUserName.getText().toString();
        String alamatKirim = etShippingAddress.getText().toString().trim();
        String kotaKirim = spinnerCity.getSelectedItem().toString();
        String provinsiKirim = spinnerProvince.getSelectedItem().toString();
        String kodeposKirim = etKodePos.getText().toString().trim();
        String telpKirim = etShippingPhone.getText().toString().trim();
        String emailKirim = SharedPrefManager.getInstance(this).getEmail();

        // Calculate total
        double totalBayar = subtotal + shippingCost;

        // Get order items from adapter
        List<OrderItem> orderItems = orderItemAdapter.getOrderItems();

        // Create request to save order
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);

        // Create request body
        RequestBody idPelanggan = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
        RequestBody rNamaKirim = RequestBody.create(MediaType.parse("text/plain"), namaKirim);
        RequestBody rEmailKirim = RequestBody.create(MediaType.parse("text/plain"), emailKirim);
        RequestBody rTelpKirim = RequestBody.create(MediaType.parse("text/plain"), telpKirim);
        RequestBody rAlamatKirim = RequestBody.create(MediaType.parse("text/plain"), alamatKirim);
        RequestBody rKotaKirim = RequestBody.create(MediaType.parse("text/plain"), kotaKirim);
        RequestBody rProvinsiKirim = RequestBody.create(MediaType.parse("text/plain"), provinsiKirim);
        RequestBody rKodeposKirim = RequestBody.create(MediaType.parse("text/plain"), kodeposKirim);
        RequestBody rLamaKirim = RequestBody.create(MediaType.parse("text/plain"), shippingEstimation);
        RequestBody rSubtotal = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(subtotal));
        RequestBody rOngkir = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(shippingCost));
        RequestBody rTotalBayar = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(totalBayar));
        RequestBody rMetodeBayar = RequestBody.create(MediaType.parse("text/plain"), paymentMethod.toLowerCase());
        RequestBody rStatus = RequestBody.create(MediaType.parse("text/plain"), "menunggu");

        // Create list for order details
        List<MultipartBody.Part> detailParts = new ArrayList<>();
        for (OrderItem item : orderItems) {
            RequestBody kode = RequestBody.create(MediaType.parse("text/plain"), item.getKode());
            RequestBody harga = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(item.getHarga()));
            RequestBody qty = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(item.getQuantity()));
            RequestBody bayar = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(item.getSubtotal()));

            detailParts.add(MultipartBody.Part.createFormData("kode[]", null, kode));
            detailParts.add(MultipartBody.Part.createFormData("harga[]", null, harga));
            detailParts.add(MultipartBody.Part.createFormData("qty[]", null, qty));
            detailParts.add(MultipartBody.Part.createFormData("bayar[]", null, bayar));
        }

        Call<ResponseBody> call = api.createOrder(
                idPelanggan,
                rNamaKirim,
                rEmailKirim,
                rTelpKirim,
                rAlamatKirim,
                rKotaKirim,
                rProvinsiKirim,
                rKodeposKirim,
                rLamaKirim,
                rSubtotal,
                rOngkir,
                rTotalBayar,
                rMetodeBayar,
                rStatus,
                detailParts
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();
                if (response.isSuccessful()) {
                    try {
                        String jsonResponse = response.body().string();
                        JSONObject json = new JSONObject(jsonResponse);

                        if (json.getBoolean("status")) {
                            Toast.makeText(activity_checkout.this, "Pesanan berhasil dibuat", Toast.LENGTH_SHORT).show();

                            // If payment method is transfer, navigate to upload payment proof
                            if (paymentMethod.equalsIgnoreCase("transfer")) {
                                int transId = json.getInt("trans_id");
                                Intent intent = new Intent(activity_checkout.this, UploadBuktiBayarActivity.class);
                                intent.putExtra("TRANS_ID", transId);
                                intent.putExtra("TOTAL_BAYAR", totalBayar);
                                startActivity(intent);
                            }

                            finish(); // Close checkout activity
                        } else {
                            Toast.makeText(activity_checkout.this, "Gagal: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        handleError("Gagal memproses response", e);
                    }
                } else {
                    handleError("Gagal membuat pesanan: " + response.code(), null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                handleError("Error koneksi saat membuat pesanan", t);
            }
        });
    }

    private void handleError(String message, Throwable t) {
        Log.e("CHECKOUT_ERROR", message, t);
        runOnUiThread(() -> {
            String errorMsg = message;
            if (t != null) {
                errorMsg += ": " + t.getMessage();
            }
            Toast.makeText(activity_checkout.this, errorMsg, Toast.LENGTH_LONG).show();
        });
    }
}
