package com.programacion.robertomtz.cdmx_go.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.programacion.robertomtz.cdmx_go.Activities.CardViewActivity;
import com.programacion.robertomtz.cdmx_go.Activities.PrincipalActivity;
import com.programacion.robertomtz.cdmx_go.Classes.Negocio;
import com.programacion.robertomtz.cdmx_go.R;

import java.util.LinkedList;

/**
 * Created by rmartinezm on 19/05/2017.
 */

public class EventosAdapterRecycler extends RecyclerView.Adapter<EventosAdapterRecycler.ViewHolder> {

    private static LinkedList<Negocio> negocios;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView titulo, descripcion;
        public ImageView imagen;

        // Tomamos la referencia a todos nuestros Views del adaptador
        public ViewHolder(View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.eventos_tv_nombre_del_evento);
            descripcion = (TextView) itemView.findViewById(R.id.eventos_tv_descripcion);
            imagen = (ImageView) itemView.findViewById(R.id.eventos_iv_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PrincipalActivity.context, CardViewActivity.class);
                    intent.putExtra("negocio", negocios.get(getAdapterPosition()));
                    String posicion = getAdapterPosition()+"";
                    intent.putExtra("identificadorEvento", posicion);
                    PrincipalActivity.context.startActivity(intent);
                }
            });
        }

    }

    public void updateList(LinkedList<Negocio> data) {
        negocios = data;
        notifyDataSetChanged();
    }

    public EventosAdapterRecycler(LinkedList<Negocio> negocios){
        EventosAdapterRecycler.negocios = negocios;
    }

    @Override
    public EventosAdapterRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.adapter_lv_eventos, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Negocio negocio = negocios.get(position);

        holder.titulo.setText(negocio.getNombre());
        holder.descripcion.setText(negocio.getDescripcion());
        Glide.with(PrincipalActivity.context).load(negocio.getUrlImagen()).into(holder.imagen);
    }

    @Override
    public int getItemCount() {
        return negocios.size();
    }

}
