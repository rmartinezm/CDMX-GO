package com.programacion.robertomtz.cdmx_go.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.programacion.robertomtz.cdmx_go.Classes.Negocio;
import com.programacion.robertomtz.cdmx_go.R;

import java.util.LinkedList;

/**
 * Created by rmartinezm on 15/04/2017.
 */

public class EventoAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<Negocio> negocios;

    public EventoAdapter(Context context, LinkedList<Negocio> negocios){
        this.context = context;
        this.negocios = negocios;
    }

    @Override
    public int getCount() {
        return negocios.size();
    }

    @Override
    public Object getItem(int i) {
        return negocios.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v = null;

        LayoutInflater inflater = LayoutInflater.from(context);

        v = inflater.inflate(R.layout.adapter_lv_eventos, null);

        Negocio negocio = negocios.get(position);

        TextView nombre = (TextView) v.findViewById(R.id.eventos_tv_nombre_del_evento);
        TextView descripcion = (TextView) v.findViewById(R.id.eventos_tv_descripcion);
        ImageView iv = (ImageView) v.findViewById(R.id.eventos_iv_image);

        nombre.setText(negocio.getNombre());
        descripcion.setText(negocio.getDescripcion());

        Glide.with(context)
                .load(negocio.getUrlImagen())
                .crossFade()
                .into(iv);

        return v;
    }
}
