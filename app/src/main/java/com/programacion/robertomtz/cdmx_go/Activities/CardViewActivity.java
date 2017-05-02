package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.programacion.robertomtz.cdmx_go.Classes.Negocio;
import com.programacion.robertomtz.cdmx_go.R;

public class CardViewActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imagen, mapa, marked;
    private TextView nombre, descripcion;
    private Negocio negocio;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        negocio = (Negocio) bundle.get("negocio");

        nombre = (TextView) findViewById(R.id.cardview_nombre);
        descripcion = (TextView) findViewById(R.id.cardview_descripcion);
        imagen = (ImageView) findViewById(R.id.cardview_imagen);
        mapa = (ImageView) findViewById(R.id.cardview_como_llegar);
        marked = (ImageView) findViewById(R.id.cardview_marked);

        nombre.setText(negocio.getNombre());
        descripcion.setText(negocio.getDescripcion());
        Glide.with(this)
                .load(negocio.getUrlImagen())
                .error(R.drawable.image_not_found)
                .into(imagen);

        Glide.with(this)
                .load(R.drawable.grey_marked)
                .into(mapa);

        Glide.with(this)
                .load(R.drawable.sin_marcador)
                .into(marked);

        marked.setOnClickListener(this);
        mapa.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cardview_marked){
            if (flag){
                Glide.with(this)
                        .load(R.drawable.sin_marcador)
                        .into(marked);
                flag = false;
            }else{
                Glide.with(this)
                        .load(R.drawable.marcado)
                        .into(marked);
                flag = true;
            }
        }
        if (view.getId() == R.id.cardview_como_llegar){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("negocio", negocio);
            startActivity(intent);
        }
    }
}
