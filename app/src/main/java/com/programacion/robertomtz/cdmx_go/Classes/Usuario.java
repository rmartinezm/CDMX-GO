package com.programacion.robertomtz.cdmx_go.Classes;

import java.io.Serializable;

/**
 * Created by rmartinezm on 14/04/2017.
 */

public class Usuario implements Serializable {

    private String username;
    private String password;
    private String email;
    private String photo;
    private boolean notificaciones;
    private int coins;

    public Usuario() {}

    public Usuario(String userName, String password, String email, String photo, int coins) {
        this.username = userName;
        this.password = password;
        this.email = email;
        this.photo = photo;
        this.coins = coins;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Usuario){
            @SuppressWarnings("unchecked") Usuario usuario = (Usuario) o;
            if (usuario.getCoins() == coins && usuario.getEmail().equals(email) &&
                    usuario.getPassword().equals(password) && usuario.getPhoto().equals(photo)
                    && usuario.getUserName().equals(username))
                return true;
        }
        return false;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public boolean getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(boolean notificaciones) {
        this.notificaciones = notificaciones;
    }
}
