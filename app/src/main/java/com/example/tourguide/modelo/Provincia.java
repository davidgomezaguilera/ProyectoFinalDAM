package com.example.tourguide.modelo;

public class Provincia {

    private String id;
    private String nombre;
    private String idComunidad;
    private String foto;

    public Provincia(String id, String nombre, String idComunidad) {
        this.id = id;
        this.nombre = nombre;
        this.idComunidad = idComunidad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdComunidad() {
        return idComunidad;
    }

    public void setIdComunidad(String idComunidad) {
        this.idComunidad = idComunidad;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Provincia{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", idComunidad='" + idComunidad + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }
}
