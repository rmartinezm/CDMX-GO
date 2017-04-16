package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.programacion.robertomtz.cdmx_go.R;

public class InicioActivity extends AppCompatActivity {

    private Button iniciarSesion;
    private Button crearCuenta;
    private Intent intent;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        inicializaVariables();
        agregaListeners();
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

    private void inicializaVariables(){
        iniciarSesion = (Button) findViewById(R.id.inicio_btn_inicia_sesion);
        crearCuenta = (Button) findViewById(R.id.inicio_btn_crear_cuenta);
        logo = (ImageView) findViewById(R.id.inicio_iv_logo);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}
