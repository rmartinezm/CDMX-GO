package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.programacion.robertomtz.cdmx_go.R;

public class SplashScreen extends AppCompatActivity {

    // Tiempo que se mostrará el SplashScreen
    private static int SPLASH_TIME_OUT = 3500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hacemos fullscreen la pantalla
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        // Quitamos el action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // Para que el activity se muestre solo un determinado tiempo
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                try {
                    // Si ocurre una exception aqui entonces el usuario aun no ha iniciado sesión
                    // por lo tanto lo mandamos a la pantalla de inicio, en otro caso a la ventana principal
                    firebaseAuth.getCurrentUser().getEmail();
                    intent = new Intent(SplashScreen.this, PrincipalActivity.class);
                }catch (Exception e){
                    intent = new Intent(SplashScreen.this, InicioActivity.class);
                }

                // Banderas para que no puedan volver a este activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
