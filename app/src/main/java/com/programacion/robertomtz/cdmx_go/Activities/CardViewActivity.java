package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.programacion.robertomtz.cdmx_go.Adapters.EventosAdapterRecycler;
import com.programacion.robertomtz.cdmx_go.Classes.Negocio;
import com.programacion.robertomtz.cdmx_go.R;

import java.util.HashMap;

public class CardViewActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imagen, mapa, marked;
    private TextView nombre, descripcion, textoCalificacion, horario, fecha;
    private Negocio negocio;
    private HashMap<String, Integer> calificaciones;
    private DatabaseReference calificacionesEvento;
    private String identificadorEvento;
    private FirebaseUser user;

    // Estrellas
    private ImageView estrella_1, estrella_2, estrella_3, estrella_4, estrella_5;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        negocio = (Negocio) bundle.get("negocio");
        identificadorEvento = (String) bundle.get("identificadorEvento");
        calificaciones = negocio.getCalificaciones();

        nombre = (TextView) findViewById(R.id.cardview_nombre);
        descripcion = (TextView) findViewById(R.id.cardview_descripcion);
        imagen = (ImageView) findViewById(R.id.cardview_imagen);
        mapa = (ImageView) findViewById(R.id.cardview_como_llegar);
        marked = (ImageView) findViewById(R.id.cardview_marked);
        textoCalificacion = (TextView) findViewById(R.id.cardview_tv_texto_calificacion);
        horario = (TextView) findViewById(R.id.cardview_tv_horario);
        fecha = (TextView) findViewById(R.id.cardview_tv_fecha);
        TextView textoGuardarEvento = (TextView) findViewById(R.id.txt_1);
        textoGuardarEvento.setOnClickListener(this);


        estrella_1 = (ImageView) findViewById(R.id.estrella_uno);
        estrella_2 = (ImageView) findViewById(R.id.estrella_dos);
        estrella_3 = (ImageView) findViewById(R.id.estrella_tres);
        estrella_4 = (ImageView) findViewById(R.id.estrella_cuatro);
        estrella_5 = (ImageView) findViewById(R.id.estrella_cinco);


        String cuando = "¿Cuándo?\n"+ negocio.getFecha();
        String horarioString = "Horario: " + negocio.getHorario();
        horario.setText(horarioString);
        fecha.setText(cuando);
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

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (calificaciones.containsKey(user.getUid().toString()))
            colocaMiCalificacion(calificaciones.get(user.getUid()));
        else {
            colocaCalificaciones();
            Toast.makeText(this, getResources().getString(R.string.puedes_subir_calificacion), Toast.LENGTH_LONG).show();
        }
        marked.setOnClickListener(this);
        mapa.setOnClickListener(this);
        estrella_1.setOnClickListener(this);
        estrella_2.setOnClickListener(this);
        estrella_3.setOnClickListener(this);
        estrella_4.setOnClickListener(this);
        estrella_5.setOnClickListener(this);

    }

    private void colocaMiCalificacion(int calificacion){

        // Siempre tenemos al menos la calificacion mas baja
         Glide.with(this).load(R.drawable.mi_estrella).into(estrella_1);
        if (calificacion >= 2)
            Glide.with(this).load(R.drawable.mi_estrella).into(estrella_2);
        if (calificacion >= 3)
            Glide.with(this).load(R.drawable.mi_estrella).into(estrella_3);
        if (calificacion >= 4)
            Glide.with(this).load(R.drawable.mi_estrella).into(estrella_4);
        if (calificacion == 5)
            Glide.with(this).load(R.drawable.mi_estrella).into(estrella_5);
        textoCalificacion.setText("Mi Calificación");
    }

    private void colocaCalificaciones() {

        int sumaCalificaciones = 0;

        for (Integer entero: calificaciones.values())
            sumaCalificaciones += entero;

        double promedio = sumaCalificaciones==0? 0: (double)sumaCalificaciones/calificaciones.size();

        // Vemos cuantas estrellas encendemos

        if (promedio == 0)
            return;
        if (promedio >= 1)
            Glide.with(this).load(R.drawable.estrella_rellena).into(estrella_1);
        if (promedio >= 1.5)
            Glide.with(this).load(R.drawable.estrella_mitad).into(estrella_2);
        if (promedio >= 2)
            Glide.with(this).load(R.drawable.estrella_rellena).into(estrella_2);
        if (promedio >= 2.5)
            Glide.with(this).load(R.drawable.estrella_mitad).into(estrella_3);
        if (promedio >= 3)
            Glide.with(this).load(R.drawable.estrella_rellena).into(estrella_3);
        if (promedio >= 3.5)
            Glide.with(this).load(R.drawable.estrella_mitad).into(estrella_4);
        if (promedio >= 4)
            Glide.with(this).load(R.drawable.estrella_rellena).into(estrella_4);
        if (promedio >= 4.5)
            Glide.with(this).load(R.drawable.estrella_mitad).into(estrella_5);
        if (promedio >= 5)
            Glide.with(this).load(R.drawable.estrella_rellena).into(estrella_5);

    }

    @Override
    public void onClick(View view) {

        TextView textoGuardarEvento = (TextView) findViewById(R.id.cardview_tv_guarda_evento);

        if (view.getId() == R.id.cardview_marked){
            if (flag){
                Glide.with(this)
                        .load(R.drawable.sin_marcador)
                        .into(marked);
                flag = false;
                textoGuardarEvento.setText("Guardar Evento");

            }else{
                Glide.with(this)
                        .load(R.drawable.marcado)
                        .into(marked);
                Snackbar.make(view, "Este evento se guardará para poder verlo sin conexión a internet en próximas actualizaciones", Snackbar.LENGTH_LONG).show();
                flag = true;
                textoGuardarEvento.setText("Quitar Evento");
            }
        }

        if (view.getId() == R.id.cardview_como_llegar || view.getId() == R.id.txt_1){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("negocio", negocio);
            startActivity(intent);
        }

        calificacionesEvento = FirebaseDatabase.getInstance().getReference().child("eventos").child(identificadorEvento).child("calificaciones");
        Negocio negocioAEditar = PrincipalActivity.negocios.get(Integer.parseInt(identificadorEvento));

        Negocio negocio = negocioAEditar.copia();
        HashMap<String, Integer> hashMap = new HashMap<>();

        switch (view.getId()){

            case R.id.estrella_uno:
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_1);
                Glide.with(this).load(R.drawable.estrella_borde).into(estrella_2);
                Glide.with(this).load(R.drawable.estrella_borde).into(estrella_3);
                Glide.with(this).load(R.drawable.estrella_borde).into(estrella_4);
                Glide.with(this).load(R.drawable.estrella_borde).into(estrella_5);
                calificacionesEvento.child(user.getUid()).setValue(1);
                textoCalificacion.setText("Mi Calificación");
                hashMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), 1);
                negocio.setCalificaciones(hashMap);
                PrincipalActivity.negocios.set(Integer.parseInt(identificadorEvento), negocio);
                ((EventosAdapterRecycler) PrincipalActivity.recyclerView.getAdapter()).updateList(PrincipalActivity.negocios);
                break;
            case R.id.estrella_dos:
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_1);
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_2);
                Glide.with(this).load(R.drawable.estrella_borde).into(estrella_3);
                Glide.with(this).load(R.drawable.estrella_borde).into(estrella_4);
                Glide.with(this).load(R.drawable.estrella_borde).into(estrella_5);
                calificacionesEvento.child(user.getUid()).setValue(2);
                textoCalificacion.setText("Mi Calificación");
                hashMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), 2);
                negocio.setCalificaciones(hashMap);
                PrincipalActivity.negocios.set(Integer.parseInt(identificadorEvento), negocio);
                ((EventosAdapterRecycler) PrincipalActivity.recyclerView.getAdapter()).updateList(PrincipalActivity.negocios);
                break;
            case R.id.estrella_tres:
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_1);
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_2);
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_3);
                Glide.with(this).load(R.drawable.estrella_borde).into(estrella_4);
                Glide.with(this).load(R.drawable.estrella_borde).into(estrella_5);
                calificacionesEvento.child(user.getUid()).setValue(3);
                textoCalificacion.setText("Mi Calificación");
                hashMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), 3);
                negocio.setCalificaciones(hashMap);
                PrincipalActivity.negocios.set(Integer.parseInt(identificadorEvento), negocio);
                ((EventosAdapterRecycler) PrincipalActivity.recyclerView.getAdapter()).updateList(PrincipalActivity.negocios);
                break;
            case R.id.estrella_cuatro:
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_1);
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_2);
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_3);
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_4);
                Glide.with(this).load(R.drawable.estrella_borde).into(estrella_5);
                calificacionesEvento.child(user.getUid()).setValue(4);
                textoCalificacion.setText("Mi Calificación");
                hashMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), 4);
                negocio.setCalificaciones(hashMap);
                PrincipalActivity.negocios.set(Integer.parseInt(identificadorEvento), negocio);
                ((EventosAdapterRecycler) PrincipalActivity.recyclerView.getAdapter()).updateList(PrincipalActivity.negocios);
                break;
            case R.id.estrella_cinco:
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_1);
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_2);
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_3);
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_4);
                Glide.with(this).load(R.drawable.mi_estrella).into(estrella_5);
                calificacionesEvento.child(user.getUid()).setValue(5);
                textoCalificacion.setText("Mi Calificación");
                hashMap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), 5);
                negocio.setCalificaciones(hashMap);
                PrincipalActivity.negocios.set(Integer.parseInt(identificadorEvento), negocio);
                ((EventosAdapterRecycler) PrincipalActivity.recyclerView.getAdapter()).updateList(PrincipalActivity.negocios);
                break;
            default:
                return;
        }
    }
}
