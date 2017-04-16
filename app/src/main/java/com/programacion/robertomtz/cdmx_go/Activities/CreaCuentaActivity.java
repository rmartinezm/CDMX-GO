package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.programacion.robertomtz.cdmx_go.R;

public class CreaCuentaActivity extends AppCompatActivity {

    private EditText user;
    private EditText password;
    private EditText correo;
    private Switch aSwitch;
    private Button aceptar;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_cuenta);

        inicializaVariables();
        agregaListeners();
    }

    private void inicializaVariables(){
        user = (EditText) findViewById(R.id.crear_cuenta_et_usuario);
        user.requestFocus();
        password = (EditText) findViewById(R.id.crear_cuenta_et_password);
        correo = (EditText) findViewById(R.id.crear_cuenta_et_correo);

        aSwitch = (Switch) findViewById(R.id.crear_cuenta_switch_informacion);

        aceptar = (Button) findViewById(R.id.crear_cuenta_btn_crear_cuenta);

        image = (ImageView) findViewById(R.id.crear_cuenta_iv_image);
    }

    private void agregaListeners() {
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario = user.getText().toString();
                String pass  = password.getText().toString();
                String email = correo.getText().toString();
                boolean info = aSwitch.isChecked();

                if (usuario.isEmpty() || pass.isEmpty() || email.isEmpty())
                    Toast.makeText(CreaCuentaActivity.this, "Ingrese todos los datos necesarios.", Toast.LENGTH_SHORT).show();
                else{
                    Intent intent = new Intent(CreaCuentaActivity.this, PrincipalActivity.class);
                    // Usuario user = new Usuario (datos del json);
                    // intent.putExtra("user", user);
                    startActivity(intent);
                }

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rmartinezm"));
                startActivity(intent);
            }});
    }
}
