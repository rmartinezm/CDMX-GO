package com.programacion.robertomtz.cdmx_go.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.programacion.robertomtz.cdmx_go.Classes.Recompensa;
import com.programacion.robertomtz.cdmx_go.R;

import java.util.LinkedList;

/**
 * Created by rmartinezm on 08/05/2017.
 */

public class RecompensaAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<Recompensa> recompensas;

    public RecompensaAdapter(Context context, LinkedList<Recompensa> recompensas) {
        this.context = context;
        this.recompensas = recompensas;
    }

    @Override
    public int getCount() {
        return recompensas.size();
    }

    @Override
    public Object getItem(int i) {
        return recompensas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v = null;

        Recompensa recompensa = recompensas.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.adapter_lv_recompensas, null);

        TextView descripcion = (TextView) v.findViewById(R.id.adapter_recompensas_tv_descripcion);
        TextView coins = (TextView) v.findViewById(R.id.adapter_recompensas_tv_coins);

        descripcion.setText(recompensa.getDescripcion());
        String monedas = recompensa.getCoins() + "";
        coins.setText(monedas);

        return v;
    }
}
