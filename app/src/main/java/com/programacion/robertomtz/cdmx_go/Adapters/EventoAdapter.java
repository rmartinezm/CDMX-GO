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

    private static Context context;
    private LinkedList<Negocio> negocios;

    public EventoAdapter(Context context, LinkedList<Negocio> negocios){
        this.context = context;
        this.negocios = negocios;
    }

    @Override
    public int getCount() {
        return negocios.size()+1;
    }

    @Override
    public Object getItem(int i) {
        return negocios.get(i+1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        LayoutInflater inflater = LayoutInflater.from(context);

        // Para poner el layout del filtro en posicion cero
        if (position == 0){
            convertView = inflater.inflate(R.layout.adapter_filtro, null);
            return convertView;
        }

        // ViewHolder Pattern para no tener que cargar los items una vez que ya han sido cargados
        ViewHolder holder;
        Negocio negocio = negocios.get(position-1);

        if (convertView.getTag() == null){

            convertView = inflater.inflate(R.layout.adapter_lv_eventos, null);

            holder = new ViewHolder();

            holder.nombre = (TextView) convertView.findViewById(R.id.eventos_tv_nombre_del_evento);
            holder.descripcion = (TextView) convertView.findViewById(R.id.eventos_tv_descripcion);
            holder.imagen = (ImageView) convertView.findViewById(R.id.eventos_iv_image);

            Glide.with(context).load(negocio.getUrlImagen()).into(holder.imagen);

            convertView.setTag(holder);

        }else
            holder = (ViewHolder) convertView.getTag();

        // LLenamos los view con nuestros datos
        holder.nombre.setText(negocio.getNombre());
        holder.descripcion.setText(negocio.getDescripcion());

        return convertView;
    }

    private static class ViewHolder{
        TextView nombre;
        TextView descripcion;
        ImageView imagen;
    }

}
