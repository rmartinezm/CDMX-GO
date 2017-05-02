package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Button;
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

import java.util.LinkedList;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;

    // Views
    private ViewPager mViewPager;
    private FloatingActionButton fab;
    // Auxiliares
    private Intent intent;
    private boolean crearCuenta;
    private boolean recibirNotificaciones;
    private final String USUARIOS = "usuarios";
    private static boolean flag;
    private static LinkedList<Negocio> negocios;
    private int i;

    private String usuarioID;
    private static Context context;

    // Firebase
    private FirebaseDatabase database;
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

    private void inicializaVariables() {
        context = this;

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

        llenaListaNegocios();

    }

    private void llenaListaNegocios(){
        DatabaseReference reference = database.getReference().child("eventos");
        i = 0;
        negocios = new LinkedList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                flag = true;
                while (flag) {
                    if (dataSnapshot.hasChild(i + "")) {
                        Negocio negocio = new Negocio();
                        negocio.setNombre(dataSnapshot.child(i + "").child("nombre_evento").getValue().toString());
                        negocio.setUrlImagen(dataSnapshot.child(i + "").child("urlImagen").getValue().toString());
                        negocio.setFecha(dataSnapshot.child(i + "").child("fecha").getValue().toString());
                        negocio.setHorario(dataSnapshot.child(i + "").child("horario").getValue().toString());
                        negocio.setLugar(dataSnapshot.child(i + "").child("lugar").getValue().toString());
                        negocio.setDescripcion(dataSnapshot.child(i+"").child("descripcion").getValue().toString());

                        negocios.add(negocio);

                        i++;

                    } else
                        flag = false;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

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

    @Override
    protected void onStart() {
        super.onStart();
        if (negocios == null)
            llenaListaNegocios();
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    /** FRAGMENT DE LA PRIMERA PAGINA **/
    public static class EventosFragment extends Fragment {

        public EventosFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_eventos, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.fragment_eventos_lista);

            EventoAdapter adapter = new EventoAdapter(rootView.getContext(), negocios);
            listView.setAdapter(adapter);

            return rootView;
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
        private String usuarioPassword;

        public UserFragment() {
            usuarioUserName = "";
            usuarioEmail = "";
            usuarioFoto = "";
            usuarioPassword = "";
            this.context = PrincipalActivity.getContext();
            auth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            usersReference = database.getReference().child(USUARIOS);
            userReference = usersReference.child(auth.getCurrentUser().getUid());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_user, container, false);

            final TextView tvCorreo = (TextView) rootView.findViewById(R.id.fragment_user_tv_correo);
            final TextView tvUserName = (TextView) rootView.findViewById(R.id.fragment_user_tv_user_name);
            final ImageView ivPhoto = (ImageView) rootView.findViewById(R.id.fragment_user_iv_image);
            final Button cambiarPassword = (Button) rootView.findViewById(R.id.fragment_user_btn_cambiar_password);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("userName"))
                        usuarioUserName = dataSnapshot.child("userName").getValue().toString();
                    if (dataSnapshot.hasChild("email"))
                        usuarioEmail = dataSnapshot.child("email").getValue().toString();
                    if (dataSnapshot.hasChild("photo"))
                        usuarioFoto = dataSnapshot.child("photo").getValue().toString();
                    if (dataSnapshot.hasChild("password"))
                        usuarioPassword = dataSnapshot.child("password").getValue().toString();

                    if (usuarioUserName.isEmpty())
                        return;

                    tvCorreo.setText(usuarioEmail);
                    tvUserName.setText(usuarioUserName);
                    if (!usuarioFoto.isEmpty())
                        Glide.with(context)
                                .load(usuarioFoto)
                                .crossFade()
                                .into(ivPhoto);

                    cambiarPassword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context, "No implementado aun", Toast.LENGTH_SHORT).show();
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
                    return new UserFragment();
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
