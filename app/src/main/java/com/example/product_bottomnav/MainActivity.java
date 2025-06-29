package com.example.product_bottomnav;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.product_bottomnav.databinding.ActivityMainBinding;
import com.example.product_bottomnav.ui.product.LoginActivity;
import com.example.product_bottomnav.ui.product.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefManager = SharedPrefManager.getInstance(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();

        // Handle guest profile dari SplashActivity
        if (getIntent() != null && getIntent().getBooleanExtra("OPEN_GUEST_PROFILE", false)) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.text_home);
        }
    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.text_home,
                R.id.navigation_product,
                R.id.navigation_dashboard,
                R.id.navigation_notifications)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Custom handling untuk profile/guest
        navView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_notifications && !sharedPrefManager.isLoggedIn()) {
                navController.navigate(R.id.navigation_guest);
                return true;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
    }
}