package com.programacion.robertomtz.cdmx_go.Activities;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.programacion.robertomtz.cdmx_go.Adapters.HomeAdapter;
import com.programacion.robertomtz.cdmx_go.Classes.Negocio;
import com.programacion.robertomtz.cdmx_go.R;

import java.util.ArrayList;

public class PrincipalActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

                Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
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
                    return new HomeFragment();
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
