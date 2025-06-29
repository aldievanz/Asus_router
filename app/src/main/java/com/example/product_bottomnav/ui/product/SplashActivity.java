package com.example.product_bottomnav.ui.product;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;
import com.example.product_bottomnav.R;
import com.example.product_bottomnav.MainActivity;
import com.example.product_bottomnav.ui.product.SharedPrefManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Animasi tetap sama
        CircleImageView logoSplash = findViewById(R.id.logoSplash);
        TextView textView = findViewById(R.id.textView);

        ObjectAnimator fadeInLogo = ObjectAnimator.ofFloat(logoSplash, "alpha", 0f, 1f);
        fadeInLogo.setDuration(1500).start();

        ObjectAnimator fadeInText = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
        fadeInText.setDuration(1500).start();

        AlphaAnimation blink = new AlphaAnimation(0.0f, 1.0f);
        blink.setDuration(1500);
        blink.setStartOffset(1500);
        blink.setRepeatMode(AlphaAnimation.REVERSE);
        blink.setRepeatCount(AlphaAnimation.INFINITE);
        textView.startAnimation(blink);

        // Navigasi setelah delay
        new Handler().postDelayed(() -> {
            SharedPrefManager sharedPref = SharedPrefManager.getInstance(this);

            Intent intent = new Intent(this, MainActivity.class);

            if (!sharedPref.isLoggedIn()) {
                intent.putExtra("OPEN_GUEST_PROFILE", true);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}