package com.programacion.robertomtz.cdmx_go.Activities;

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
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.programacion.robertomtz.cdmx_go.Adapters.HomeAdapter;
import com.programacion.robertomtz.cdmx_go.Classes.Negocio;
import com.programacion.robertomtz.cdmx_go.Classes.Usuario;
import com.programacion.robertomtz.cdmx_go.R;

import java.util.ArrayList;

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

    private String usuarioUserName;
    private String usuarioEmail;
    private String usuarioFoto;
    private String usuarioPassword;

    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inicializaVariables();

        if (crearCuenta)
            creaUsuario();
    }

    private void inicializaVariables() {

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            crearCuenta = (boolean) bundle.get("crear cuenta");
            recibirNotificaciones = (boolean) bundle.get("recibir notificaciones");
            usuarioPassword = (String) bundle.get("password");
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);


    }

    private void creaUsuario(){
        FirebaseUser user = auth.getCurrentUser();
        usuarioEmail = (user.getEmail() != null)? user.getEmail(): "";
        // Foto temporal
        usuarioFoto = "https://avatars3.githubusercontent.com/u/1452563?v=3&s=400";
        // Password esta guardado en la clase
        // Coins los inicializaremos en 0

        usuarioUserName = (user.getDisplayName() != null)? user.getDisplayName() : "";

        if (yaExisteUserName(usuarioUserName)){
            intent = new Intent(this, UserNameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        Usuario usuario = new Usuario(usuarioUserName, usuarioPassword, usuarioEmail, usuarioFoto, 0);

        reference.child(USUARIOS).child(usuarioUserName).setValue(usuario);

    }

    private boolean yaExisteUserName(String userName){
        return false;
    }

    @Override
    public void onClick(View view){
        int id = view.getId();

        switch (id){
            case R.id.fab:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;
            default:
                return;
        }

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

    /** FRAGMENT DE LA PRIMERA PAGINA **/
    public static class HomeFragment extends Fragment {

        public HomeFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.home_listview);

            HomeAdapter adapter = new HomeAdapter(rootView.getContext(), new ArrayList<Negocio>());
            listView.setAdapter(adapter);

            return rootView;
        }

    }

    public static class UserFragment extends Fragment {

        public UserFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_user, container, false);

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
                    return new HomeFragment();
                case 1:
                    return new UserFragment();
                case 2:
                    return new HomeFragment();
                default:
                    return new HomeFragment();
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
