package com.programacion.robertomtz.cdmx_go.Classes;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by rmartinezm on 15/04/2017.
 */

public class MiBaseDeDatos {

    private static final String USUARIOS = "usuarios";
    private static final String EVENTOS = "eventos";

    private FirebaseDatabase database;
    private DatabaseReference usuarios;
    private DatabaseReference eventos;
    private static MiBaseDeDatos bdd;

    public static Usuario miUsuario;

    // Utilizaremos el patrón de Diseño Singleton
    private MiBaseDeDatos(){

        database = FirebaseDatabase.getInstance();
        usuarios = database.getReference(USUARIOS);
        eventos = database.getReference(EVENTOS);

    }

    /**
     * @param nombreDeUsuario usuario que buscaremos por medio de su nombre de Usuario
     * @return el Usuario si éste se encuentra en la Base de Datos y null
     * en otro caso.
     */
    public Usuario buscaUsuario(String nombreDeUsuario){
        try{
            usuarios.child(nombreDeUsuario).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    MiBaseDeDatos.miUsuario = dataSnapshot.getValue(Usuario.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return miUsuario;
        }catch(Exception e){}
        return null;
    }

    public static MiBaseDeDatos getInstance(){
        return (bdd == null)? new MiBaseDeDatos(): bdd;
    }
}
