package com.example.tourguide.controladores;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Ciudad;
import com.example.tourguide.modelo.Comunidad;
import com.example.tourguide.modelo.Estados;
import com.example.tourguide.modelo.Guia;
import com.example.tourguide.modelo.Intereses;
import com.example.tourguide.modelo.Propuesta;
import com.example.tourguide.modelo.Provincia;
import com.example.tourguide.modelo.Turista;

import java.io.File;
import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ControladorBDLocal {

    private SQLiteDatabase bdLocal;
    private Context contexto;

    public ControladorBDLocal(Context contexto){
        this.contexto = contexto;
        this.bdLocal = contexto.openOrCreateDatabase("TourGuide",Context.MODE_PRIVATE,null);
    }


    public void relacionarTablas(){

/*
--
-- Filtros para la tabla `comunidades`
--
ALTER TABLE `comunidades`
  ADD CONSTRAINT `FK_comunidades_pais` FOREIGN KEY (`IDPais`) REFERENCES `pais` (`ID`);

--
-- Filtros para la tabla `guia`
--
ALTER TABLE `guia`
  ADD CONSTRAINT `FK__ciudades_guia` FOREIGN KEY (`IDCiudad`) REFERENCES `ciudades` (`ID`),
  ADD CONSTRAINT `FK_guia_provincias` FOREIGN KEY (`IDProvincia`) REFERENCES `provincias` (`ID`);

--
-- Filtros para la tabla `propuesta`
--
ALTER TABLE `propuesta`
  ADD CONSTRAINT `FK__anuncios` FOREIGN KEY (`IDAnuncio`) REFERENCES `anuncios` (`ID`),
  ADD CONSTRAINT `FK_propuesta_guia` FOREIGN KEY (`IDGuia`) REFERENCES `guia` (`ID`);

--
-- Filtros para la tabla `provincias`
--
ALTER TABLE `provincias`
  ADD CONSTRAINT `FK_provincias_comunidades` FOREIGN KEY (`IDComunidad`) REFERENCES `comunidades` (`ID`);
COMMIT;*/
        bdLocal.execSQL(String.format("ALTER TABLE `anuncios`" +
                "  ADD CONSTRAINT `FK__ciudades` FOREIGN KEY (`idCiudad`) REFERENCES `ciudades` (`id`)," +
                "  ADD CONSTRAINT `FK_anuncios_guia` FOREIGN KEY (`idGuia`) REFERENCES `guia` (`id`)," +
                "  ADD CONSTRAINT `FK_anuncios_tipo` FOREIGN KEY (`idTipo`) REFERENCES `tipo` (`id`)," +
                "  ADD CONSTRAINT `FK_anuncios_turista` FOREIGN KEY (`idTurista`) REFERENCES `turista` (`id`);"));
        bdLocal.execSQL(String.format("ALTER TABLE `ciudades`" +
                "  ADD CONSTRAINT `FK_ciudades_provincias` FOREIGN KEY (`idProvincia`) REFERENCES `provincias` (`id`)"));
        bdLocal.execSQL(String.format("ALTER TABLE `guias`" +
                "  ADD CONSTRAINT `FK__ciudades_guia` FOREIGN KEY (`idCiudad`) REFERENCES `ciudades` (`id`)," +
                "  ADD CONSTRAINT `FK_guia_provincias` FOREIGN KEY (`idProvincia`) REFERENCES `provincias` (`id`);"));
        bdLocal.execSQL(String.format("ALTER TABLE `propuestas`" +
                "  ADD CONSTRAINT `FK__anuncios` FOREIGN KEY (`idAnuncio`) REFERENCES `anuncios` (`id`)," +
                "  ADD CONSTRAINT `FK_propuesta_guia` FOREIGN KEY (`idGuia`) REFERENCES `guia` (`id`);"));
        bdLocal.execSQL(String.format("ALTER TABLE `provincias`" +
                "  ADD CONSTRAINT `FK_provincias_comunidades` FOREIGN KEY (`idComunidad`) REFERENCES `comunidades` (`id`);"));
    }


    public void borrarTabla(String nombreTabla){

        bdLocal.execSQL("DROP TABLE "+nombreTabla+";");
        System.out.println("BORRANDO TABLA..... "+ nombreTabla);

    }

    public Turista login(String email, String pass){
        Turista t = null;
        Cursor r = bdLocal.rawQuery("SELECT * FROM turistas WHERE email ='"+email+"'",null);

        while(r.moveToNext()){

            String id = r.getInt(0)+"";
            String tokenTurista = r.getString(1);
            String nombre = r.getString(2);
            String apellidos = r.getString(3);
            String emailT = r.getString(4);

            t = new Turista(id,tokenTurista,nombre,apellidos,email,5.0);

        }


        return t;
    }


    public boolean insertarGuia(String id,String nombre, String apellido, String token,String idCiudad, String email, String puntuacion, String idProvincia,String foto){
        boolean centinela = false;
        ArrayList<Guia> guias = obtenerGuias();
//parece ser que con esto inserta si el guia no existe en la base de datos
        bdLocal.execSQL("INSERT OR IGNORE INTO guias VALUES("
                +Integer.parseInt(id) +
                ",'"+ nombre +
                "','"+ apellido+
                "','"+ token+
                "',"+ Integer.parseInt(idCiudad)+
                ",'"+ email+
                "','"+ puntuacion+
                "',"+ Integer.parseInt(idProvincia)+
                ",'"+ foto+"');");

        Cursor resultado = bdLocal.rawQuery("SELECT * FROM guias",null);
        System.out.println("COMPROBANDO INSERTACIÓN");
        while(resultado.moveToNext()){
            System.out.println("GUIA INSERTADO? "+ resultado.getString(0));
        }
        /*for (int i = 0; i<guias.size(); i++){
            if(obtenerGuias().get(i).getId() == id){
                centinela = true;
            }else{
                centinela = false;
            }
        }

        if(centinela){

        }else{
            bdLocal.execSQL("INSERT INTO IF NOT EXISTS guias VALUES("
                    +Integer.parseInt(id) +
                    ",'"+ nombre +
                    "','"+ apellido+
                    "','"+ token+
                    "',"+ Integer.parseInt(idCiudad)+
                    ",'"+ email+
                    "','"+ puntuacion+
                    "',"+ Integer.parseInt(idProvincia)+
                    ",'"+ foto+"');");
        }

        //Cursor resultado = bdLocal.rawQuery("SELECT id FROM guias g WHERE id = "+id+"",null);
        System.out.println("resultado.getString(0)");
        if(!resultado.moveToNext()){
            //se ha insertado correctamente el guia

            //System.out.println("Guia : "+ nombre +" insertado correctamente");
            System.out.println("Ya existe el usuario");
            centinela = true;
        }else{
            //no se ha insertado el guia
            bdLocal.execSQL("INSERT INTO guias VALUES("
                    +Integer.parseInt(id) +
                    ",'"+ nombre +
                    "','"+ apellido+
                    "','"+ token+
                    "',"+ Integer.parseInt(idCiudad)+
                    ",'"+ email+
                    "','"+ puntuacion+
                    "',"+ Integer.parseInt(idProvincia)+
                    ",'"+ foto+"');");
            //System.out.println("Error al insertar el guia");
            centinela = false;
        }*/
        return centinela;
    }


    public void insertarGuias(ArrayList<Guia> guias) throws SQLException {
        System.out.println("CREANDO TABLA GUIAS");
            bdLocal.execSQL("CREATE TABLE IF NOT EXISTS guias(id INTEGER,nombre VARCHAR,apellido VARCHAR, token VARCHAR,idCiudad INTEGER,email VARCHAR, puntuacion VARCHAR, idProvincia INTEGER, foto VARCHAR ); ");
        for (int i = 0; i < guias.size(); i++){
            System.out.println("CUANTAS VECES INSERTAS UN GUIA "+ i);
         bdLocal.execSQL("INSERT INTO guias VALUES("
                 +Integer.parseInt(guias.get(i).getId()) +
                 ",'"+ guias.get(i).getNombre() +
                 "','"+ guias.get(i).getApellido()+
                 "','"+ guias.get(i).getToken()+
                 "',"+ Integer.parseInt(guias.get(i).getCiudad())+
                 ",'"+ guias.get(i).getEmail()+
                 "','"+ guias.get(i).getPuntuacion()+
                 "',"+ Integer.parseInt(guias.get(i).getProvincia())+
                 ",'"+ guias.get(i).getFoto()+"');");
        }

    }


    public Guia obtenerGuia(String token){
        Guia g = new Guia();
        Cursor resultado = bdLocal.rawQuery("SELECT * FROM guias INNER JOIN provincias p ON guias.idProvincia = p.id WHERE token = '"+token+"'",null);
        //no se si utilizar el metodo moveToNext() o el moveToFirst()
        if(resultado.moveToFirst()){


            String id = resultado.getInt(0)+"";
            String nombre = resultado.getString(1);
            String apellido = resultado.getString(2);
            String tokenGuia = resultado.getString(3);
            String idCiudad = resultado.getString(4)+"";
            String email = resultado.getString(5);
            //String puntuacion = resultado.getString(6);
            String nombreProvincia = resultado.getString(10);
            String foto = resultado.getString(8);


            g = new Guia(id,tokenGuia,nombre,apellido,email,idCiudad,nombreProvincia,5.0,foto);
        }else{
            System.out.println("ERROR AL OBTENER EL GUIA CON TOKEN + "+ token);
        }

        return g;
    }

    public Guia obtenerGuiaID(String id){

        Guia g = new Guia();
        Cursor resultado = bdLocal.rawQuery("SELECT * FROM guias WHERE id = '"+id+"'",null);

        if(resultado.moveToFirst()){

            //id INTEGER,nombre VARCHAR,apellido VARCHAR, token VARCHAR,idCiudad INTEGER,email VARCHAR, puntuacion VARCHAR, idProvincia INTEGER, foto VARCHAR
            //String id,String token,String nombre, String apellido, String email, String ciudad,String provincia,Double puntuacion,String foto
            String idGuia = resultado.getInt(0)+"";
            String nombre = resultado.getString(1);
            String apellido = resultado.getString(2);
            String tokenGuia = resultado.getString(3);
            String idCiudad = resultado.getString(4)+"";
            String email = resultado.getString(5);
            //String puntuacion = resultado.getString(6);
            String idProvincia = resultado.getString(7)+"";
            String foto = resultado.getString(8);

            g = new Guia(idGuia,tokenGuia,nombre,apellido,email,idCiudad,idProvincia,5.0,foto);
        }else{

            System.out.println("ERROR AL OBTENER EL GUIA CON ID = "+ id);
        }
        return g;
    }


    public ArrayList<Guia> obtenerGuias(){

        Cursor resultado = bdLocal.rawQuery("SELECT * FROM 'guias'", null);
        resultado.moveToFirst();
        while(resultado.moveToNext()){
            System.out.println("Guia -->"+resultado.getString(2));
        }
        return new ArrayList<Guia>();
    }

//LO INSERTA IGUALMENTE, NO FUNCIONA EL OR IGNORE
    public void insertarTurista(String id,String token, String nombre, String apellidos, String email, String puntuacion, String pass){
        System.out.println("INSERTANDO O IGNORANDO.......... "+ id);
        bdLocal.execSQL("INSERT OR IGNORE INTO turistas VALUES("
                +Integer.parseInt(id) +
                ",'"+ token +
                "','"+ nombre+
                "','"+ apellidos+
                "','"+ email+
                "','"+ puntuacion+
                "','"+ pass +"');");

        Cursor resultado = bdLocal.rawQuery("SELECT * FROM turistas",null);
        while(resultado.moveToNext()){
            System.out.println("MOSTRANDO TABLA TURISTAS TRAS LA INSERCION ");
            System.out.println("TURISTA--> " + resultado.getString(0));
        }

    }

    public void insertarTuristas(ArrayList<Turista> turistas){
        System.out.println("CREANDO TABLA TURISTAS");
        bdLocal.execSQL("CREATE TABLE IF NOT EXISTS turistas(id INTEGER, token VARCHAR, nombre VARCHAR, apellidos VARCHAR, email VARCHAR, puntuacion VARCHAR);");
        for (int i = 0; i<turistas.size(); i++){

            bdLocal.execSQL("INSERT INTO turistas VALUES("
                    +Integer.parseInt(turistas.get(i).getId()) +
                    ",'"+ turistas.get(i).getToken() +
                    "','"+ turistas.get(i).getNombre()+
                    "','"+ turistas.get(i).getApellidos()+
                    "','"+ turistas.get(i).getEmail()+
                    "','"+ turistas.get(i).getPuntuacion()+
                    "');");

        }
    }

    public Turista obtenerTurista(String token){

        Turista t = new Turista();

        Cursor r = bdLocal.rawQuery("SELECT * FROM turistas WHERE token = '"+token+"'",null);
        if(r.moveToFirst()){
            //id INTEGER, token VARCHAR, nombre VARCHAR, apellidos VARCHAR, email VARCHAR, puntuacion VARCHAR
            //String id,String token, String nombre, String apellidos,String email,Double puntuacion

            String id = r.getInt(0)+"";
            String tokenTurista = r.getString(1);
            String nombre = r.getString(2);
            String apellidos = r.getString(3);
            String email = r.getString(4);

            t = new Turista(id,tokenTurista,nombre,apellidos,email,5.0);

        }else{
            System.out.println("ERROR AL OBTENER EL TURISTA CON TOKEN = "+ token);
        }
        return t;
    }


    public ArrayList<Turista> obtenerTuristas(){
        System.out.println();
        Cursor resultado = bdLocal.rawQuery("SELECT * FROM 'turistas'", null);
        resultado.moveToFirst();
        while(resultado.moveToNext()){
            System.out.println("Obteniendo resultados de turista: ");
            System.out.println(resultado.getString(2));
        }
        return new ArrayList<Turista>();
    }

    public void insertarAnuncio(String idturista, String idciudad, String acompanantes, Intereses idtipo, String nombreviaje, Date fechacreacion, Date fechainicio, Date fechafin, String mensaje,String estado){

        bdLocal.execSQL("INSERT INTO anuncios VALUES("
                +500 +
                ","+ Integer.parseInt(idturista) +
                ","+ Integer.parseInt(idciudad)+
                ","+ idtipo.ordinal()+
                ","+ null+
                ",'"+ nombreviaje+
                "','"+ acompanantes+
                "','"+ fechacreacion.toString()+
                "','"+ fechainicio.toString()+
                "','"+ fechafin.toString()+
                "','"+ mensaje+
                "','"+ estado+"');");
    }


    public void insertarAnuncios(ArrayList<Anuncio> anuncios) throws SQLException {
        System.out.println("CREANDO TABLA ANUNCIOS");
        bdLocal.execSQL("CREATE TABLE IF NOT EXISTS anuncios(id INTEGER,idTurista INTEGER, idCiudad INTEGER,idTipo INTEGER, idGuia INTEGER,nombre VARCHAR,acompanante VARCHAR,fechaCreacion DATE, fechaInicio DATE, FechaFin DATE, mensaje VARCHAR, estado VARCHAR ); ");
        int tipo = 0;
        for (int i = 0; i < anuncios.size(); i++){
            System.out.println("TOSTRING GETTIPO DE ANUNCIOOOOOOOOOO + " +anuncios.get(i).getTipoString());
            if(anuncios.get(i).getTipoString().equals("DEPORTES")){
                tipo = 1;
            }else if(anuncios.get(i).getTipoString().equals("NATURALEZA")){
                tipo = 2;
            }else if(anuncios.get(i).getTipoString().equals("MONUMENTOS")){
                tipo = 3;
            }else if(anuncios.get(i).getTipoString().equals("FIESTA")){
                tipo = 4;
            }
            String idGuiaPrueba = anuncios.get(i).getIdGuia();
            Object idGuia = null;
            if(idGuiaPrueba!="null"){
                idGuia = Integer.parseInt(idGuiaPrueba);
            }

            bdLocal.execSQL("INSERT INTO anuncios VALUES("
                    +Integer.parseInt(anuncios.get(i).getId()) +
                    ","+ Integer.parseInt(anuncios.get(i).getTurista().getId()) +
                    ","+ Integer.parseInt(anuncios.get(i).getCiudad())+
                    ","+ tipo+
                    ","+ idGuia+
                    ",'"+ anuncios.get(i).getNombre()+
                    "','"+ anuncios.get(i).getAcompanantes()+
                    "','"+ anuncios.get(i).getFechaCreacion().toString()+
                    "','"+ anuncios.get(i).getFecha1().toString()+
                    "','"+ anuncios.get(i).getFecha2().toString()+
                    "','"+ anuncios.get(i).getMensaje()+
                    "','"+ anuncios.get(i).getEstado()+"');");
        }

    }

    public ArrayList<Anuncio> obtenerAnunciosToken(String token) throws ParseException {

        ArrayList<Anuncio> anuncios = new ArrayList<Anuncio>();
        ArrayList<Anuncio> anunciosSinDuplicados = new ArrayList<>();
        System.out.println("antes de la query");
        int contador = 0;
        System.out.println("CONTADOR "+ contador);
        contador++;
        Cursor r = bdLocal.rawQuery("SELECT * FROM anuncios INNER JOIN turistas t ON anuncios.idTurista = t.id INNER JOIN ciudades c ON anuncios.idCiudad = c.id  INNER JOIN provincias p ON c.idProvincia = p.id  WHERE t.token = '"+token+"';",null);
        //Cursor r = bdLocal.rawQuery("SELECT * FROM comunidades",null);
        System.out.println("Despues de la query");
        System.out.println("COLUMNS NAMES RESULTADO = " +r.getColumnNames().toString());
        if(r==null){
            System.out.println("error en la query");
        }
        while(r.moveToNext()){
            for(int i=0;i<r.getColumnCount();i++){
                //System.out.println("NOMBRES DE COLUMNAS DE LA SENTENCIA = " +r.getColumnName(i));
            }
            //System.out.println("RESULTADO "+ i + " " +r.getColumnName(i));
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            //id INTEGER,idTurista INTEGER, idCiudad INTEGER,idTipo INTEGER, idGuia INTEGER,nombre VARCHAR,acompanante VARCHAR,fechaCreacion DATE, fechaInicio DATE, FechaFin DATE, mensaje VARCHAR, estado VARCHAR
            //String id, String tokenTurista, String nombre, String ciudad,String provincia, Intereses tipo, String acompanantes, Date fechaCreacion, Date fecha1, Date fecha2, String mensaje, Turista turista,Estados estado
            System.out.println("VECES QUE SE REPITE  " + r.getString(0));
            String id = r.getInt(0)+"";
            String idTurista = r.getInt(1)+"";
            String idCiudad = r.getInt(2)+"";
            String idTipo = r.getInt(3)+"";


            //System.out.println("ME DEVUELVES EL IDPROVINCIA O NO=?  ?? "+r.getInt(21));
            String idGuia = r.getInt(4)+"";
            String nombre = r.getString(5);
            String acomp = r.getString(6);
            Date fechaCreacion = new Date(r.getString(7));
            Date fecha1 = new Date(r.getString(8));
            Date fecha2 = new Date(r.getString(9));
            String mensaje = r.getString(10);
            String estado = r.getString(11);
            String idProvincia = r.getInt(21)+"";
            //System.out.println("NOMBRE ANUNCIO = "+ nombre);
            //LA SENTENCIA ESTA CORRECTA, AHORA HAY QUE CREAR EL ANUNCIO PARA DEVOLVERLO EN UN ARRAY
            System.out.println("ID ANUNCIOS "+ id);

            Anuncio a = new Anuncio(id,
                    token,
                    idGuia,
                    nombre,
                    idCiudad,
                    idProvincia,
                    Intereses.DEPORTES,
                    acomp,
                    fechaCreacion,
                    fecha1,
                    fecha2,
                    mensaje,
                    obtenerTurista(token),
                    Estados.valueOf(estado));
            if(Integer.parseInt(idTipo)==1){
                a.setTipo(Intereses.DEPORTES);
            }else if(Integer.parseInt(idTipo)==2){
                a.setTipo(Intereses.NATURALEZA);
            }else if(Integer.parseInt(idTipo)==3){
                a.setTipo(Intereses.MONUMENTOS);
            }else if(Integer.parseInt(idTipo)==4){
                a.setTipo(Intereses.FIESTA);
            }

            anuncios.add(a);

            /*if(anunciosSinDuplicados.isEmpty()){
                anunciosSinDuplicados.add(anuncios.get(0));
            }*/
            }

           /* for (int i = 0; i!=anunciosSinDuplicados.size()&&i!=anuncios.size();i++){
                for(int j = 0; j!=anuncios.size();j++){

                    if(j<=anunciosSinDuplicados.size()){

                        if(!anuncios.get(j).getId().equals(anunciosSinDuplicados.get(j))){

                            anunciosSinDuplicados.add(anuncios.get(j));

                        }
                    }


                }
            }*/

        return anuncios;
    }

    public ArrayList<Anuncio> obtenerAnunciosIDGuia(String idGuia) throws ParseException {

        ArrayList<Anuncio> anuncios = new ArrayList<Anuncio>();

        // System.out.println("antes de la query");
        Cursor r = bdLocal.rawQuery("SELECT * FROM anuncios INNER JOIN turistas t ON t.id = anuncios.idTurista INNER JOIN ciudades c ON c.id = anuncios.idCiudad INNER JOIN provincias p ON p.id = c.idProvincia WHERE anuncios.idGuia = '"+idGuia+"'",null);
        System.out.println("Despues de la query");
        System.out.println("COLUMNS NAMES RESULTADO = " +r.getColumnNames().toString());
        if(r==null){
            System.out.println("error en la query");
        }
        while(r.moveToNext()){
            for(int i=0;i<r.getColumnCount();i++){
                //System.out.println("NOMBRES DE COLUMNAS DE LA SENTENCIA = " +r.getColumnName(i));
            }
            //System.out.println("RESULTADO "+ i + " " +r.getColumnName(i));

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            //id INTEGER,idTurista INTEGER, idCiudad INTEGER,idTipo INTEGER, idGuia INTEGER,nombre VARCHAR,acompanante VARCHAR,fechaCreacion DATE, fechaInicio DATE, FechaFin DATE, mensaje VARCHAR, estado VARCHAR
            //String id, String tokenTurista, String nombre, String ciudad,String provincia, Intereses tipo, String acompanantes, Date fechaCreacion, Date fecha1, Date fecha2, String mensaje, Turista turista,Estados estado
            //System.out.println("ENTRAS???");
            String id = r.getInt(0)+"";
            String idTurista = r.getInt(1)+"";
            String idCiudad = r.getInt(2)+"";
            String idTipo = r.getInt(3)+"";


            //System.out.println("ME DEVUELVES EL IDPROVINCIA O NO=?  ?? "+r.getInt(21));
            //String idGuia = r.getInt(4)+"";
            String nombre = r.getString(5);
            String acomp = r.getString(6);
            Date fechaCreacion = new Date(r.getString(7));
            Date fecha1 = new Date(r.getString(8));
            Date fecha2 = new Date(r.getString(9));
            String mensaje = r.getString(10);
            String estado = r.getString(11);
            String token = r.getString(13);
            String idProvincia = r.getInt(21)+"";
            System.out.println("NOMBRE ANUNCIO = "+ nombre);
            //LA SENTENCIA ESTA CORRECTA, AHORA HAY QUE CREAR EL ANUNCIO PARA DEVOLVERLO EN UN ARRAY

            Anuncio a = new Anuncio(id,
                    token,
                    nombre,
                    idCiudad,
                    idProvincia,
                    Intereses.DEPORTES,
                    acomp,
                    fechaCreacion,
                    fecha1,
                    fecha2,
                    mensaje,
                    obtenerTurista(token),
                    Estados.valueOf(estado));
            if(Integer.parseInt(idTipo)==1){
                a.setTipo(Intereses.DEPORTES);
            }else if(Integer.parseInt(idTipo)==2){
                a.setTipo(Intereses.NATURALEZA);
            }else if(Integer.parseInt(idTipo)==3){
                a.setTipo(Intereses.MONUMENTOS);
            }else if(Integer.parseInt(idTipo)==4){
                a.setTipo(Intereses.FIESTA);
            }
            anuncios.add(a);
        }
        return anuncios;
    }
    public ArrayList<Anuncio> obtenerAnunciosProvincia(String nombreProvincia) throws ParseException {

        ArrayList<Anuncio> anuncios = new ArrayList<Anuncio>();
        //System.out.println("antes de la query");
        Cursor r = bdLocal.rawQuery("SELECT * FROM anuncios INNER JOIN turistas t ON t.id = anuncios.idTurista INNER JOIN ciudades c ON c.id = anuncios.idCiudad INNER JOIN provincias p ON p.id = c.idProvincia WHERE p.nombre = '"+nombreProvincia+"'",null);
        System.out.println("Despues de la query");
        System.out.println("COLUMNS NAMES RESULTADO = " +r.getColumnNames().toString());
        if(r==null){
            System.out.println("error en la query");
        }
        while(r.moveToNext()){
            for(int i=0;i<r.getColumnCount();i++){
                //System.out.println("NOMBRES DE COLUMNAS DE LA SENTENCIA = " +r.getColumnName(i));
            }
            //System.out.println("RESULTADO "+ i + " " +r.getColumnName(i));

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            //id INTEGER,idTurista INTEGER, idCiudad INTEGER,idTipo INTEGER, idGuia INTEGER,nombre VARCHAR,acompanante VARCHAR,fechaCreacion DATE, fechaInicio DATE, FechaFin DATE, mensaje VARCHAR, estado VARCHAR
            //String id, String tokenTurista, String nombre, String ciudad,String provincia, Intereses tipo, String acompanantes, Date fechaCreacion, Date fecha1, Date fecha2, String mensaje, Turista turista,Estados estado
            System.out.println("ENTRAS???");
            String id = r.getInt(0)+"";
            String idTurista = r.getInt(1)+"";
            String idCiudad = r.getInt(2)+"";
            String idTipo = r.getInt(3)+"";


            String idProvincia = r.getInt(21)+"";
            String idGuia = r.getInt(4)+"";
            String nombre = r.getString(5);
            String acomp = r.getString(6);
            Date fechaCreacion = new Date(r.getString(7));
            Date fecha1 = new Date(r.getString(8));
            Date fecha2 = new Date(r.getString(9));
            String mensaje = r.getString(10);
            String estado = r.getString(11);
            String token = r.getString(13);
            System.out.println("NOMBRE ANUNCIO = "+ nombre);
            //LA SENTENCIA ESTA CORRECTA, AHORA HAY QUE CREAR EL ANUNCIO PARA DEVOLVERLO EN UN ARRAY

            Anuncio a = new Anuncio(id,
                    token,
                    nombre,
                    idCiudad,
                    idProvincia,
                    Intereses.DEPORTES,
                    acomp,
                    fechaCreacion,
                    fecha1,
                    fecha2,
                    mensaje,
                    obtenerTurista(token),
                    Estados.valueOf(estado));
            if(Integer.parseInt(idTipo)==1){
                a.setTipo(Intereses.DEPORTES);
            }else if(Integer.parseInt(idTipo)==2){
                a.setTipo(Intereses.NATURALEZA);
            }else if(Integer.parseInt(idTipo)==3){
                a.setTipo(Intereses.MONUMENTOS);
            }else if(Integer.parseInt(idTipo)==4){
                a.setTipo(Intereses.FIESTA);
            }
            anuncios.add(a);
        }
        return anuncios;
    }

    public Anuncio obtenerAnunciosID(int idAnuncio) throws ParseException {

        ArrayList<Anuncio> anuncios = new ArrayList<Anuncio>();
        Anuncio a = null;
        //System.out.println("antes de la query");
        Cursor r = bdLocal.rawQuery("SELECT * FROM anuncios a INNER JOIN turistas t ON t.id = a.idTurista INNER JOIN ciudades c ON c.id = a.idCiudad INNER JOIN provincias p ON p.id = c.idProvincia WHERE a.ID = "+idAnuncio+"",null);
        System.out.println("Despues de la query");
        //System.out.println("COLUMNS NAMES RESULTADO = " +r.getColumnNames().toString());
        if(r==null){
            System.out.println("error en la query");
        }
        while(r.moveToNext()){
            for(int i=0;i<r.getColumnCount();i++){
                //System.out.println("NOMBRES DE COLUMNAS DE LA SENTENCIA = " +r.getColumnName(i));
            }
            //System.out.println("RESULTADO "+ i + " " +r.getColumnName(i));

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            //id INTEGER,idTurista INTEGER, idCiudad INTEGER,idTipo INTEGER, idGuia INTEGER,nombre VARCHAR,acompanante VARCHAR,fechaCreacion DATE, fechaInicio DATE, FechaFin DATE, mensaje VARCHAR, estado VARCHAR
            //String id, String tokenTurista, String nombre, String ciudad,String provincia, Intereses tipo, String acompanantes, Date fechaCreacion, Date fecha1, Date fecha2, String mensaje, Turista turista,Estados estado
            System.out.println("ENTRAS???");
            String id = r.getInt(0)+"";
            String idTurista = r.getInt(1)+"";
            String idCiudad = r.getInt(2)+"";
            String idTipo = r.getInt(3)+"";


            String idProvincia = r.getInt(21)+"";
            String idGuia = r.getInt(4)+"";
            String nombre = r.getString(5);
            String acomp = r.getString(6);
            Date fechaCreacion = new Date(r.getString(7));
            Date fecha1 = new Date(r.getString(8));
            Date fecha2 = new Date(r.getString(9));
            String mensaje = r.getString(10);
            String estado = r.getString(11);
            String token = r.getString(13);
            System.out.println("NOMBRE ANUNCIO = "+ nombre);
            //LA SENTENCIA ESTA CORRECTA, AHORA HAY QUE CREAR EL ANUNCIO PARA DEVOLVERLO EN UN ARRAY

            a = new Anuncio(id,
                    token,
                    idGuia,
                    nombre,
                    idCiudad,
                    idProvincia,
                    Intereses.DEPORTES,
                    acomp,
                    fechaCreacion,
                    fecha1,
                    fecha2,
                    mensaje,
                    obtenerTurista(token),
                    Estados.valueOf(estado));
            if(Integer.parseInt(idTipo)==1){
                a.setTipo(Intereses.DEPORTES);
            }else if(Integer.parseInt(idTipo)==2){
                a.setTipo(Intereses.NATURALEZA);
            }else if(Integer.parseInt(idTipo)==3){
                a.setTipo(Intereses.MONUMENTOS);
            }else if(Integer.parseInt(idTipo)==4){
                a.setTipo(Intereses.FIESTA);
            }
        }
        return a;
    }

    public ArrayList<Anuncio> obtenerAnuncios(){


        Cursor resultado = bdLocal.rawQuery("SELECT * FROM anuncios",null);

        while(resultado.moveToNext()){

            System.out.println("ANUNCIO---> "+ resultado.getString(9));

        }

        return new ArrayList<Anuncio>();
    }


    public void insertarCiudades(ArrayList<Ciudad> ciudades){

        bdLocal.execSQL("CREATE TABLE IF NOT EXISTS ciudades(id INTEGER, nombre VARCHAR, idProvincia INTEGER);");

        for (int i = 0; i < ciudades.size();i++){

            bdLocal.execSQL("INSERT OR IGNORE INTO ciudades VALUES('"+ciudades.get(i).getId()+"','"+ciudades.get(i).getNombre()+"','"+ciudades.get(i).getIdProvincia()+"');");
            //System.out.println("INSERTANDO EN LA TABLA CIUDADES DESDE BDEXTERNA");
        }

    }


    public ArrayList<String> obtenerCiudadesNombre(String nombreProvincia){
        ArrayList<String> ciudades = new ArrayList<>();
        Cursor resultado = bdLocal.rawQuery("SELECT nombre FROM ciudades INNER JOIN provincias p ON ciudades.idProvincia = p.id WHERE p.id = '"+nombreProvincia+"'",null);

        while(resultado.moveToNext()){
            ciudades.add(resultado.getString(0));
            //System.out.println("CIUDAD ---> "+ resultado.getString(1));

        }
        return ciudades;
    }
    public String obtenerCiudadesIDPorNombre(String nombreCiudad){
        String ciudad = "";
        Cursor resultado = bdLocal.rawQuery("SELECT id FROM ciudades WHERE nombre = '"+nombreCiudad+"'",null);

        if(resultado.moveToFirst()){
            ciudad = resultado.getString(0);
        }
        return ciudad;
    }

    public ArrayList<String> obtenerCiudadesNombreID(String nombreProvincia){
        ArrayList<String> ciudades = new ArrayList<>();
        Cursor resultado = bdLocal.rawQuery("SELECT nombre FROM ciudades WHERE idProvincia = (SELECT id FROM provincias p WHERE p.nombre = '"+nombreProvincia+"')",null);

        while(resultado.moveToNext()){
            ciudades.add(resultado.getString(0));
            //System.out.println("CIUDAD ---> "+ resultado.getString(1));

        }
        return ciudades;
    }
    public String obtenerCiudadesPorNombre(String  nombreCiudad){
       String ciudad = "";
        Cursor resultado = bdLocal.rawQuery("SELECT id FROM ciudades WHERE nombre = '"+nombreCiudad+"'",null);

        if(resultado.moveToFirst()){
            ciudad = resultado.getString(0);
        }
        return ciudad;
    }
    public ArrayList<Ciudad> obtenerCiudades(){

        Cursor resultado = bdLocal.rawQuery("SELECT * FROM ciudades",null);

        while(resultado.moveToNext()){

            //System.out.println("CIUDAD ---> "+ resultado.getString(1));

        }


        return new ArrayList<Ciudad>();
    }

    public void insertarComunidades(ArrayList<Comunidad> comunidades){

        bdLocal.execSQL("CREATE TABLE IF NOT EXISTS comunidades(id INTEGER, nombre VARCHAR, idPais INTEGER);");
        System.out.println("CREANDO TABLA COMUNIDADES");
        for (int i = 0; i < comunidades.size();i++){
            System.out.println("NOMBRE COMUNIDADES QUE SE INSERTAN "+ comunidades.get(i).getNombre());
            bdLocal.execSQL("INSERT OR IGNORE INTO comunidades VALUES('"+comunidades.get(i).getId()+"','"+comunidades.get(i).getNombre()+"','"+1+"');");
            System.out.println("INSERTANDO EN LA TABLA COMUNIDADES DESDE BDEXTERNA");
        }


    }

    public ArrayList<Comunidad> obtenerComunidades(){

        Cursor resultado = bdLocal.rawQuery("SELECT * FROM comunidades",null);

        while(resultado.moveToNext()){

            System.out.println("COMUNIDAD ---> "+ resultado.getString(1));

        }


        return new ArrayList<Comunidad>();

    }


    public void insertarProvincias(ArrayList<Provincia> provincias){

        bdLocal.execSQL("CREATE TABLE IF NOT EXISTS provincias(id INTEGER, nombre VARCHAR, idComunidad INTEGER,foto VARCHAR);");
        System.out.println("CREANDO TABLA PROVINCIAS");
        for (int i = 0; i < provincias.size();i++){
            System.out.println("NOMBRE PROVINCIA INSERTADA  "+ provincias.get(i).getNombre());
            bdLocal.execSQL("INSERT OR IGNORE INTO provincias VALUES("+Integer.parseInt(provincias.get(i).getId())+",'"+provincias.get(i).getNombre()+"','"+provincias.get(i).getIdComunidad()+"','"+"ciudad1"+"');");
            System.out.println("INSERTANDO EN LA TABLA provincias DESDE BDEXTERNA");
        }


    }

    public ArrayList<String> obtenerProvincias(){

        ArrayList<String> nombresProvincias = new ArrayList<>();

        Cursor resultado = bdLocal.rawQuery("SELECT nombre FROM provincias",null);

        while(resultado.moveToNext()){

            nombresProvincias.add(resultado.getString(0));

        }

        return nombresProvincias;
    }

    public void insertarPropuestas(ArrayList<Propuesta> propuestas){

        bdLocal.execSQL("CREATE TABLE IF NOT EXISTS propuestas(id INTEGER, idGuia INTEGER, idAnuncio INTEGER, mensaje TEXT, estado VARCHAR);");

        for(int i=0;i<propuestas.size();i++){

            bdLocal.execSQL("INSERT INTO propuestas VALUES("
                    +Integer.parseInt(propuestas.get(i).getId()) +
                    ","+Integer.parseInt(propuestas.get(i).getIdGuia()) +
                    ","+ Integer.parseInt(propuestas.get(i).getIdAnuncio())+
                    ",'"+ propuestas.get(i).getMensaje()+
                    "','"+ propuestas.get(i).getEstado().toString()+"');");
            System.out.println("INSERTANDO EN LA TABLA PROPUESTAS!! " + Integer.parseInt(propuestas.get(i).getIdAnuncio()));
        }

    }


    public ArrayList<Propuesta> obtenerPropuestaIDAnuncio(String idAnuncio){
        ArrayList<Propuesta> propuestas = new ArrayList<Propuesta>();
        //Cursor resultado = bdLocal.rawQuery("SELECT * FROM propuestas WHERE idAnuncio = (SELECT id FROM anuncios WHERE id = "+idAnuncio+")",null);
        Cursor resultado = bdLocal.rawQuery("SELECT * FROM propuestas p WHERE p.idAnuncio = "+idAnuncio+"",null);

        while(resultado.moveToNext()){
            //id INTEGER, idGuia INTEGER, idAnuncio INTEGER, mensaje TEXT, estado VARCHAR
//String id, String idGuia, String idAnuncio, String mensaje,Estados estado,Guia guia
            String id = resultado.getInt(0)+"";
            String idGuia = resultado.getInt(1)+"";
            String idAnuncioP = resultado.getInt(2)+"";
            String mensaje = resultado.getString(3);
            String estado = resultado.getString(4);
            Propuesta p = new Propuesta(id,idGuia,idAnuncio,mensaje,Estados.valueOf(estado),obtenerGuiaID(idGuia));
            //System.out.println("ID PROPUESTA : "+ resultado.getInt(0));
            propuestas.add(p);
        }


        return propuestas;
    }

    public ArrayList<Propuesta> obtenerPropuestaIDAnuncioIDGuia(String idAnuncio, String idGuia){
        ArrayList<Propuesta> propuestas = new ArrayList<Propuesta>();
        //Cursor resultado = bdLocal.rawQuery("SELECT * FROM propuestas WHERE idAnuncio = (SELECT id FROM anuncios WHERE id = "+idAnuncio+")",null);
        Cursor resultado = bdLocal.rawQuery("SELECT * FROM propuestas p WHERE p.idAnuncio = (SELECT id FROM anuncios WHERE id = "+idAnuncio+") AND p.idGuia = (SELECT id FROM guias WHERE id = "+idGuia+");",null);

        while(resultado.moveToNext()){
            //id INTEGER, idGuia INTEGER, idAnuncio INTEGER, mensaje TEXT, estado VARCHAR
//String id, String idGuia, String idAnuncio, String mensaje,Estados estado,Guia guia
            String id = resultado.getInt(0)+"";
            String idGuiaNuevaPropuesta = resultado.getInt(1)+"";
            String idAnuncioP = resultado.getInt(2)+"";
            String mensaje = resultado.getString(3);
            String estado = resultado.getString(4);
            Propuesta p = new Propuesta(id,idGuia,idAnuncio,mensaje,Estados.valueOf(estado),obtenerGuiaID(idGuia));
            System.out.println("ID PROPUESTA : "+ resultado.getInt(0));
            propuestas.add(p);
        }


        return propuestas;
    }
    public ArrayList<Propuesta> obtenerPropuestaIDGuia(String idGuia){
        ArrayList<Propuesta> propuestas = new ArrayList<Propuesta>();
        //Cursor resultado = bdLocal.rawQuery("SELECT * FROM propuestas WHERE idAnuncio = (SELECT id FROM anuncios WHERE id = "+idAnuncio+")",null);
        Cursor resultado = bdLocal.rawQuery("SELECT * FROM propuestas p WHERE p.idGuia = "+idGuia+"",null);

        while(resultado.moveToNext()){
            //id INTEGER, idGuia INTEGER, idAnuncio INTEGER, mensaje TEXT, estado VARCHAR
//String id, String idGuia, String idAnuncio, String mensaje,Estados estado,Guia guia
            String id = resultado.getInt(0)+"";
            String idGuiaNP = resultado.getInt(1)+"";
            String idAnuncioP = resultado.getInt(2)+"";
            String mensaje = resultado.getString(3);
            String estado = resultado.getString(4);
            Propuesta p = new Propuesta(id,idGuia,idAnuncioP,mensaje,Estados.valueOf(estado),obtenerGuiaID(idGuia));
            //System.out.println("ID PROPUESTA : "+ resultado.getInt(0));
            propuestas.add(p);
        }


        return propuestas;
    }
    public ArrayList<Propuesta> obtenerPropuestaID(String id){
        ArrayList<Propuesta> propuestas = new ArrayList<Propuesta>();
        //Cursor resultado = bdLocal.rawQuery("SELECT * FROM propuestas WHERE idAnuncio = (SELECT id FROM anuncios WHERE id = "+idAnuncio+")",null);
        Cursor resultado = bdLocal.rawQuery("SELECT * FROM propuestas p WHERE p.id = "+id+"",null);

        while(resultado.moveToNext()){
            //id INTEGER, idGuia INTEGER, idAnuncio INTEGER, mensaje TEXT, estado VARCHAR
//String id, String idGuia, String idAnuncio, String mensaje,Estados estado,Guia guia
            String idNP = resultado.getInt(0)+"";
            String idGuia = resultado.getInt(1)+"";
            String idAnuncioP = resultado.getInt(2)+"";
            String mensaje = resultado.getString(3);
            String estado = resultado.getString(4);
            Propuesta p = new Propuesta(id,idGuia,idAnuncioP,mensaje,Estados.valueOf(estado),obtenerGuiaID(idGuia));
            //System.out.println("ID PROPUESTA : "+ resultado.getInt(0));
            propuestas.add(p);
        }


        return propuestas;
    }

    public ArrayList<Propuesta> obtenerPropuestas(){

        Cursor resultado = bdLocal.rawQuery("SELECT * FROM propuestas",null);

        while(resultado.moveToNext()){

            System.out.println("MOSTRANDO PROPUESTA --> "+resultado.getString(0));

        }


        return new ArrayList<Propuesta>();
    }

    public void insertarIntereses(ArrayList<String> intereses){

        bdLocal.execSQL("CREATE TABLE IF NOT EXISTS tipos(id INTEGER, nombre VARCHAR)");

        for(int i = 0; i<intereses.size();i++){
            bdLocal.execSQL("INSERT INTO tipos VALUES("+(i+1)+",'"+intereses.get(i)+"')");
        }

    }

    public ArrayList<String> obtenerIntereses(){

        Cursor resultado = bdLocal.rawQuery("SELECT * FROM tipos",null);

        while(resultado.moveToNext()){

            System.out.println("INTERESES---> "+ resultado.getInt(0));

        }

        return new ArrayList<String>();
    }

}
