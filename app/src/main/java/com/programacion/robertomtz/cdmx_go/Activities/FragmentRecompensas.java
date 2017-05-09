package com.programacion.robertomtz.cdmx_go.Activities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

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

    public FragmentRecompensas(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                listViewRecompensas.getChildAt(position).setBackgroundColor(Color.parseColor("#c5e8f9"));
            }
        });
    }

    private class AsyncTaskRecompensas extends AsyncTask<Void, Integer, Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            DatabaseReference recompensasReference = FirebaseDatabase.getInstance().getReference().child("recompensas");
            recompensasList = new LinkedList<>();

            recompensasReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot miDataSnapshot: dataSnapshot.getChildren())
                        recompensasList.add(new Recompensa(miDataSnapshot.child("coins").getValue(Integer.class), miDataSnapshot.child("descripcion").getValue(String.class)));

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
