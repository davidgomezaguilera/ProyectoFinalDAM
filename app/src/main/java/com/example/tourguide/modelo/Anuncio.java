package com.example.tourguide.modelo;
import java.io.Serializable;
import java.util.Date;

public class Anuncio implements Serializable {
    private String tokenTurista,nombre,ciudad,acompanantes,mensaje,id,provincia,idGuia;
    private Date fecha1,fecha2;
    private Date fechaCreacion;
    private Intereses tipo;
    private Turista turista;
    private Estados estado;

    public Anuncio(String id, String tokenTurista, String nombre, String ciudad,String provincia, Intereses tipo, String acompanantes, Date fechaCreacion, Date fecha1, Date fecha2, String mensaje, Turista turista,Estados estado) {
        this.id = id;
        this.tokenTurista = tokenTurista;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.provincia = provincia;
        this.tipo = tipo;
        this.acompanantes = acompanantes;
        this.fechaCreacion = fechaCreacion;
        this.fecha1 = fecha1;
        this.fecha2 = fecha2;
        this.mensaje = mensaje;
        this.turista = turista;
        this.estado = estado;
    }
    public Anuncio(String id, String tokenTurista,String idGuia, String nombre, String ciudad,String provincia, Intereses tipo, String acompanantes, Date fechaCreacion, Date fecha1, Date fecha2, String mensaje, Turista turista,Estados estado) {
        this.id = id;
        this.tokenTurista = tokenTurista;
        this.idGuia = idGuia;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.provincia = provincia;
        this.tipo = tipo;
        this.acompanantes = acompanantes;
        this.fechaCreacion = fechaCreacion;
        this.fecha1 = fecha1;
        this.fecha2 = fecha2;
        this.mensaje = mensaje;
        this.turista = turista;
        this.estado = estado;
    }

    public Anuncio() {

    }

    public String getIdGuia() {
        return idGuia;
    }

    public void setIdGuia(String idGuia) {
        this.idGuia = idGuia;
    }

    public Estados getEstado() {
        return estado;
    }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Turista getTurista() {
        return turista;
    }


    public void setTurista(Turista turista) {
        this.turista = turista;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTokenTurista() {
        return tokenTurista;
    }

    public void setTokenTurista(String tokenTurista) {
        this.tokenTurista = tokenTurista;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Intereses getTipo() {
        return tipo;
    }

    public String getTipoString(){return tipo.toString();}

    public void setTipo(Intereses tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getAcompanantes() {
        return acompanantes;
    }

    public void setAcompanantes(String acompanantes) {
        this.acompanantes = acompanantes;
    }

    public Date getFecha1() {
        return fecha1;
    }

    public void setFecha1(Date fecha1) {
        this.fecha1 = fecha1;
    }

    public Date getFecha2() {
        return fecha2;
    }

    public void setFecha2(Date fecha2) {
        this.fecha2 = fecha2;
    }

    @Override
    public String toString() {
        return "Anuncio{" +
                "tokenTurista='" + tokenTurista + '\'' +
                ", nombre='" + nombre + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", acompanantes='" + acompanantes + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", id='" + id + '\'' +
                ", provincia='" + provincia + '\'' +
                ", fecha1=" + fecha1 +
                ", fecha2=" + fecha2 +
                ", fechaCreacion=" + fechaCreacion +
                ", tipo=" + tipo +
                ", turista=" + turista +
                ", estado=" + estado +
                '}';
    }
}
