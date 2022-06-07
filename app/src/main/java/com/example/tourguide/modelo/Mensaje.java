package com.example.tourguide.modelo;

public class Mensaje {
    private String emisor, tipo, mensaje,urlfoto,tipomensaje;

    public Mensaje(String emisor, String tipo, String mensaje, String urlfoto, String tipomensaje) {
        this.emisor = emisor;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.urlfoto = urlfoto;
        this.tipomensaje = tipomensaje;
    }

    public Mensaje() {
    }


    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUrlfoto() {
        return urlfoto;
    }

    public void setUrlfoto(String urlfoto) {
        this.urlfoto = urlfoto;
    }

    public String getTipomensaje() {
        return tipomensaje;
    }

    public void setTipomensaje(String tipomensaje) {
        this.tipomensaje = tipomensaje;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                ", emisor='" + emisor + '\'' +
                ", tipo='" + tipo + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", urlfoto='" + urlfoto + '\'' +
                ", tipomensaje='" + tipomensaje + '\'' +
                '}';
    }

   
}
