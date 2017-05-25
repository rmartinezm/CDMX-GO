package com.programacion.robertomtz.cdmx_go.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
    private TextView tvCoins;
    int monedasDadasPorMiRecompensa;

    public FragmentRecompensas(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        recompensasReference = FirebaseDatabase.getInstance().getReference().child("recompensas");
        // Referencia a nuestro usuario en la base de datos
        userReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        View rootView = inflater.inflate(R.layout.activity_fragment_recompensas, container, false);

        tvCoins = (TextView) rootView.findViewById(R.id.fragment_recompensas_tv_coins);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("coins")) {
                    String misMonedas = "Mis Monedas: " + dataSnapshot.child("coins").getValue().toString();
                    tvCoins.setText(misMonedas);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

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
            public void onItemClick(AdapterView<?> adapterView, final View view, final int position, long l) {
                // Referencia a la recompensa clickeada
                final DatabaseReference miRecompensa = recompensasReference.child(recompensasList.get(position).getId());

                miRecompensa.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        monedasDadasPorMiRecompensa = dataSnapshot.child("coins").getValue(Integer.class);

                        // Damos por omision por el momento la recompensa del CCADET
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
                                                Snackbar.make(PrincipalActivity.viewActivity, "Recompensa reclamada correctamente, has ganado "
                                                        + monedasDadasPorMiRecompensa + " monedas!", Snackbar.LENGTH_SHORT).show();
                                                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        int misCoins = dataSnapshot.child("coins").getValue(Integer.class);
                                                        // Actualizamos las monedas del usuario
                                                        userReference.child("coins").setValue(misCoins + monedasDadasPorMiRecompensa);
                                                        monedasDadasPorMiRecompensa = 0;
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                // Eliminamos el elemento de la lista y actualizamos

                                                recompensasList.remove(position);
                                                if (listViewRecompensas.getAdapter() != null)
                                                    ((BaseAdapter) listViewRecompensas.getAdapter()).notifyDataSetChanged();

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

                        // Simulacion de estar buscando ubicacion
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

                            // Iniciamos el scanner y esperamos tener un resultado
                            Intent intent = new Intent(PrincipalActivity.context, ScannerActivity.class);
                            intent.putExtra("codigo", recompensasList.get(position).getKey());
                            intent.putExtra("position", position);
                            getActivity().startActivityForResult(intent, 2);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2)
            if (data != null){

                final int position = data.getIntExtra("position", -1);
                final int keyLeidaPorQR = data.getIntExtra("result", 0);

                if (position == -1){
                    Toast.makeText(getContext(), "Error al reclamar la recompensa", Toast.LENGTH_SHORT).show();
                    onResume();
                    return;
                }

                DatabaseReference recompensaAReclamar = recompensasReference.child(recompensasList.get(position).getId());
                recompensaAReclamar.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshotRecompensaAReclamar) {
                        final String key = dataSnapshotRecompensaAReclamar.child("key").getValue().toString();

                        DatabaseReference recompensasUsuario = userReference.child("recompensas");
                        recompensasUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshotRecompensasUsuario) {

                                if (dataSnapshotRecompensasUsuario.hasChild(recompensasList.get(position).getId())){
                                    Toast.makeText(PrincipalActivity.context, "La recompensa ya fue reclamada anteriormente", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // La recompensa no ha sido reclamada
                                if (keyLeidaPorQR == ScannerActivity.CORRECTO){

                                    userReference.child("recompensas").child(recompensasList.get(position).getId()).setValue(true);
                                    Snackbar.make(PrincipalActivity.viewActivity, "Recompensa reclamada correctamente, has ganado "
                                            + monedasDadasPorMiRecompensa + " monedas!", Snackbar.LENGTH_SHORT).show();
                                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            int misCoins = dataSnapshot.child("coins").getValue(Integer.class);
                                            // Actualizamos las monedas del usuario
                                            userReference.child("coins").setValue(misCoins + monedasDadasPorMiRecompensa);
                                            monedasDadasPorMiRecompensa = 0;
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    // Eliminamos el elemento de la lista y actualizamos

                                    recompensasList.remove(position);
                                    if (listViewRecompensas.getAdapter() != null)
                                        ((BaseAdapter) listViewRecompensas.getAdapter()).notifyDataSetChanged();

                                }else
                                    Snackbar.make(PrincipalActivity.viewActivity, "Código incorrecto, intente otra vez", Snackbar.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

    }

    private class AsyncTaskRecompensas extends AsyncTask<Void, Integer, Boolean>{

        private String keyMiRecompensa;

        @Override
        protected Boolean doInBackground(Void... voids) {
            recompensasList = new LinkedList<>();

            final DatabaseReference yaReclamadasReference = userReference.child("recompensas");
            final LinkedList<String> keysRecompensasYaReclamadas = new LinkedList<>();

            yaReclamadasReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot aux: dataSnapshot.getChildren())
                        keysRecompensasYaReclamadas.add(aux.getKey());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

            recompensasReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Rellenamos recompensasList con todas las recompensas que tiene el usuario
                    for (DataSnapshot miDataSnapshot : dataSnapshot.getChildren()) {
                        keyMiRecompensa = miDataSnapshot.getKey();
                        if (!keysRecompensasYaReclamadas.contains(keyMiRecompensa)) {
                            Recompensa recompensa = new Recompensa(miDataSnapshot.child("coins").getValue(Integer.class), miDataSnapshot.child("descripcion").getValue(String.class), keyMiRecompensa);
                            if (miDataSnapshot.hasChild("key"))
                                recompensa.setKey(miDataSnapshot.child("key").getValue(String.class));
                            recompensasList.add(recompensa);
                        }
                    }
                    // Notificamos para que se actualice el ListView
                    if (listViewRecompensas.getAdapter() != null)
                        ((BaseAdapter) listViewRecompensas.getAdapter()).notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

            return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            recompensaAdapter = new RecompensaAdapter(PrincipalActivity.context, recompensasList);
            listViewRecompensas.setAdapter(recompensaAdapter);
        }
    }

}
