package com.androidcodeman.simpleimagegallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //agregar animaciones
        Animation animacion1 = AnimationUtils.loadAnimation(this,R.anim.desplazamientoarriba);
        Animation animacion2 = AnimationUtils.loadAnimation(this,R.anim.desplazamientoabajo);

        TextView uno = findViewById(R.id.unoTextView2);
        TextView dos = findViewById(R.id.dosTextView2);
        ImageView logo = findViewById(R.id.logoimageView2);

        uno.setAnimation(animacion2);
        dos.setAnimation(animacion2);
        logo.setAnimation(animacion1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this,CamaraActivity.class);
                startActivity(intent);
            }
        },5000);

    }
}