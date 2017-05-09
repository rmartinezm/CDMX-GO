package com.programacion.robertomtz.cdmx_go.Classes;

/**
 * Created by rmartinezm on 08/05/2017.
 */

public class Recompensa {

    private int coins;
    private String descripcion;

    public Recompensa() {}

    public Recompensa(int coins, String descripcion) {
        this.coins = coins;
        this.descripcion = descripcion;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
