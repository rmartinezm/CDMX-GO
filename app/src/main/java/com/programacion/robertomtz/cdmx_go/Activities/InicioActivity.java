package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.programacion.robertomtz.cdmx_go.R;

public class InicioActivity extends AppCompatActivity {

    // Views
    private Button iniciarSesion;
    private Button crearCuenta;
    private Intent intent;

    // Firebase Auth
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        inicializaVariables();
        agregaListeners();
    }

    private void inicializaVariables(){
        iniciarSesion = (Button) findViewById(R.id.inicio_btn_inicia_sesion);
        crearCuenta = (Button) findViewById(R.id.inicio_btn_crear_cuenta);

        auth = FirebaseAuth.getInstance();

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                    irPrincipalActivity();
            }
        };

    }

    private void agregaListeners() {

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(InicioActivity.this, IniciaSesionActivity.class);
                startActivity(intent);
            }
        });

        crearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(InicioActivity.this, CreaCuentaActivity.class);
                startActivity(intent);
            }
        });

    }

    // Metodo auxiliar para mandar intent con banderas
    private void irPrincipalActivity() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Hacemos que con el boton hacia atras se minimice la app
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // Escuchamos para ver si ya tiene sesion inciada o no
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    // Quitamos el listener
    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(listener);
    }
}
