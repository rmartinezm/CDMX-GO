package com.programacion.robertomtz.cdmx_go.Classes;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Montserrat on 15/04/2017.
 */

public class Negocio implements Serializable {

    private String nombre;
    private String fecha;
    private String horario;
    private String lugar;
    private String urlImagen;
    private String descripcion;
    private HashMap<String, Integer> calificaciones;

    public Negocio() {}

    public Negocio(String nombre, String fecha, String horario, String lugar, String urlImagen, String descripcion, HashMap<String, Integer> calificaciones) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.horario = horario;
        this.lugar = lugar;
        this.urlImagen = urlImagen;
        this.descripcion = descripcion;
        this.calificaciones = calificaciones;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public HashMap<String, Integer> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(HashMap<String, Integer> calificaciones) {
        this.calificaciones = calificaciones;
    }
}
