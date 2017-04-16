package com.programacion.robertomtz.cdmx_go.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.programacion.robertomtz.cdmx_go.Classes.Negocio;

import java.util.ArrayList;

/**
 * Created by Montserrat on 15/04/2017.
 */

public class HomeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Negocio> negocios;

    public HomeAdapter(Context context, ArrayList<Negocio> negocios){
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
    public View getView(int i, View view, ViewGroup viewGroup) {


        return null;
    }
}
