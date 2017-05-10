package com.programacion.robertomtz.cdmx_go.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.programacion.robertomtz.cdmx_go.Adapters.RecompensaAdapter;
import com.programacion.robertomtz.cdmx_go.Classes.Recompensa;
import com.programacion.robertomtz.cdmx_go.R;

import java.util.LinkedList;

public class FragmentRecompensas extends Fragment {

    private ListView listViewRecompensas;
    private LinkedList<Recompensa> recompensasList;
    private RecompensaAdapter recompensaAdapter;
    private DatabaseReference recompensasReference;
    private DatabaseReference userReference;
    int monedasDadasPorMiRecompensa;

    public FragmentRecompensas(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        recompensasReference = FirebaseDatabase.getInstance().getReference().child("recompensas");
        // Referencia a nuestro usuario en la base de datos
        userReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        View rootView = inflater.inflate(R.layout.activity_fragment_recompensas, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listViewRecompensas = (ListView) getActivity().findViewById(R.id.fragment_recompensas_lista);

        AsyncTaskRecompensas atr = new AsyncTaskRecompensas();
        atr.execute();

        listViewRecompensas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                // Referencia a la recompensa clickeada
                final DatabaseReference miRecompensa = recompensasReference.child(recompensasList.get(position).getId());

                miRecompensa.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        monedasDadasPorMiRecompensa = dataSnapshot.child("coins").getValue(Integer.class);

                        if (dataSnapshot.child("categoria").getValue().toString().equals("prueba")) {

                            userReference.child("recompensas").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(recompensasList.get(position).getId()))
                                        Toast.makeText(PrincipalActivity.context, "Ya has reclamado esta recompensa", Toast.LENGTH_SHORT).show();
                                    else {
                                        final ProgressDialog progressDialog = new ProgressDialog(PrincipalActivity.context);
                                        progressDialog.setMessage("Verificando ubicación...");
                                        progressDialog.show();

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                progressDialog.dismiss();

                                                // Colocamos la recompensa en la lista de recompensas reclamadas por el usuario
                                                userReference.child("recompensas").child(recompensasList.get(position).getId()).setValue(true);
                                                Toast.makeText(PrincipalActivity.context, "Recompensa reclamada correctamente", Toast.LENGTH_SHORT).show();
                                                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        int misCoins = dataSnapshot.child("coins").getValue(Integer.class);
                                                        // Actualizamos las monedas del usuario
                                                        userReference.child("coins").setValue(misCoins + monedasDadasPorMiRecompensa);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                                // Cambiamos el fondo del elemento seleccionado en el ListView
                                                listViewRecompensas.getChildAt(position).setBackgroundColor(Color.parseColor("#ffd1f1"));
                                            }
                                        }, 1500);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                            return;
                        }

                        if (dataSnapshot.child("categoria").getValue().toString().equals("lugar")){
                            final ProgressDialog progressDialog = new ProgressDialog(PrincipalActivity.context);
                            progressDialog.setMessage("Verificando ubicación...");
                            progressDialog.show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();

                                    new AlertDialog.Builder(PrincipalActivity.context).setMessage("Debes estar mas cerca del lugar indicado para reclamar esta recompensa")
                                            .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    return; // Solo cerramos el AlertDialog
                                                }
                                            }).show();

                                }
                            }, 1500);
                            return;
                        }

                        if (dataSnapshot.child("categoria").getValue().toString().equals("establecimiento")){
                            return;
                        }
                        // Falta evento

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private class AsyncTaskRecompensas extends AsyncTask<Void, Integer, Boolean>{

        private String keyMiRecompensa;
        /**
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(PrincipalActivity.context);
            progressDialog.setMessage("Espera...");
            progressDialog.show();
        }
         **/

        @Override
        protected Boolean doInBackground(Void... voids) {
            recompensasList = new LinkedList<>();

            recompensasReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Rellenamos recompensasList con todas las recompensas que tiene el usuario
                    for (DataSnapshot miDataSnapshot : dataSnapshot.getChildren()) {
                        keyMiRecompensa = miDataSnapshot.getKey();
                        recompensasList.add(new Recompensa(miDataSnapshot.child("coins").getValue(Integer.class),
                                miDataSnapshot.child("descripcion").getValue(String.class), keyMiRecompensa));
                    }
                    // Notificamos para que se actualice el ListView
                    if (listViewRecompensas.getAdapter() != null)
                        ((BaseAdapter) listViewRecompensas.getAdapter()).notifyDataSetChanged();

                }

                public void onCancelled(DatabaseError databaseError) {

                }
                 });

            return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            /** COLOREAR EL LISTVIEW SI YA FUE RECLAMADA LA RECOMPENSA **/


            recompensaAdapter = new RecompensaAdapter(PrincipalActivity.context, recompensasList);
            listViewRecompensas.setAdapter(recompensaAdapter);
            //progressDialog.dismiss();
        }
    }
}
