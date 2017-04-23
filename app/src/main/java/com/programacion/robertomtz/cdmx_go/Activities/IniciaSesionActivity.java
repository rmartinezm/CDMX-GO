package com.programacion.robertomtz.cdmx_go.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.programacion.robertomtz.cdmx_go.R;

public class IniciaSesionActivity extends AppCompatActivity implements View.OnClickListener {

    // Views
    private EditText usuario;
    private EditText password;
    private Button aceptar;
    private TextView olvidePass;
    private ProgressDialog progressDialog;
    // FB Button
    private CallbackManager callbackManager;
    private LoginButton loginButtonFB;
    // Firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;
    // Auxiliares
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicia_sesion);

        inicializaVariables();


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id){

            case R.id.inicia_sesion_btn_aceptar:

                String user = usuario.getText().toString().trim();
                String pass = password.getText().toString();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(this, getResources().getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.length() < 6) {
                    Toast.makeText(this, getResources().getString(R.string.error_password_longitud), Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.setMessage(getResources().getString(R.string.iniciando_sesion));
                progressDialog.show();

                verificaCuenta(user, pass);
                return;

            case R.id.inicia_sesion_tv_olvide_pass:
                break;

        }
    }

    /** Suponemos que user y pass son ambos distintos tanto de null como de cadena vacia **/
    private void verificaCuenta(String user, String pass){
        auth.signInWithEmailAndPassword(user, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful())
                            irPincipalActivity();
                        else
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_iniciar_sesion), Toast.LENGTH_SHORT);
                    }
                });
    }

    private void inicializaVariables() {
        // Views
        usuario = (EditText) findViewById(R.id.inicia_sesion_et_usuario);
        usuario.requestFocus();
        password = (EditText) findViewById(R.id.inicia_sesion_et_password);
        aceptar = (Button) findViewById(R.id.inicia_sesion_btn_aceptar);
        aceptar.setOnClickListener(this);
        olvidePass = (TextView) findViewById(R.id.inicia_sesion_tv_olvide_pass);
        olvidePass.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        // Facebook login
        callbackManager = CallbackManager.Factory.create();
        loginButtonFB = (LoginButton) findViewById(R.id.inicia_sesion_btn_fb);
        loginButtonFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                progressDialog.setMessage(getResources().getString(R.string.iniciando_sesion));
                progressDialog.show();

                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Toast.makeText(IniciaSesionActivity.this, getResources().getString(R.string.error_iniciar_sesion), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(IniciaSesionActivity.this, getResources().getString(R.string.error_iniciar_sesion), Toast.LENGTH_SHORT).show();
            }
        });

        // Firebase
        auth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null)
                    irPincipalActivity();
            }
        };

    }

    /** Una vez que ingresamos a fecebook tenemos que ingresar a Firebase **/
    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful())
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_iniciar_sesion), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** Quitamos la opcion de ir al anterior activity con el boton back **/
    private void irPincipalActivity() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(listener);
    }

    /** Avisamos al CallbackManager **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
