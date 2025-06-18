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

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        CircleImageView logoSplash = findViewById(R.id.logoSplash);
        TextView textView = findViewById(R.id.textView);

        // Munculkan logo perlahan (fade in) dalam 1.5 detik
        ObjectAnimator fadeInLogo = ObjectAnimator.ofFloat(logoSplash, "alpha", 0f, 1f);
        fadeInLogo.setDuration(1500);
        fadeInLogo.start();

        // Munculkan teks perlahan (fade in) dalam 1.5 detik
        ObjectAnimator fadeInText = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
        fadeInText.setDuration(1500);
        fadeInText.start();

        // Animasi berkedip (blink) pada teks dalam 1.5 detik
        AlphaAnimation blink = new AlphaAnimation(0.0f, 1.0f);
        blink.setDuration(1500);
        blink.setStartOffset(1500);
        blink.setRepeatMode(AlphaAnimation.REVERSE);
        blink.setRepeatCount(AlphaAnimation.INFINITE);
        textView.startAnimation(blink);

        // Lanjutkan ke aktivitas Login setelah delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // agar tidak kembali ke splash saat tekan tombol back
        }, SPLASH_DELAY);
    }
}
