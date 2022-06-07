package com.example.tourguide.modelo;

import java.io.Serializable;

public class Turista implements Serializable {
    private String id,token,nombre, apellidos, email;
    private Double puntuacion;

    public Turista(){

    }

    public Turista(String id,String token, String nombre, String apellidos,String email,Double puntuacion) {
        this.id = id;
        this.token = token;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.puntuacion = puntuacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellido1(String apellido1) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Double puntuacion) {
        this.puntuacion = puntuacion;
    }

    @Override
    public String toString() {
        return "Turista{" +
                "id=" + id + '\''+
                "nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", puntuacion=" + puntuacion +
                '}';
    }



}
