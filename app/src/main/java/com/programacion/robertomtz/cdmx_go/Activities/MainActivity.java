package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.programacion.robertomtz.cdmx_go.Activities.InicioActivity;
import com.programacion.robertomtz.cdmx_go.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, InicioActivity.class);
        startActivity(intent);
    }
}
