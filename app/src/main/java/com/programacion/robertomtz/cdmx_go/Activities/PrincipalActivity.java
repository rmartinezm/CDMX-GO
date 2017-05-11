package com.programacion.robertomtz.cdmx_go.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.programacion.robertomtz.cdmx_go.Adapters.EventoAdapter;
import com.programacion.robertomtz.cdmx_go.Classes.Negocio;
import com.programacion.robertomtz.cdmx_go.R;

import java.util.HashMap;
import java.util.LinkedList;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;

    // Views
    public static ViewPager mViewPager;
    private FloatingActionButton fab;
    // Auxiliares
    private Intent intent;
    private boolean crearCuenta;
    private boolean recibirNotificaciones;
    public static ListView listView;
    private final String USUARIOS = "usuarios";
    private static boolean flag;
    public static LinkedList<Negocio> negocios;
    private static int i;
    public static EventoAdapter adapter;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

    }

    public static Context getContext(){
        return context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case (R.id.action_settings):
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                intent = new Intent(getApplicationContext(), InicioActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        final Intent intent;

        switch (id){
            case R.id.fab:
                intent = new Intent(PrincipalActivity.this, MapsActivity.class);
                startActivity(intent);
                break;
            default:
                return;
        }

    }

    /** FRAGMENT DE LA PRIMERA PAGINA **/
    public static class EventosFragment extends Fragment {

        public EventosFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_eventos, container, false);

            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            listView = (ListView) getActivity().findViewById(R.id.fragment_eventos_lista);

            AsyncTaskAuxiliar ata = new AsyncTaskAuxiliar();
            ata.execute();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent(context, CardViewActivity.class);
                    intent.putExtra("negocio", negocios.get(position));
                    String posicion = position+"";
                    intent.putExtra("identificadorEvento", posicion);
                    startActivity(intent);
                }
            });

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

                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                adapter = new EventoAdapter(context, negocios);
                listView.setAdapter(adapter);
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
                        return;
                    }

                    cambiarPassword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditText passwordActual = (EditText) getActivity().findViewById(R.id.fragment_user_et_password_actual);
                            EditText passwordNuevo = (EditText) getActivity().findViewById(R.id.fragment_user_et_nuevo_password);
                            String passActual = passwordActual.getText().toString();
                            String passNuevo = passwordNuevo.getText().toString();
                            if (passActual.isEmpty() || passNuevo.isEmpty()) {
                                Toast.makeText(context, getResources().getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (passActual.length() < 6 || passNuevo.length() < 6){
                                Toast.makeText(context, getResources().getString(R.string.error_password_longitud), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (!passActual.equals(dataSnapshot.child("password").getValue())){
                                Toast.makeText(context, getResources().getString(R.string.error_password_actual_no_coinciden), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            DatabaseReference currentUserDB = usersReference.child(auth.getCurrentUser().getUid());
                            currentUserDB.child("password").setValue(passNuevo);
                            Toast.makeText(context, getResources().getString(R.string.exito_cambiar_password), Toast.LENGTH_SHORT).show();

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
                    return new UserFragment();
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
