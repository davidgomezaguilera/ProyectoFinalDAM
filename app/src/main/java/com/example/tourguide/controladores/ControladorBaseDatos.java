package com.example.tourguide.controladores;

import android.os.StrictMode;

import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Ciudad;
import com.example.tourguide.modelo.Comunidad;
import com.example.tourguide.modelo.Estados;
import com.example.tourguide.modelo.Guia;
import com.example.tourguide.modelo.Intereses;
import com.example.tourguide.modelo.Propuesta;
import com.example.tourguide.modelo.Provincia;
import com.example.tourguide.modelo.Turista;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ControladorBaseDatos{

    private final int RESULTADO_OK = 1;
    private final int RESULTADO_ERROR = 2;
    private final int RESULTADO_ERROR_DESCONOCIDO = 3;

    //public final static String urlservidor = "http://tourguide.sytes.net/Tourguide/";
    //public final static String urlservidor = "http://apptourguide.ddns.net/TourGuide/";
    //ip de mi pc
    public final static String urlservidor = "http://192.168.56.1/TourGuide";
    //ip para movil
    //public final static String urlservidor = "http://192.168.0.110/TourGuide";
    private final String urlregistrarGuia = urlservidor + "/registrarGuia.php";
    private final String urlregistrarTurista = urlservidor + "/registrarTurista.php";
    private final String urlobtenerCiudades = urlservidor + "/obtenerCiudades.php";
    private final String urlobtenerProvincias = urlservidor + "/obtenerProvincias.php";
    private final String urlobtenerGuia = urlservidor + "/obtenerGuiaToken.php";
    private final String urlobtenerGuiaID = urlservidor + "/obtenerGuiaID.php";
    private final String urlobtenerTurista = urlservidor + "/obtenerTuristaToken.php";
    private final String urlinsertaranuncio = urlservidor + "/registrarAnuncio.php";
    private final String urlobteneranuncios = urlservidor + "/obtenerAnunciosUS.php";
    private final String urleliminaranuncio = urlservidor + "/eliminarAnuncio.php";
    private final String urleliminarPropuesta = urlservidor + "/eliminarPropuesta.php";
    private final String urlobtenerpropuestaIDA = urlservidor + "/obtenerPropuestaIDA.php";
    private final String urlobtenerpropuestaIDAIDGuia = urlservidor + "/obtenerPropuestaIDA-IGuia.php";
    private final String urlobtenerpropuestaIDGuia = urlservidor + "/obtenerPropuestaIDGuia.php";
    private final String urlobtenerProvinciasFoto = urlservidor + "/obtenerProvinciaFoto.php";
    private final String urlfotos = urlservidor + "/fotos/";
    private final String urlobtenerAnunciosProvincia = urlservidor + "/obtenerAnunciosCiudad.php";
    private final String urlobtenerAnunciosid = urlservidor + "/obtenerAnunciosIDAnuncio.php";
    private final String urlobtenerAnunciosidguia = urlservidor + "/obtenerAnunciosIDGuia.php";
    private final String urlregistrarPropuesta = urlservidor + "/registrarPropuesta.php";
    private final String urlupdateestadopropuesta = urlservidor + "/uptadeEstadoPropuesta.php";
    private final String urlupdateidguiaanuncio = urlservidor + "/uptadeIDGuiaAnuncio.php";
    private final String urlupdateestadoanuncio = urlservidor + "/uptadeEstadoAnuncio.php";
    private final String urlobtenerpropuestaID = urlservidor + "/obtenerPropuestaID.php";
    private final String urlobtenerpropuestas = urlservidor + "/obtenerPropuestas.php";
    private final String urlObtenerGuias = urlservidor + "/obtenerUsuarios.php";
    private final String urlObtenerTuristas = urlservidor + "/obtenerTuristas.php";
    private final String urlObtenerTodasCiudades = urlservidor + "/obtenerTodasCiudades.php";
    private final String urlObtenerTodosAnuncios = urlservidor + "/obtenerAnuncios.php";

    /**
     * Constructor
     */
    public ControladorBaseDatos(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    /**
     * Inserta un guia
     * @return Boolean true si sale bien false si no
     * @throws ServidorPHPException
     */
    public Boolean insertarGuia(String token, String ciudad,String provincia,String nombre,String apellidos,String email, String pass) throws ServidorPHPException{
        Boolean centinela = false;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("Token",token);
        parametros.put("ciudad",ciudad);
        parametros.put("nombre",nombre);
        parametros.put("apellidos",apellidos);
        parametros.put("provincia",provincia);
        parametros.put("email",email);
        parametros.put("puntuacion","5");
        parametros.put("pass", pass);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlregistrarGuia, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        centinela = true;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return centinela;
    }

    /**
     *
     * @param ciudad
     * @return
     * @throws ServidorPHPException
     */
    public ArrayList<String> obtenerCiudades(String ciudad)throws ServidorPHPException{
        ArrayList<String> ciudades = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("ciudad",ciudad);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerCiudades, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");

                        for (int i = 0; i != mensaje.length(); i++){
                            ciudades.add(mensaje.getJSONObject(i).getString("nombre"));
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return ciudades;
    }

    /**
     *
     * @param token
     * @return
     * @throws ServidorPHPException
     */
    public Guia obtenerGuia(String token)throws ServidorPHPException{
        Guia guia = null;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("token",token);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerGuia, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");

                        for (int i = 0; i != mensaje.length(); i++){
                            String id = (mensaje.getJSONObject(i).getString("ID"));
                            String nombre = (mensaje.getJSONObject(i).getString("Nombre"));
                            String ciudad = (mensaje.getJSONObject(i).getString("ciudad"));
                            String apellido = (mensaje.getJSONObject(i).getString("Apellidos"));
                            String email = (mensaje.getJSONObject(i).getString("Email"));
                            String puntuacion = (mensaje.getJSONObject(i).getString("puntuacion"));
                            String nombreProvincia = (mensaje.getJSONObject(i).getString("nombreProvincia"));
                            String foto = (mensaje.getJSONObject(i).getString("foto"));
                            guia = new Guia(id,token,nombre,apellido,email,ciudad,nombreProvincia,Double.valueOf(puntuacion),foto);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return guia;
    }

    /**
     *
     * @param id
     * @return
     * @throws ServidorPHPException
     */
    public Guia obtenerGuiaID(String id)throws ServidorPHPException{
        Guia guia = null;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("id",id);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerGuiaID, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");

                        for (int i = 0; i != mensaje.length(); i++){
                            String token = (mensaje.getJSONObject(i).getString("Token"));
                            String nombre = (mensaje.getJSONObject(i).getString("Nombre"));
                            String ciudad = (mensaje.getJSONObject(i).getString("ciudad"));
                            String apellido = (mensaje.getJSONObject(i).getString("Apellidos"));
                            String email = (mensaje.getJSONObject(i).getString("Email"));
                            String puntuacion = (mensaje.getJSONObject(i).getString("puntuacion"));
                            String nombreProvincia = (mensaje.getJSONObject(i).getString("nombreProvincia"));
                            String foto = (mensaje.getJSONObject(i).getString("foto"));
                            guia = new Guia(id,token,nombre,apellido,email,ciudad,nombreProvincia,Double.valueOf(puntuacion),foto);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return guia;
    }

    /**
     *
     * @return
     * @throws ServidorPHPException
     */
    public ArrayList<String> obtenerProvincias()throws ServidorPHPException{
        ArrayList<String> provincias = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();

        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerProvincias, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");

                        for (int i = 0; i != mensaje.length(); i++){
                            provincias.add(mensaje.getJSONObject(i).getString("nombre"));
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return provincias;
    }

    public ArrayList<Provincia> obtenerTodasProvincias()throws ServidorPHPException{
        ArrayList<Provincia> provincias = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();

        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerProvincias, null);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");

                        for (int i = 0; i != mensaje.length(); i++){
                            String id = mensaje.getJSONObject(i).getString("ID");
                            String nombre = mensaje.getJSONObject(i).getString("nombre");
                            String idComunidad = mensaje.getJSONObject(i).getString("IDComunidad");
                            Provincia p = new Provincia(id,nombre,idComunidad);

                            provincias.add(p);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return provincias;
    }

    public ArrayList<Ciudad> obtenerTodasCiudades()throws ServidorPHPException{
        ArrayList<Ciudad> ciudades = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();

        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlObtenerTodasCiudades, null);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");

                        for (int i = 0; i != mensaje.length(); i++){
                            String id = mensaje.getJSONObject(i).getString("ID");
                            String nombre = mensaje.getJSONObject(i).getString("nombre");
                            String idProvincia = mensaje.getJSONObject(i).getString("IDProvincia");

                            Ciudad c = new Ciudad(id, nombre, idProvincia);

                            ciudades.add(c);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return ciudades;
    }
    public ArrayList<Guia> obtenerGuias()throws ServidorPHPException{
        ArrayList<Guia> guias = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();

        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlObtenerGuias,null);
            System.out.println("DATOS "+ datos.toString());
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                System.out.println("RESULTADOS OBTENIDOS "+ resultadoobtenido);
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");

                        for (int i = 0; i != mensaje.length(); i++){
                            String nombre = mensaje.getJSONObject(i).getString("Nombre");
                            String apellido = mensaje.getJSONObject(i).getString("Apellidos");
                            String email = mensaje.getJSONObject(i).getString("Email");
                            String puntuacion = mensaje.getJSONObject(i).getString("puntuacion");
                            String idProvincia = mensaje.getJSONObject(i).getString("IDProvincia");
                            String token = mensaje.getJSONObject(i).get("Token").toString();
                            String foto = mensaje.getJSONObject(i).getString("foto");
                            String id = mensaje.getJSONObject(i).getString("ID");
                            String idCiudad = mensaje.getJSONObject(i).getString("IDCiudad");
                            guias.add(new Guia(id +"",token,nombre,apellido,email,idCiudad+"",idProvincia+"",5.0,foto));
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return guias;
    }

    public ArrayList<Turista> obtenerTuristas()throws ServidorPHPException{
        ArrayList<Turista> turistas = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();

        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlObtenerTuristas,null);
            System.out.println("DATOS "+ datos.toString());
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                System.out.println("RESULTADOS OBTENIDOS "+ resultadoobtenido);
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");

                        for (int i = 0; i != mensaje.length(); i++){
                            String nombre = mensaje.getJSONObject(i).getString("Nombre");
                            String apellido = mensaje.getJSONObject(i).getString("Apellidos");
                            String email = mensaje.getJSONObject(i).getString("Email");
                            //String puntuacion = mensaje.getJSONObject(i).getString("puntuacion");
                            String token = mensaje.getJSONObject(i).get("Token").toString();
                            String id = mensaje.getJSONObject(i).getString("ID");
                            turistas.add(new Turista(id +"",token,nombre,apellido,email,5.0));
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return turistas;
    }
/*
    public ArrayList<String> obtenerTodasCiudades()throws ServidorPHPException{
        ArrayList<String> ciudades = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();

        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlObtenerTodasCiudades,null);
            System.out.println("DATOS "+ datos.toString());
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                System.out.println("RESULTADOS OBTENIDOS "+ resultadoobtenido);
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");

                        for (int i = 0; i != mensaje.length(); i++){
                            String nombre = mensaje.getJSONObject(i).getString("Nombre");
                            String apellido = mensaje.getJSONObject(i).getString("Apellidos");
                            String email = mensaje.getJSONObject(i).getString("Email");
                            //String puntuacion = mensaje.getJSONObject(i).getString("puntuacion");
                            String token = mensaje.getJSONObject(i).get("Token").toString();
                            String id = mensaje.getJSONObject(i).getString("ID");
                            //turistas.add(new Turista(id +"",token,nombre,apellido,email,5.0));
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return new Turista();
    }
*/
    /**
     *
     * @return
     * @throws ServidorPHPException
     */
    public String obtenerFotoProvincia(String ciudad) throws ServidorPHPException{
        String foto = "";
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("nombre",ciudad);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerProvinciasFoto, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        foto = urlfotos + (mensaje.getJSONObject(0).getString("foto"));

                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return foto;
    }
    /**
     *
     * @param token
     * @param nombre
     * @param apellidos
     * @param email
     * @return
     * @throws ServidorPHPException
     */
    public Boolean insertarTurista(String token,String nombre,String apellidos,String email,String ciudad, String pass) throws ServidorPHPException{
        Boolean centinela = false;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("Token",token);
        parametros.put("nombre",nombre);
        parametros.put("apellidos",apellidos);
        parametros.put("email",email);
        parametros.put("ciudad",ciudad);
        parametros.put("puntuacion","5");
        parametros.put("pass",pass);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlregistrarTurista, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        centinela = true;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return centinela;
    }

    /**
     *
     * @param token
     * @return
     * @throws ServidorPHPException
     */
    public Turista obtenerTurista(String token)throws ServidorPHPException{
        Turista turista = null;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("token",token);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerTurista, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");

                        for (int i = 0; i != mensaje.length(); i++){
                            String id = (mensaje.getJSONObject(i).getString("ID"));
                            String nombre = (mensaje.getJSONObject(i).getString("Nombre"));
                            String apellido = (mensaje.getJSONObject(i).getString("Apellidos"));
                            String email = (mensaje.getJSONObject(i).getString("Email"));
                            String puntuacion = (mensaje.getJSONObject(i).getString("puntuacion"));
                            turista = new Turista(id,token,nombre,apellido,email,Double.valueOf(puntuacion));
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return turista;
    }

    /**
     *
     * @param idturista
     * @param idciudad
     * @param acompanantes
     * @param idtipo
     * @param nombreviaje
     * @param fechacreacion
     * @param fechainicio
     * @param fechafin
     * @param mensaje
     * @return
     * @throws ServidorPHPException
     */
    public Boolean insertarAnuncio(String idturista, String idciudad, String acompanantes, Intereses idtipo, String nombreviaje, Date fechacreacion, Date fechainicio, Date fechafin, String mensaje,String estado)throws ServidorPHPException{
        Boolean centinela = false;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("idturista",idturista);
        parametros.put("idciudad",idciudad);
        parametros.put("acompanantes",acompanantes);
        parametros.put("idtipo",String.valueOf(idtipo));
        parametros.put("nombreviaje",String.valueOf(nombreviaje));
        parametros.put("fechacreacion",String.valueOf(fechacreacion));
        parametros.put("fechainicio",String.valueOf(fechainicio));
        parametros.put("fechafin",String.valueOf(fechafin));
        parametros.put("mensaje",mensaje);
        parametros.put("estado",estado);
        System.out.println(parametros.toString());
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlinsertaranuncio, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        centinela = true;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return centinela;
    }

    /**
     *
     * @param token
     * @return
     * @throws ServidorPHPException
     */
    public ArrayList<Anuncio> obtenerAnuncios(String token)throws ServidorPHPException{
        Turista turista = obtenerTurista(token);
        ArrayList<Anuncio>anuncios = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("token",token);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobteneranuncios, parametros);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = new GregorianCalendar();
                        for (int i = 0; i != mensaje.length(); i++){
                            String id = (mensaje.getJSONObject(i).getString("ID"));
                            String NombreViaje = (mensaje.getJSONObject(i).getString("NombreViaje"));
                            String Acompanantes = (mensaje.getJSONObject(i).getString("Acompanantes"));
                            String FechaCreacion = (mensaje.getJSONObject(i).getString("FechaCreacion"));
                            String FechaInicio = (mensaje.getJSONObject(i).getString("FechaInicio"));
                            String FechaFin = (mensaje.getJSONObject(i).getString("FechaFin"));
                            String Mensaje = (mensaje.getJSONObject(i).getString("Mensaje"));
                            String nombreCiudad = (mensaje.getJSONObject(i).getString("nombreCiudad"));
                            String nombreInteres = (mensaje.getJSONObject(i).getString("nombreInteres"));
                            String estado = (mensaje.getJSONObject(i).getString("Estado"));
                            Anuncio a = new Anuncio(id,token,NombreViaje,nombreCiudad,null,Intereses.valueOf(nombreInteres),Acompanantes,formato.parse(FechaCreacion),formato.parse(FechaInicio),formato.parse(FechaFin),Mensaje,turista,Estados.valueOf(estado));
                            anuncios.add(a);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException | ParseException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return anuncios;
    }

    /*
    * PROBLEMA AL OBTENER TODOS LOS ANUNCIOS YA QUE NECESITO EL TOKEN PARA CREAR UN ANUNCIO.. Y NO SE DE DONDE RECOGERLO
    *
    * */

    public ArrayList<Anuncio> obtenerTodosAnuncios()throws ServidorPHPException{
        ArrayList<Anuncio>anuncios = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlObtenerTodosAnuncios,null);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = new GregorianCalendar();
                        for (int i = 0; i != mensaje.length(); i++){
                            String id = (mensaje.getJSONObject(i).getString("ID"));
                            String NombreViaje = (mensaje.getJSONObject(i).getString("NombreViaje"));
                            String Acompanantes = (mensaje.getJSONObject(i).getString("Acompanantes"));
                            String FechaCreacion = (mensaje.getJSONObject(i).getString("FechaCreacion"));
                            String FechaInicio = (mensaje.getJSONObject(i).getString("FechaInicio"));
                            String FechaFin = (mensaje.getJSONObject(i).getString("FechaFin"));
                            String Mensaje = (mensaje.getJSONObject(i).getString("Mensaje"));
                            String nombreCiudad = (mensaje.getJSONObject(i).getString("nombreCiudad"));
                            String nombreInteres = (mensaje.getJSONObject(i).getString("nombreInteres"));
                            String estado = (mensaje.getJSONObject(i).getString("Estado"));
                            //String token = (mensaje.getJSONObject(i).getString("token"));
                            int idCiudad = mensaje.getJSONObject(i).getInt("idCiudad");
                            String idGuia = mensaje.getJSONObject(i).getString("idGuia");

                            String idTurista = (mensaje.getJSONObject(i).getString("idTurista"));
                            String tokenTurista = (mensaje.getJSONObject(i).getString("tokenTurista"));
                            String nombreTurista = (mensaje.getJSONObject(i).getString("nombreTurista"));
                            String apellidosTurista = (mensaje.getJSONObject(i).getString("apellidosTurista"));
                            String emailTurista = (mensaje.getJSONObject(i).getString("emailTurista"));



                            Turista t = new Turista(idTurista,tokenTurista,nombreTurista,apellidosTurista,emailTurista,5.0);
                            Anuncio a = new Anuncio(id,tokenTurista,idGuia, NombreViaje,idCiudad+"",null,Intereses.valueOf(nombreInteres),Acompanantes,formato.parse(FechaCreacion),formato.parse(FechaInicio),formato.parse(FechaFin),Mensaje,t,Estados.valueOf(estado));
                            anuncios.add(a);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException | ParseException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return anuncios;
    }


    public ArrayList<Comunidad> obtenerComunidades(){

        ArrayList<Comunidad> comunidades = new ArrayList<Comunidad>();

        Comunidad andalucia = new Comunidad("1","Andalucía");
        Comunidad aragon = new Comunidad("2", "Aragón");
        Comunidad asturias = new Comunidad("3","Asturias");
        Comunidad baleares = new Comunidad("4","Balears Illes");
        Comunidad canarias = new Comunidad("5","Canarias");
        Comunidad cantabria = new Comunidad("6","Cantabria");
        Comunidad castillaLeon = new Comunidad("7","Castilla y León");
        Comunidad castillaMancha = new Comunidad("8","Castilla-La Mancha");
        Comunidad catalunya = new Comunidad("9","Catalunya");
        Comunidad valencia = new Comunidad("10","Comunitat Valenciana");
        Comunidad extremadura = new Comunidad("11","Extremadura");
        Comunidad galicia = new Comunidad("12","Galicia");
        Comunidad madrid = new Comunidad("13","Madrid, Comunidad de");
        Comunidad murcia = new Comunidad("14","Murcia, Región de");
        Comunidad navarra = new Comunidad("15","Navarra, Comunidad Foral de");
        Comunidad paisVasco = new Comunidad("16","País Vasco");
        Comunidad rioja = new Comunidad("17","Rioja, La");
        Comunidad ceuta = new Comunidad("18","Ceuta");
        Comunidad melilla = new Comunidad("19","Melilla");

        comunidades.add(andalucia);
        comunidades.add(aragon);
        comunidades.add(asturias);
        comunidades.add(baleares);
        comunidades.add(canarias);
        comunidades.add(cantabria);
        comunidades.add(castillaLeon);
        comunidades.add(castillaMancha);
        comunidades.add(catalunya);
        comunidades.add(valencia);
        comunidades.add(extremadura);
        comunidades.add(galicia);
        comunidades.add(madrid);
        comunidades.add(murcia);
        comunidades.add(navarra);
        comunidades.add(paisVasco);
        comunidades.add(rioja);
        comunidades.add(ceuta);
        comunidades.add(melilla);

        return comunidades;
    }


    public ArrayList<String> obtenerIntereses(){

        ArrayList<String> intereses = new ArrayList<String>();

        intereses.add("DEPORTES");
        intereses.add("NATURALEZA");
        intereses.add("MONUMENTOS");
        intereses.add("FIESTA");

        return intereses;
    }

    /**
     *
     * @param id
     * @return
     * @throws ServidorPHPException
     */
    public Boolean eliminarAnuncio(String id) throws  ServidorPHPException{
        Boolean centinela = false;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("id",id);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urleliminaranuncio, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        centinela = true;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return centinela;
    }

    /**
     *
     * @param idanuncio
     * @return
     * @throws ServidorPHPException
     */
    public ArrayList<Propuesta> obtenerPropuestaIDA(String idanuncio)throws ServidorPHPException{
        ArrayList<Propuesta>propuestas = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("idanuncio", idanuncio);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerpropuestaIDA, parametros);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        for (int i = 0; i != mensaje.length(); i++){
                            String idguia = (mensaje.getJSONObject(i).getString("IDGuia"));
                            String Mensaje = (mensaje.getJSONObject(i).getString("Mensaje"));
                            String id = (mensaje.getJSONObject(i).getString("ID"));
                            String estado = (mensaje.getJSONObject(i).getString("Estado"));
                            Guia guia = obtenerGuiaID(idguia);
                            Propuesta p = new Propuesta(id,idguia,idanuncio,Mensaje, Estados.valueOf(estado),guia);
                            propuestas.add(p);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return propuestas;
    }

    /**
     *
     * @param idanuncio
     * @param idguia
     * @return
     * @throws ServidorPHPException
     */
    public ArrayList<Propuesta> obtenerPropuestaIDAIDGuia(String idanuncio,String idguia)throws ServidorPHPException{
        ArrayList<Propuesta>propuestas = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("idanuncio", idanuncio);
        parametros.put("idguia",idguia);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerpropuestaIDAIDGuia, parametros);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        for (int i = 0; i != mensaje.length(); i++){
                            String Mensaje = (mensaje.getJSONObject(i).getString("Mensaje"));
                            String id = (mensaje.getJSONObject(i).getString("ID"));
                            String estado = (mensaje.getJSONObject(i).getString("Estado"));
                            Guia guia = obtenerGuiaID(idguia);
                            Propuesta p = new Propuesta(id,idguia,idanuncio,Mensaje,Estados.valueOf(estado),guia);
                            propuestas.add(p);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return propuestas;
    }
    public ArrayList<Propuesta> obtenerPropuestaIDAIDGuia2(String idanuncio,String idguia)throws ServidorPHPException{
        ArrayList<Propuesta>propuestas = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("idanuncio", idanuncio);
        parametros.put("idguia",idguia);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerpropuestaIDA, parametros);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        for (int i = 0; i != mensaje.length(); i++){
                            String Mensaje = (mensaje.getJSONObject(i).getString("Mensaje"));
                            String id = (mensaje.getJSONObject(i).getString("ID"));
                            String estado = (mensaje.getJSONObject(i).getString("Estado"));
                            Guia guia = obtenerGuiaID(idguia);
                            Propuesta p = new Propuesta(id,idguia,idanuncio,Mensaje,Estados.valueOf(estado),guia);
                            propuestas.add(p);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return propuestas;
    }

    /**
     *
     * @param idguia
     * @return
     * @throws ServidorPHPException
     */
    public ArrayList<Propuesta> obtenerPropuestaIDGuia(String idguia)throws ServidorPHPException{
        ArrayList<Propuesta>propuestas = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("idguia", idguia);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerpropuestaIDGuia, parametros);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        for (int i = 0; i != mensaje.length(); i++){
                            String idanuncio = (mensaje.getJSONObject(i).getString("IDAnuncio"));
                            String Mensaje = (mensaje.getJSONObject(i).getString("Mensaje"));
                            String id = (mensaje.getJSONObject(i).getString("ID"));
                            String estado = (mensaje.getJSONObject(i).getString("Estado"));
                            Guia guia = obtenerGuiaID(idguia);
                            Propuesta p = new Propuesta(id,idguia,idanuncio,Mensaje, Estados.valueOf(estado),guia);
                            propuestas.add(p);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return propuestas;
    }

    /**
     *
     * @param provincia
     * @return
     * @throws ServidorPHPException
     */
    public ArrayList<Anuncio>obtenerAnunciosProvincia(String provincia) throws  ServidorPHPException{
        ArrayList<Anuncio>anuncios = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("provincia",provincia);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerAnunciosProvincia, parametros);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = new GregorianCalendar();
                        for (int i = 0; i != mensaje.length(); i++){
                            String id = (mensaje.getJSONObject(i).getString("ID"));
                            String NombreViaje = (mensaje.getJSONObject(i).getString("NombreViaje"));
                            String Acompanantes = (mensaje.getJSONObject(i).getString("Acompanantes"));
                            String FechaCreacion = (mensaje.getJSONObject(i).getString("FechaCreacion"));
                            String FechaInicio = (mensaje.getJSONObject(i).getString("FechaInicio"));
                            String FechaFin = (mensaje.getJSONObject(i).getString("FechaFin"));
                            String Mensaje = (mensaje.getJSONObject(i).getString("Mensaje"));
                            String nombreCiudad = (mensaje.getJSONObject(i).getString("ciudad"));
                            String nombreInteres = (mensaje.getJSONObject(i).getString("interes"));
                            String token = (mensaje.getJSONObject(i).getString("token"));
                            String nombreprovincia = (mensaje.getJSONObject(i).getString("nombreprovincia"));
                            String estado = (mensaje.getJSONObject(i).getString("Estado"));
                            Turista turista = obtenerTurista(token);
                            Anuncio a = new Anuncio(id,token,NombreViaje,nombreCiudad,nombreprovincia,Intereses.valueOf(nombreInteres),Acompanantes,formato.parse(FechaCreacion),formato.parse(FechaInicio),formato.parse(FechaFin),Mensaje,turista, Estados.valueOf(estado));
                            anuncios.add(a);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException | ParseException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return anuncios;
    }

    /**
     *
     * @param id
     * @return
     * @throws ServidorPHPException
     */
    public Anuncio obtenerAnunciosID(String id) throws  ServidorPHPException{
        ArrayList<Anuncio>anuncios = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("id",id);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerAnunciosid, parametros);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = new GregorianCalendar();
                        for (int i = 0; i != mensaje.length(); i++){
                            String NombreViaje = (mensaje.getJSONObject(i).getString("NombreViaje"));
                            String Acompanantes = (mensaje.getJSONObject(i).getString("Acompanantes"));
                            String FechaCreacion = (mensaje.getJSONObject(i).getString("FechaCreacion"));
                            String FechaInicio = (mensaje.getJSONObject(i).getString("FechaInicio"));
                            String FechaFin = (mensaje.getJSONObject(i).getString("FechaFin"));
                            String Mensaje = (mensaje.getJSONObject(i).getString("Mensaje"));
                            String nombreCiudad = (mensaje.getJSONObject(i).getString("ciudad"));
                            String nombreInteres = (mensaje.getJSONObject(i).getString("interes"));
                            String token = (mensaje.getJSONObject(i).getString("tokenturista"));
                            String nombreprovincia = (mensaje.getJSONObject(i).getString("nombreprovincia"));
                            String estado = (mensaje.getJSONObject(i).getString("Estado"));
                            Turista turista = obtenerTurista(token);
                            Anuncio a = new Anuncio(id,token,NombreViaje,nombreCiudad,nombreprovincia,Intereses.valueOf(nombreInteres),Acompanantes,formato.parse(FechaCreacion),formato.parse(FechaInicio),formato.parse(FechaFin),Mensaje,turista,Estados.valueOf(estado));
                            anuncios.add(a);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException | ParseException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return anuncios.get(0);
    }

    /**
     *
     * @param idguia
     * @param idanuncio
     * @param mensaje
     * @return
     * @throws ServidorPHPException
     */
    public Boolean insertarPropuesta(String idguia,String idanuncio,String mensaje,Estados estado) throws ServidorPHPException{
        Boolean centinela = false;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("idguia",idguia);
        parametros.put("idanuncio",idanuncio);
        parametros.put("mensaje",mensaje);
        parametros.put("estado",String.valueOf(estado));
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlregistrarPropuesta, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        centinela = true;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return centinela;
    }

    /**
     *
     * @param id
     * @return
     * @throws ServidorPHPException
     */
    public Boolean eliminarPropuesta(String id) throws  ServidorPHPException{
        Boolean centinela = false;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("id",id);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urleliminarPropuesta, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        centinela = true;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return centinela;
    }

    /**
     *
     * @param estado
     * @param idpropuesta
     * @return
     * @throws ServidorPHPException
     */
    public Boolean updateEstadoPropuesta(Estados estado,String idpropuesta) throws ServidorPHPException{
        Boolean centinela = false;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("estado",String.valueOf(estado));
        parametros.put("idpropuesta",idpropuesta);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlupdateestadopropuesta, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        centinela = true;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return centinela;
    }

    /**
     *
     * @param id
     * @return
     * @throws ServidorPHPException
     */
    public Propuesta obtenerPropuestaID(String id)throws ServidorPHPException{
        ArrayList<Propuesta>propuestas = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("id", id);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerpropuestaID, parametros);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        for (int i = 0; i != mensaje.length(); i++){
                            String Mensaje = (mensaje.getJSONObject(i).getString("Mensaje"));
                            String idguia = (mensaje.getJSONObject(i).getString("IDGuia"));
                            String idanuncio = (mensaje.getJSONObject(i).getString("IDAnuncio"));
                            String estado = (mensaje.getJSONObject(i).getString("Estado"));
                            Guia guia = obtenerGuiaID(idguia);
                            Propuesta p = new Propuesta(id,idguia,idanuncio,Mensaje,Estados.valueOf(estado),guia);
                            propuestas.add(p);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return propuestas.get(0);
    }

    /**
     *
     * @return
     * @throws ServidorPHPException
     */
    public ArrayList<Propuesta> obtenerPropuestas()throws ServidorPHPException{
        ArrayList<Propuesta>propuestas = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerpropuestas, parametros);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        for (int i = 0; i != mensaje.length(); i++){
                            String Mensaje = (mensaje.getJSONObject(i).getString("Mensaje"));
                            String id = (mensaje.getJSONObject(i).getString("ID"));
                            String idguia = (mensaje.getJSONObject(i).getString("IDGuia"));
                            String idanuncio = (mensaje.getJSONObject(i).getString("IDAnuncio"));
                            String estado = (mensaje.getJSONObject(i).getString("Estado"));
                            Guia guia = obtenerGuiaID(idguia);
                            Propuesta p = new Propuesta(id,idguia,idanuncio,Mensaje,Estados.valueOf(estado),guia);
                            propuestas.add(p);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return propuestas;
    }

    /**
     *
     * @param estado
     * @param idanuncio
     * @return
     * @throws ServidorPHPException
     */
    public Boolean updateEstadoAnuncio(Estados estado,String idanuncio) throws ServidorPHPException{
        Boolean centinela = false;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("estado",String.valueOf(estado));
        parametros.put("idanuncio",idanuncio);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlupdateestadoanuncio, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        centinela = true;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return centinela;
    }

    /**
     *
     * @param idanuncio
     * @param idguia
     * @return
     * @throws ServidorPHPException
     */
    public Boolean updateIDGuiaAnuncio(String idguia,String idanuncio) throws ServidorPHPException{
        Boolean centinela = false;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("idguia",idguia);
        parametros.put("idanuncio",idanuncio);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlupdateidguiaanuncio, parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        centinela = true;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return centinela;
    }

    /**
     *
     * @param idguia
     * @return
     * @throws ServidorPHPException
     */
    public ArrayList<Anuncio> obtenerAnunciosIDGuia(String idguia) throws  ServidorPHPException{
        ArrayList<Anuncio>anuncios = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("idguia",idguia);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlobtenerAnunciosidguia, parametros);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = new GregorianCalendar();
                        for (int i = 0; i != mensaje.length(); i++){
                            String NombreViaje = (mensaje.getJSONObject(i).getString("NombreViaje"));
                            String id = (mensaje.getJSONObject(i).getString("ID"));
                            String Acompanantes = (mensaje.getJSONObject(i).getString("Acompanantes"));
                            String FechaCreacion = (mensaje.getJSONObject(i).getString("FechaCreacion"));
                            String FechaInicio = (mensaje.getJSONObject(i).getString("FechaInicio"));
                            String FechaFin = (mensaje.getJSONObject(i).getString("FechaFin"));
                            String Mensaje = (mensaje.getJSONObject(i).getString("Mensaje"));
                            String nombreCiudad = (mensaje.getJSONObject(i).getString("ciudad"));
                            String nombreInteres = (mensaje.getJSONObject(i).getString("interes"));
                            String token = (mensaje.getJSONObject(i).getString("tokenturista"));
                            String nombreprovincia = (mensaje.getJSONObject(i).getString("nombreprovincia"));
                            String estado = (mensaje.getJSONObject(i).getString("Estado"));
                            Turista turista = obtenerTurista(token);
                            Anuncio a = new Anuncio(id,token,NombreViaje,nombreCiudad,nombreprovincia,Intereses.valueOf(nombreInteres),Acompanantes,formato.parse(FechaCreacion),formato.parse(FechaInicio),formato.parse(FechaFin),Mensaje,turista,Estados.valueOf(estado));
                            anuncios.add(a);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException | ParseException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return anuncios;
    }

    /**
     *
     * @param mensaje
     * @return
     * @throws ServidorPHPException
     */
    public Boolean escribir(String mensaje) throws ServidorPHPException{
        Boolean centinela = false;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("mensaje",mensaje);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlfotos + "/registrar.php", parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        centinela = true;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return centinela;
    }

    /**
     *
     * @return
     * @throws ServidorPHPException
     */
    public ArrayList<String> leer() throws ServidorPHPException{
        ArrayList<String>obtencion = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlfotos + "/leer.php", parametros);
            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        for (int i = 0; i != mensaje.length(); i++){
                            obtencion.add(mensaje.getString(i));
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return obtencion;
    }

    /**
     *
     * @param mensaje
     * @return
     * @throws ServidorPHPException
     */
    public Boolean crearIncidencia(String mensaje) throws ServidorPHPException{
        Boolean centinela = false;
        JSONParser parser = new JSONParser();
        JSONArray datos;
        // Declaro el array de los parámetros
        HashMap<String, String> parametros = new HashMap<>();
        parametros.put("mensaje",mensaje);
        try{
            // Obtengo los datos de la persona del servidor
            datos = parser.getJSONArrayFromUrl(urlfotos + "/registrarincidencias.php", parametros);

            if( datos != null ){
                int resultadoobtenido = datos.getJSONObject(0).getInt("estado");

                switch(resultadoobtenido){
                    case RESULTADO_OK:
                        centinela = true;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }
            }
            else{
                System.out.println("Error obteniendo los datos del servidor.");
            }
        }
        catch (IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }
        return centinela;
    }

}
