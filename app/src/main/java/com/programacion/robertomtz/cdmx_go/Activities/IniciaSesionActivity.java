package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.programacion.robertomtz.cdmx_go.Classes.MiBaseDeDatos;
import com.programacion.robertomtz.cdmx_go.Classes.Usuario;
import com.programacion.robertomtz.cdmx_go.R;

public class IniciaSesionActivity extends AppCompatActivity {

    private Intent intent;
    private EditText usuario;
    private EditText password;
    private Button aceptar;
    private TextView olvidePass;
    private MiBaseDeDatos mbdd;
    // FB Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicia_sesion);

        inicializaVariables();
        agregaListeners();
    }

    private void agregaListeners() {

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = usuario.getText().toString();
                String pass = password.getText().toString();

                // Buscamos en la base de datos
                Usuario usuarioABuscar = mbdd.buscaUsuario(user);

                if (usuarioABuscar != null) {
                    Toast.makeText(IniciaSesionActivity.this, usuarioABuscar.getUserName(), Toast.LENGTH_SHORT).show();
                    if (usuarioABuscar.getPassword().equals(pass)) {
                        intent = new Intent(IniciaSesionActivity.this, PrincipalActivity.class);
                        intent.putExtra("user", usuarioABuscar);
                        startActivity(intent);
                    } else
                        Toast.makeText(IniciaSesionActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(IniciaSesionActivity.this, "Usuario no registrado", Toast.LENGTH_SHORT).show();
            }
        });

        olvidePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(IniciaSesionActivity.this, OlvidePasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void inicializaVariables() {
        usuario = (EditText) findViewById(R.id.inicia_sesion_et_usuario);
        usuario.requestFocus();
        password = (EditText) findViewById(R.id.inicia_sesion_et_password);
        aceptar = (Button) findViewById(R.id.inicia_sesion_btn_aceptar);
        olvidePass = (TextView) findViewById(R.id.inicia_sesion_tv_olvide_pass);

        mbdd = MiBaseDeDatos.getInstance();
    }
}
