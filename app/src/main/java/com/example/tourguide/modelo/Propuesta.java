package com.example.tourguide.modelo;

public class Propuesta {
    private String id, idGuia,idAnuncio,mensaje;
    private Estados estado;
    private Guia guia;

    public Propuesta(){

    }

    public Propuesta(String id, String idGuia, String idAnuncio, String mensaje,Estados estado,Guia guia) {
        this.id = id;
        this.idGuia = idGuia;
        this.idAnuncio = idAnuncio;
        this.mensaje = mensaje;
        this.estado = estado;
        this.guia = guia;
    }

    public Estados getEstado() {
        return estado;
    }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }

    public Guia getGuia() {
        return guia;
    }

    public void setGuia(Guia guia) {
        this.guia = guia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdGuia() {
        return idGuia;
    }

    public void setIdGuia(String idGuia) {
        this.idGuia = idGuia;
    }

    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "Propuesta{" +
                "id='" + id + '\'' +
                ", idGuia='" + idGuia + '\'' +
                ", idAnuncio='" + idAnuncio + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", estado=" + estado +
                ", guia=" + guia +
                '}';
    }
}
