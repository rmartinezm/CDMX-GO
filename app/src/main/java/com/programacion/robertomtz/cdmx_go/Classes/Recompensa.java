package com.programacion.robertomtz.cdmx_go.Classes;

/**
 * Created by rmartinezm on 08/05/2017.
 */

public class Recompensa {

    private String id;
    private int coins;
    private String descripcion;

    public Recompensa() {}

    public Recompensa(int coins, String descripcion, String id) {
        this.coins = coins;
        this.descripcion = descripcion;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
