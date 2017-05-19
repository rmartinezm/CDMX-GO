package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.programacion.robertomtz.cdmx_go.R;

public class UserNameActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etUserName;
    private Button btnCrear;

    private FirebaseDatabase database;
    private DatabaseReference usersReference;
    private String userName;
    private String id;
    private Intent intent;
    private final String USUARIOS = "usuarios";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        etUserName = (EditText) findViewById(R.id.user_name_et_user_name);
        btnCrear = (Button) findViewById(R.id.user_name_btn_crear);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            id = (String) bundle.get("id");

        database = FirebaseDatabase.getInstance();
        usersReference = database.getReference().child(USUARIOS);

        btnCrear.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        userName = etUserName.getText().toString().trim();

        if (userName.isEmpty()){
            Snackbar.make(view, "Indicanos un nombre de usuario", Snackbar.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usuario = usersReference.child(id);
        usuario.child("userName").setValue(userName);

        Snackbar.make(view, R.string.exito_crear_cuenta, Snackbar.LENGTH_SHORT).show();

        irPincipalActivity();
    }

    private void irPincipalActivity() {
        intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onResume();
    }
}
