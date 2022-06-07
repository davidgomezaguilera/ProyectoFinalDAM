package com.example.tourguide.modelo;

public class Ciudad {


    private String id;
    private String nombre;
    private String idProvincia;

    public Ciudad(String id, String nombre, String idProvincia) {
        this.id = id;
        this.nombre = nombre;
        this.idProvincia = idProvincia;
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

    public String getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(String idProvincia) {
        this.idProvincia = idProvincia;
    }

    @Override
    public String toString() {
        return "Ciudad{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", idProvincia='" + idProvincia + '\'' +
                '}';
    }
}
