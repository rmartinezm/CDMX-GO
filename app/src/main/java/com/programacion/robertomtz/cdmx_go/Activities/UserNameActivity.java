package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.programacion.robertomtz.cdmx_go.R;

public class UserNameActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etUserName;
    private Button btnCrear;

    public static String nombreDeUsuarioValido;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String userName;
    private final String USUARIOS = "usuarios";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        etUserName = (EditText) findViewById(R.id.user_name_et_user_name);
        btnCrear = (Button) findViewById(R.id.user_name_btn_crear);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        btnCrear.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        userName = etUserName.getText().toString().trim();

        if (userName.isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT).show();
            return;
        }

        UserNameActivity.nombreDeUsuarioValido = userName;
        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
