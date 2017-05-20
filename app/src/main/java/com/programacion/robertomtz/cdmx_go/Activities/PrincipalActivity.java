package com.programacion.robertomtz.cdmx_go.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.programacion.robertomtz.cdmx_go.Adapters.EventoAdapter;
import com.programacion.robertomtz.cdmx_go.Adapters.EventosAdapterRecycler;
import com.programacion.robertomtz.cdmx_go.Classes.Negocio;
import com.programacion.robertomtz.cdmx_go.R;

import java.util.HashMap;
import java.util.LinkedList;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;

    // Views
    public static ViewPager mViewPager;
    private static FloatingActionButton fab;
    private static FloatingActionButton fabScroll;
    // Auxiliares
    private Intent intent;
    private View view;
    private boolean crearCuenta;
    private boolean recibirNotificaciones;

    public static ListView listView;
    public static EventoAdapter adapter;
    public static RecyclerView recyclerView;
    public static EventosAdapterRecycler recyclerAdapter;

    private final String USUARIOS = "usuarios";
    private static boolean flag;
    public static LinkedList<Negocio> negocios;
    private static int i;

    private String usuarioID;
    public static Context context;
    public static Activity miActivity;
    // Firebase
    private static FirebaseDatabase database;
    private DatabaseReference userReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        inicializaVariables();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void inicializaVariables() {
        context = this;
        miActivity = this;

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        usuarioID = auth.getCurrentUser().getUid();
        userReference = database.getReference().child(USUARIOS).child(usuarioID);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        view = findViewById(R.id.container);
        mViewPager = (ViewPager) view;
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        fabScroll = (FloatingActionButton) findViewById(R.id.fab_scroll_up);
        fabScroll.setOnClickListener(this);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Damos bienvenida al usuario
                Snackbar snackbar = Snackbar.make(view, "¡Bienvenido a CDMX-GO " + dataSnapshot.child("userName").getValue(String.class) + "!", Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                snackbar.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static Context getContext(){
        return context;
    }

    /** CERRAR SESION
      case (R.id.action_settings):
      FirebaseAuth.getInstance().signOut();
      LoginManager.getInstance().logOut();
      intent = new Intent(getApplicationContext(), InicioActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      return true;
      **/

    @Override
    public void onClick(View view) {
        int id = view.getId();
        final Intent intent;

        switch (id){
            case R.id.fab:
                intent = new Intent(PrincipalActivity.this, MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_scroll_up:
                 if (recyclerView != null && recyclerView.getLayoutManager() != null) {
                     LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                     layoutManager.scrollToPositionWithOffset(0, 0);
                     break;
                 }
        }

    }

    /** FRAGMENT DE LA PRIMERA PAGINA **/
    public static class EventosFragment extends Fragment implements View.OnClickListener {

        ImageView conciertos, cine, cultural, comida;
        boolean flagConciertos, flagCine, flagCultural, flagComida;

        public EventosFragment(){
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_eventos, container, false);

            conciertos = (ImageView) rootView.findViewById(R.id.filtro_concierto);
            cine = (ImageView) rootView.findViewById(R.id.filtro_cine);
            cultural = (ImageView) rootView.findViewById(R.id.filtro_museo);
            comida = (ImageView) rootView.findViewById(R.id.filtro_restaurante);

            flagComida = flagCine = flagConciertos = flagCultural = false;

            /**
            conciertos.setOnClickListener(this);
            cine.setOnClickListener(this);
            cultural.setOnClickListener(this);
            comida.setOnClickListener(this);
            **/

            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            recyclerView = (RecyclerView) getActivity().findViewById(R.id.fragment_eventos_lista);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);

            AsyncTaskAuxiliar ata = new AsyncTaskAuxiliar();
            ata.execute();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    LinearLayoutManager lm= (LinearLayoutManager) recyclerView.getLayoutManager();

                    if(lm.findFirstVisibleItemPosition()==0)
                        fabScroll.setVisibility(View.INVISIBLE);
                    else
                        fabScroll.setVisibility(View.VISIBLE);

                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager lm= (LinearLayoutManager) recyclerView.getLayoutManager();

                    if(lm.findFirstVisibleItemPosition()==0)
                        fabScroll.setVisibility(View.INVISIBLE);
                    else
                        fabScroll.setVisibility(View.VISIBLE);
                }
            });

        }

        @Override
        public void onClick(View view) {
            DatabaseReference reference;

            switch (view.getId()){

                case R.id.filtro_restaurante:

                    if (flagComida){
                        Toast.makeText(context, "Todos", Toast.LENGTH_SHORT).show();
                        reference = database.getReference().child("eventos");
                        i = 0;
                        negocios = new LinkedList<>();

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                flag = true;
                                while (flag) {
                                    if (dataSnapshot.hasChild(i + "")) {

                                        DataSnapshot miNegocio = dataSnapshot.child(i+"");

                                        Negocio negocio = new Negocio();
                                        negocio.setNombre(miNegocio.child("nombre_evento").getValue().toString());
                                        negocio.setUrlImagen(miNegocio.child("urlImagen").getValue().toString());
                                        negocio.setFecha(miNegocio.child("fecha").getValue().toString());
                                        negocio.setHorario(miNegocio.child("horario").getValue().toString());
                                        negocio.setLugar(miNegocio.child("lugar").getValue().toString());
                                        negocio.setDescripcion(miNegocio.child("descripcion").getValue().toString());

                                        if (miNegocio.hasChild("calificaciones")){
                                            DataSnapshot calificaciones = miNegocio.child("calificaciones");
                                            HashMap<String, Integer> hashMap = new HashMap<>();

                                            for (DataSnapshot calificacion: calificaciones.getChildren())
                                                hashMap.put(calificacion.getKey(), Integer.parseInt(calificacion.getValue().toString()));

                                            negocio.setCalificaciones(hashMap);
                                        }else
                                            negocio.setCalificaciones(new HashMap<String, Integer>());

                                        negocios.add(negocio);

                                        if (listView.getAdapter() != null)
                                            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

                                        i++;

                                    } else
                                        flag = false;
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                        adapter = new EventoAdapter(context, negocios);
                        listView.setAdapter(adapter);
                        comida.setBackgroundColor(Color.parseColor("#ffd1f1"));


                        flagComida = false;
                        flagConciertos = false;
                        flagCine = false;
                        flagCultural = false;


                        return;
                    }
                    Toast.makeText(context, "Comida", Toast.LENGTH_SHORT).show();

                    flagComida = true;
                    flagConciertos = false;
                    flagCine = false;
                    flagCultural = false;

                    conciertos.setBackgroundColor(Color.parseColor("#ffd1f1"));
                    cine.setBackgroundColor(Color.parseColor("#ffd1f1"));
                    comida.setBackgroundColor(Color.parseColor("#0097A7"));
                    cultural.setBackgroundColor(Color.parseColor("#ffd1f1"));

                    reference = database.getReference().child("eventos");
                    i = 0;
                    negocios = new LinkedList<>();

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            flag = true;
                            while (flag) {
                                if (dataSnapshot.hasChild(i + "")) {

                                    DataSnapshot miNegocio = dataSnapshot.child(i+"");

                                    Negocio negocio = new Negocio();
                                    negocio.setNombre(miNegocio.child("nombre_evento").getValue().toString());
                                    negocio.setUrlImagen(miNegocio.child("urlImagen").getValue().toString());
                                    negocio.setFecha(miNegocio.child("fecha").getValue().toString());
                                    negocio.setHorario(miNegocio.child("horario").getValue().toString());
                                    negocio.setLugar(miNegocio.child("lugar").getValue().toString());
                                    negocio.setDescripcion(miNegocio.child("descripcion").getValue().toString());

                                    if (miNegocio.hasChild("calificaciones")){
                                        DataSnapshot calificaciones = miNegocio.child("calificaciones");
                                        HashMap<String, Integer> hashMap = new HashMap<>();
                                        for (DataSnapshot calificacion: calificaciones.getChildren())
                                            hashMap.put(calificacion.getKey(), Integer.parseInt(calificacion.getValue().toString()));

                                        negocio.setCalificaciones(hashMap);
                                    }else
                                        negocio.setCalificaciones(new HashMap<String, Integer>());

                                    if ("Comida".equals(miNegocio.child("categoria").getValue(String.class)))
                                        negocios.add(negocio);

                                    if (listView.getAdapter() != null)
                                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

                                    i++;

                                } else
                                    flag = false;
                            }

                            adapter = new EventoAdapter(context, negocios);
                            listView.setAdapter(adapter);

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                    break;

                case R.id.filtro_cine:

                    if (flagCine){
                        reference = database.getReference().child("eventos");
                        i = 0;
                        negocios = new LinkedList<>();

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                flag = true;
                                while (flag) {
                                    if (dataSnapshot.hasChild(i + "")) {

                                        DataSnapshot miNegocio = dataSnapshot.child(i+"");

                                        Negocio negocio = new Negocio();
                                        negocio.setNombre(miNegocio.child("nombre_evento").getValue().toString());
                                        negocio.setUrlImagen(miNegocio.child("urlImagen").getValue().toString());
                                        negocio.setFecha(miNegocio.child("fecha").getValue().toString());
                                        negocio.setHorario(miNegocio.child("horario").getValue().toString());
                                        negocio.setLugar(miNegocio.child("lugar").getValue().toString());
                                        negocio.setDescripcion(miNegocio.child("descripcion").getValue().toString());

                                        if (miNegocio.hasChild("calificaciones")){
                                            DataSnapshot calificaciones = miNegocio.child("calificaciones");
                                            HashMap<String, Integer> hashMap = new HashMap<>();

                                            for (DataSnapshot calificacion: calificaciones.getChildren())
                                                hashMap.put(calificacion.getKey(), Integer.parseInt(calificacion.getValue().toString()));

                                            negocio.setCalificaciones(hashMap);
                                        }else
                                            negocio.setCalificaciones(new HashMap<String, Integer>());

                                        negocios.add(negocio);

                                        if (listView.getAdapter() != null)
                                            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

                                        i++;

                                    } else
                                        flag = false;
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                        adapter = new EventoAdapter(context, negocios);
                        listView.setAdapter(adapter);
                        cine.setBackgroundColor(Color.parseColor("#ffd1f1"));

                        flagComida = false;
                        flagConciertos = false;
                        flagCine = false;
                        flagCultural = false;

                        Toast.makeText(context, "Todos", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    Toast.makeText(context, "Cine", Toast.LENGTH_SHORT).show();

                    flagComida = false;
                    flagConciertos = false;
                    flagCine = true;
                    flagCultural = false;

                    reference = database.getReference().child("eventos");
                    i = 0;
                    negocios = new LinkedList<>();

                    conciertos.setBackgroundColor(Color.parseColor("#ffd1f1"));
                    cine.setBackgroundColor(Color.parseColor("#0097A7"));
                    comida.setBackgroundColor(Color.parseColor("#ffd1f1"));
                    cultural.setBackgroundColor(Color.parseColor("#ffd1f1"));

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            flag = true;
                            while (flag) {
                                if (dataSnapshot.hasChild(i + "")) {

                                    DataSnapshot miNegocio = dataSnapshot.child(i+"");

                                    Negocio negocio = new Negocio();
                                    negocio.setNombre(miNegocio.child("nombre_evento").getValue().toString());
                                    negocio.setUrlImagen(miNegocio.child("urlImagen").getValue().toString());
                                    negocio.setFecha(miNegocio.child("fecha").getValue().toString());
                                    negocio.setHorario(miNegocio.child("horario").getValue().toString());
                                    negocio.setLugar(miNegocio.child("lugar").getValue().toString());
                                    negocio.setDescripcion(miNegocio.child("descripcion").getValue().toString());

                                    if (miNegocio.hasChild("calificaciones")){
                                        DataSnapshot calificaciones = miNegocio.child("calificaciones");
                                        HashMap<String, Integer> hashMap = new HashMap<>();
                                        for (DataSnapshot calificacion: calificaciones.getChildren())
                                            hashMap.put(calificacion.getKey(), Integer.parseInt(calificacion.getValue().toString()));

                                        negocio.setCalificaciones(hashMap);
                                    }else
                                        negocio.setCalificaciones(new HashMap<String, Integer>());

                                    if ("Cine".equals(miNegocio.child("categoria").getValue(String.class)))
                                        negocios.add(negocio);

                                    if (listView.getAdapter() != null)
                                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

                                    i++;

                                } else
                                    flag = false;
                            }

                            adapter = new EventoAdapter(context, negocios);
                            listView.setAdapter(adapter);

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                    break;

                case R.id.filtro_museo:

                    if (flagCultural){
                        reference = database.getReference().child("eventos");
                        i = 0;
                        negocios = new LinkedList<>();

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                flag = true;
                                while (flag) {
                                    if (dataSnapshot.hasChild(i + "")) {

                                        DataSnapshot miNegocio = dataSnapshot.child(i+"");

                                        Negocio negocio = new Negocio();
                                        negocio.setNombre(miNegocio.child("nombre_evento").getValue().toString());
                                        negocio.setUrlImagen(miNegocio.child("urlImagen").getValue().toString());
                                        negocio.setFecha(miNegocio.child("fecha").getValue().toString());
                                        negocio.setHorario(miNegocio.child("horario").getValue().toString());
                                        negocio.setLugar(miNegocio.child("lugar").getValue().toString());
                                        negocio.setDescripcion(miNegocio.child("descripcion").getValue().toString());

                                        if (miNegocio.hasChild("calificaciones")){
                                            DataSnapshot calificaciones = miNegocio.child("calificaciones");
                                            HashMap<String, Integer> hashMap = new HashMap<>();

                                            for (DataSnapshot calificacion: calificaciones.getChildren())
                                                hashMap.put(calificacion.getKey(), Integer.parseInt(calificacion.getValue().toString()));

                                            negocio.setCalificaciones(hashMap);
                                        }else
                                            negocio.setCalificaciones(new HashMap<String, Integer>());

                                        negocios.add(negocio);

                                        if (listView.getAdapter() != null)
                                            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

                                        i++;

                                    } else
                                        flag = false;
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                        adapter = new EventoAdapter(context, negocios);
                        listView.setAdapter(adapter);
                        cultural.setBackgroundColor(Color.parseColor("#ffd1f1"));

                        flagComida = false;
                        flagConciertos = false;
                        flagCine = false;
                        flagCultural = false;

                        Toast.makeText(context, "Todos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(context, "Cultural", Toast.LENGTH_SHORT).show();
                    flagComida = false;
                    flagConciertos = false;
                    flagCine = false;
                    flagCultural = true;

                    reference = database.getReference().child("eventos");
                    i = 0;
                    negocios = new LinkedList<>();

                    conciertos.setBackgroundColor(Color.parseColor("#ffd1f1"));
                    cine.setBackgroundColor(Color.parseColor("#ffd1f1"));
                    comida.setBackgroundColor(Color.parseColor("#ffd1f1"));
                    cultural.setBackgroundColor(Color.parseColor("#0097A7"));

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            flag = true;
                            while (flag) {
                                if (dataSnapshot.hasChild(i + "")) {

                                    DataSnapshot miNegocio = dataSnapshot.child(i+"");

                                    Negocio negocio = new Negocio();
                                    negocio.setNombre(miNegocio.child("nombre_evento").getValue().toString());
                                    negocio.setUrlImagen(miNegocio.child("urlImagen").getValue().toString());
                                    negocio.setFecha(miNegocio.child("fecha").getValue().toString());
                                    negocio.setHorario(miNegocio.child("horario").getValue().toString());
                                    negocio.setLugar(miNegocio.child("lugar").getValue().toString());
                                    negocio.setDescripcion(miNegocio.child("descripcion").getValue().toString());

                                    if (miNegocio.hasChild("calificaciones")){
                                        DataSnapshot calificaciones = miNegocio.child("calificaciones");
                                        HashMap<String, Integer> hashMap = new HashMap<>();
                                        for (DataSnapshot calificacion: calificaciones.getChildren())
                                            hashMap.put(calificacion.getKey(), Integer.parseInt(calificacion.getValue().toString()));

                                        negocio.setCalificaciones(hashMap);
                                    }else
                                        negocio.setCalificaciones(new HashMap<String, Integer>());

                                    if ("Cultura".equals(miNegocio.child("categoria").getValue(String.class)))
                                        negocios.add(negocio);

                                    if (listView.getAdapter() != null)
                                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

                                    i++;

                                } else
                                    flag = false;
                            }

                            adapter = new EventoAdapter(context, negocios);
                            listView.setAdapter(adapter);

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                    break;

                case R.id.filtro_concierto:

                    if (flagConciertos){
                        reference = database.getReference().child("eventos");
                        i = 0;
                        negocios = new LinkedList<>();

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                flag = true;
                                while (flag) {
                                    if (dataSnapshot.hasChild(i + "")) {

                                        DataSnapshot miNegocio = dataSnapshot.child(i+"");

                                        Negocio negocio = new Negocio();
                                        negocio.setNombre(miNegocio.child("nombre_evento").getValue().toString());
                                        negocio.setUrlImagen(miNegocio.child("urlImagen").getValue().toString());
                                        negocio.setFecha(miNegocio.child("fecha").getValue().toString());
                                        negocio.setHorario(miNegocio.child("horario").getValue().toString());
                                        negocio.setLugar(miNegocio.child("lugar").getValue().toString());
                                        negocio.setDescripcion(miNegocio.child("descripcion").getValue().toString());

                                        if (miNegocio.hasChild("calificaciones")){
                                            DataSnapshot calificaciones = miNegocio.child("calificaciones");
                                            HashMap<String, Integer> hashMap = new HashMap<>();

                                            for (DataSnapshot calificacion: calificaciones.getChildren())
                                                hashMap.put(calificacion.getKey(), Integer.parseInt(calificacion.getValue().toString()));

                                            negocio.setCalificaciones(hashMap);
                                        }else
                                            negocio.setCalificaciones(new HashMap<String, Integer>());

                                        negocios.add(negocio);

                                        if (listView.getAdapter() != null)
                                            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

                                        i++;

                                    } else
                                        flag = false;
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                        adapter = new EventoAdapter(context, negocios);
                        listView.setAdapter(adapter);
                        conciertos.setBackgroundColor(Color.parseColor("#ffd1f1"));

                        flagComida = false;
                        flagConciertos = false;
                        flagCine = false;
                        flagCultural = false;

                        Toast.makeText(context, "Todos", Toast.LENGTH_SHORT).show();

                        return;
                    }

                    Toast.makeText(context, "Conciertos", Toast.LENGTH_SHORT).show();
                    flagComida = false;
                    flagConciertos = true;
                    flagCine = false;
                    flagCultural = false;

                    conciertos.setBackgroundColor(Color.parseColor("#0097A7"));
                    cine.setBackgroundColor(Color.parseColor("#ffd1f1"));
                    comida.setBackgroundColor(Color.parseColor("#ffd1f1"));
                    cultural.setBackgroundColor(Color.parseColor("#ffd1f1"));

                    reference = database.getReference().child("eventos");
                    i = 0;
                    negocios = new LinkedList<>();

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            flag = true;
                            while (flag) {
                                if (dataSnapshot.hasChild(i + "")) {

                                    DataSnapshot miNegocio = dataSnapshot.child(i+"");

                                    Negocio negocio = new Negocio();
                                    negocio.setNombre(miNegocio.child("nombre_evento").getValue().toString());
                                    negocio.setUrlImagen(miNegocio.child("urlImagen").getValue().toString());
                                    negocio.setFecha(miNegocio.child("fecha").getValue().toString());
                                    negocio.setHorario(miNegocio.child("horario").getValue().toString());
                                    negocio.setLugar(miNegocio.child("lugar").getValue().toString());
                                    negocio.setDescripcion(miNegocio.child("descripcion").getValue().toString());

                                    if (miNegocio.hasChild("calificaciones")){
                                        DataSnapshot calificaciones = miNegocio.child("calificaciones");
                                        HashMap<String, Integer> hashMap = new HashMap<>();
                                        for (DataSnapshot calificacion: calificaciones.getChildren())
                                            hashMap.put(calificacion.getKey(), Integer.parseInt(calificacion.getValue().toString()));

                                        negocio.setCalificaciones(hashMap);
                                    }else
                                        negocio.setCalificaciones(new HashMap<String, Integer>());

                                    if ("Concierto".equals(miNegocio.child("categoria").getValue(String.class)))
                                        negocios.add(negocio);

                                    if (listView.getAdapter() != null)
                                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();

                                    i++;

                                } else
                                    flag = false;
                            }

                            adapter = new EventoAdapter(context, negocios);
                            listView.setAdapter(adapter);

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                    break;

                default:
                    Toast.makeText(context, "Pronto será activada esta opción", Toast.LENGTH_SHORT).show();
            }

        }

        private static class AsyncTaskAuxiliar extends AsyncTask<Void, Integer, Boolean>{

            @Override
            protected Boolean doInBackground(Void... voids) {
                DatabaseReference reference = database.getReference().child("eventos");
                i = 0;
                negocios = new LinkedList<>();

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        flag = true;
                        while (flag) {
                            if (dataSnapshot.hasChild(i + "")) {

                                DataSnapshot miNegocio = dataSnapshot.child(i+"");

                                Negocio negocio = new Negocio();
                                negocio.setNombre(miNegocio.child("nombre_evento").getValue().toString());
                                negocio.setUrlImagen(miNegocio.child("urlImagen").getValue().toString());
                                negocio.setFecha(miNegocio.child("fecha").getValue().toString());
                                negocio.setHorario(miNegocio.child("horario").getValue().toString());
                                negocio.setLugar(miNegocio.child("lugar").getValue().toString());
                                negocio.setDescripcion(miNegocio.child("descripcion").getValue().toString());

                                if (miNegocio.hasChild("calificaciones")){
                                    DataSnapshot calificaciones = miNegocio.child("calificaciones");
                                    HashMap<String, Integer> hashMap = new HashMap<>();

                                    for (DataSnapshot calificacion: calificaciones.getChildren())
                                        hashMap.put(calificacion.getKey(), Integer.parseInt(calificacion.getValue().toString()));

                                    negocio.setCalificaciones(hashMap);
                                }else
                                    negocio.setCalificaciones(new HashMap<String, Integer>());

                                negocios.add(negocio);

                                if (recyclerView.getAdapter() != null)
                                    ((EventosAdapterRecycler) recyclerView.getAdapter()).updateList(negocios);

                                i++;

                            } else
                                flag = false;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                recyclerAdapter = new EventosAdapterRecycler(negocios);
                recyclerView.setAdapter(recyclerAdapter);
            }
        }
    }

    /** FRAGMENT DE LA SEGUNDA PAGINA **/
    public static class UserFragment extends Fragment {

        final String USUARIOS = "usuarios";
        FirebaseAuth auth;
        FirebaseDatabase database;
        DatabaseReference usersReference;
        DatabaseReference userReference;
        Context context;
        private String usuarioUserName;
        private String usuarioEmail;
        private String usuarioFoto;

        public UserFragment() {
            usuarioUserName = "";
            usuarioEmail = "";
            usuarioFoto = "";
            this.context = PrincipalActivity.getContext();
            auth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            usersReference = database.getReference().child(USUARIOS);
            userReference = usersReference.child(auth.getCurrentUser().getUid());
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_user, container, false);

            final TextView tvCorreo = (TextView) rootView.findViewById(R.id.fragment_user_tv_correo);
            final TextView tvUserName = (TextView) rootView.findViewById(R.id.fragment_user_tv_user_name);
            final ImageView ivPhoto = (ImageView) rootView.findViewById(R.id.fragment_user_iv_image);
            final TextView tvCoins = (TextView) rootView.findViewById(R.id.fragment_user_tv_coins);

            final EditText etNuevoPassword = (EditText) rootView.findViewById(R.id.fragment_user_et_nuevo_password);
            final EditText etActualPassword = (EditText) rootView.findViewById(R.id.fragment_user_et_password_actual);

            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Pronto podrás actualizar tu foto de perfil!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
            final Button cambiarPassword = (Button) rootView.findViewById(R.id.fragment_user_btn_cambiar_password);

            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("userName"))
                        usuarioUserName = dataSnapshot.child("userName").getValue().toString();
                    if (dataSnapshot.hasChild("email"))
                        usuarioEmail = dataSnapshot.child("email").getValue().toString();
                    if (dataSnapshot.hasChild("photo"))
                        usuarioFoto = dataSnapshot.child("photo").getValue().toString().trim();
                    if (dataSnapshot.hasChild("coins")){
                        String misMonedas = "Mis Monedas: " + dataSnapshot.child("coins").getValue().toString();
                        tvCoins.setText(misMonedas);
                    }

                    tvUserName.setText(usuarioUserName);

                    tvCorreo.setText(usuarioEmail);
                    if (!usuarioFoto.isEmpty())
                        Glide.with(context)
                                .load(usuarioFoto)
                                .crossFade()
                                .into(ivPhoto);

                    if (usuarioEmail.isEmpty()){
                        etNuevoPassword.setVisibility(View.INVISIBLE);
                        etActualPassword.setVisibility(View.INVISIBLE);
                        cambiarPassword.setVisibility(View.INVISIBLE);
                        return;
                    }

                    cambiarPassword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditText passwordActual = (EditText) getActivity().findViewById(R.id.fragment_user_et_password_actual);
                            EditText passwordNuevo = (EditText) getActivity().findViewById(R.id.fragment_user_et_nuevo_password);

                            Snackbar.make(view, "Opción aún no disponible", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                            passwordActual.setText("");
                            passwordNuevo.setText("");
                            passwordNuevo.requestFocus();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return rootView;
        }
    }

    /** EL FRAGMENT DE LA TERCERA PAGINA ESTA EN FRAGMENTRECOMPENSAS **/

    /** ADAPTADOR DE FRAGMENTS **/
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new EventosFragment();
                case 1:
                    return new UserFragment();
                case 2:
                    return new FragmentRecompensas();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "EVENTOS!";
                case 1:
                    return "PERFIL!";
                case 2:
                    return "BONUS!";
            }
            return null;
        }
    }

}
