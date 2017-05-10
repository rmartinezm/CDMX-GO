package com.programacion.robertomtz.cdmx_go.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.programacion.robertomtz.cdmx_go.R;

public class CreaCuentaActivity extends AppCompatActivity implements View.OnClickListener{

    // Views
    private EditText etUser;
    private EditText etPassword;
    private EditText etRepitePassword;
    private Switch aSwitch;
    private Button btnAceptar;
    private ImageView ivImagen;
    private ProgressDialog progressDialog;
    //Auxiliares
    private Intent intent;
    private final String USUARIOS = "usuarios";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_cuenta);

        inicializaVariables();
    }

    private void inicializaVariables(){
        etUser = (EditText) findViewById(R.id.crear_cuenta_et_usuario);
        etUser.requestFocus();
        etPassword = (EditText) findViewById(R.id.crear_cuenta_et_password);
        etRepitePassword = (EditText) findViewById(R.id.crear_cuenta_et_repite_password);
        aSwitch = (Switch) findViewById(R.id.crear_cuenta_switch_informacion);
        progressDialog = new ProgressDialog(this);
        btnAceptar = (Button) findViewById(R.id.crear_cuenta_btn_crear_cuenta);
        btnAceptar.setOnClickListener(this);
        ivImagen = (ImageView) findViewById(R.id.crear_cuenta_iv_image);

        Glide.with(this)
                .load(R.drawable.logo)
                .into(ivImagen);
    }

    private void crearCuenta(final String user, final String password){
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(user, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            // Nos logeamos con el nuevo usuario creado
                            auth.signInWithEmailAndPassword(user, password);

                            // Agregamos a la base de datos al usuario
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(USUARIOS);
                            DatabaseReference currentUserDB = mDatabase.child(auth.getCurrentUser().getUid());

                            currentUserDB.child("userName").setValue("");
                            currentUserDB.child("email").setValue(user);
                            currentUserDB.child("photo").setValue("");
                            currentUserDB.child("coins").setValue(0);
                            currentUserDB.child("notificaciones").setValue(aSwitch.isChecked());

                            Intent intent1 = new Intent(CreaCuentaActivity.this, UserNameActivity.class);
                            intent1.putExtra("id", auth.getCurrentUser().getUid());
                            startActivity(intent1);
                        }else
                            Toast.makeText(CreaCuentaActivity.this, getResources().getString(R.string.error_crear_cuenta), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        String user = etUser.getText().toString().trim();
        String password = etPassword.getText().toString();
        String repitePassword = etRepitePassword.getText().toString();

        switch (id){
            case R.id.crear_cuenta_btn_crear_cuenta:

                if (user.isEmpty() || password.isEmpty() || repitePassword.isEmpty()) {
                    Toast.makeText(this, getResources().getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6){
                    Toast.makeText(this, getResources().getString(R.string.error_password_longitud), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(repitePassword)){
                    Toast.makeText(this, getResources().getString(R.string.error_passwords_no_coinciden), Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage(getResources().getString(R.string.creando_cuenta));
                progressDialog.show();

                crearCuenta(user, password);

                break;
            default:
                return;
        }

    }
}
