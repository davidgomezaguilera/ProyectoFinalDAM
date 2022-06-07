package com.example.tourguide.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.tourguide.MainActivity;
import com.example.tourguide.R;
import com.example.tourguide.controladores.ControladorBDLocal;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.ObjectSerializer;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Comunidad;
import com.example.tourguide.modelo.Estados;
import com.example.tourguide.modelo.Guia;
import com.example.tourguide.modelo.Intereses;
import com.example.tourguide.modelo.Turista;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Button iniciarSesion, registrarseLogin, pswdOlvidada;
    private EditText etEmail,etPswd;
    private Switch swGuia;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private Boolean guia = false;
    private ControladorBaseDatos controlador;
    private ControladorBDLocal controladorBDLocal;
    private SQLiteDatabase dbLocal;
    private static ConnectivityManager conectividad;
    private static NetworkInfo conectividadInfo;

    private  Context contexto;

    public void iniciarSesionSinConexion(String email, String password){

        Turista t = controladorBDLocal.login(email,password);

        Guia g = controladorBDLocal.obtenerGuia(t.getToken());
                if (t!=null){
                    editor.putString("nombre", t.getNombre());
                    editor.putString("email", t.getEmail());
                    editor.putString("token",t.getToken());
                    editor.putString("apellidos", t.getApellidos());
                    editor.putString("provincia",g.getProvincia());
                    editor.putString("ciudad", g.getCiudad());
                    if(guia){
                        editor.putString("modo", "guia");
                    }else{
                        editor.putString("modo", "turista");
                    }
                    editor.commit();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{

                    //USUARIO NO ENCONTRADO
                }


    }


    public void iniciarSesion(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            try {
                                if(controlador.obtenerGuia(user.getUid())!= null){
                                    updateUI(user);
                                }else{
                                    Toasty.error(getBaseContext(), "Usuario y contraseña correctos, nuestros servidores estan sufriendo un mantenimiento vuelva a intentarlo de nuevo", Toast.LENGTH_SHORT, true).show();
                                }
                                if(controlador.obtenerTurista(user.getUid())!= null){
                                    updateUI(user);
                                }else{
                                    Toasty.error(getBaseContext(), "Usuario y contraseña correctos, nuestros servidores estan sufriendo un mantenimiento vuelva a intentarlo de nuevo", Toast.LENGTH_SHORT, true).show();
                                }
                            } catch (ServidorPHPException e) {
                                e.printStackTrace();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
    }

    public void obtenerDatosPerfil(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            if(guia) {
                try {
                    Guia g = controlador.obtenerGuia(user.getUid());
                    Turista t = controlador.obtenerTurista(user.getUid());
                    if (g!=null){
                        editor.putString("nombre", g.getNombre());
                        editor.putString("email", g.getEmail());
                        editor.putString("ciudad", g.getCiudad());
                        editor.putString("apellidos", g.getApellido());
                        editor.putString("token",g.getToken());
                        editor.putString("provincia",g.getProvincia());
                        editor.putString("modo", "guia");
                        editor.commit();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        editor.putString("nombre", t.getNombre());
                        editor.putString("email", t.getEmail());
                        editor.putString("token",t.getToken());
                        editor.putString("apellidos", t.getApellidos());
                        editor.putString("ciudad", g.getCiudad());
                        editor.putString("provincia",g.getProvincia());
                        editor.putString("modo", "turista");
                        editor.commit();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (ServidorPHPException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    Turista t = controlador.obtenerTurista(user.getUid());
                    Guia g = controlador.obtenerGuia(user.getUid());
                    if (t!=null){
                        editor.putString("nombre", t.getNombre());
                        editor.putString("email", t.getEmail());
                        editor.putString("token",t.getToken());
                        editor.putString("apellidos", t.getApellidos());
                        editor.putString("provincia",g.getProvincia());
                        editor.putString("ciudad", g.getCiudad());
                        editor.putString("modo", "turista");
                        editor.commit();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{

                        editor.putString("nombre", g.getNombre());
                        editor.putString("email", g.getEmail());
                        editor.putString("token",g.getToken());
                        editor.putString("ciudad", g.getCiudad());
                        editor.putString("apellidos", g.getApellido());
                        editor.putString("provincia",g.getProvincia());
                        editor.putString("modo", "guia");
                        editor.commit();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (ServidorPHPException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void updateUI(FirebaseUser user){
        if (user != null){
            obtenerDatosPerfil();
        }
        else if (user == null){
            //Si has agregado mal
            Toasty.error(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT, true).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        contexto = getApplicationContext();
        etEmail = findViewById(R.id.tfEmail);
        etPswd = findViewById(R.id.tfPswd);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        iniciarSesion = findViewById(R.id.bIniciarSesion);
        registrarseLogin = findViewById(R.id.bRegistrarseLogin);
        pswdOlvidada = findViewById(R.id.bPswdOlvidado);
        mAuth = FirebaseAuth.getInstance();
        swGuia = findViewById(R.id.swEresGuia);
        swGuia.setOnCheckedChangeListener(this);
        controlador = new ControladorBaseDatos();


        dbLocal = openOrCreateDatabase("TourGuide",MODE_PRIVATE,null);
        controladorBDLocal = new ControladorBDLocal(contexto);

        conectividad = (ConnectivityManager)getSystemService(contexto.CONNECTIVITY_SERVICE);
        conectividadInfo = conectividad.getActiveNetworkInfo();
        try {
            Toasty.error(contexto, "¿Entro en el login?", Toast.LENGTH_SHORT, true).show();
            System.out.println("ENTRO EN EL LOGIN TRAS HABER INSERTADO?");
            hayConexion();
            /*if(!Once.beenDone(Once.THIS_APP_INSTALL,"")){
                System.out.println("Es la primera vez que se instala la app?");
            }
            //RECORDAR EL USUARIO INICIADO ANTERIORMENTE
            if(prefs.getString("modo","").equals("turista")){
                if (controlador.obtenerTurista(controlador.obtenerTurista(prefs.getString("token","")).getToken())!=null){
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else if (prefs.getString("modo","").equals("guia")){
                if (controlador.obtenerTurista(controlador.obtenerGuia(prefs.getString("token","")).getToken())!=null){
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }*/
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        } catch( android.database.SQLException e){
            System.out.println(e.getMessage());
            System.out.println("estamos en el catch sqlException");
        }







        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etEmail.getText().toString().isEmpty() && !etPswd.getText().toString().isEmpty()){
                    if(conectividadInfo != null && conectividadInfo.isConnected()){
                        iniciarSesion(etEmail.getText().toString(),etPswd.getText().toString());
                    }else{
                        iniciarSesionSinConexion(etEmail.getText().toString(), etPswd.getText().toString());
                    }

                }else{
                    Toasty.error(contexto,"Los campos no pueden estar vacios",Toast.LENGTH_LONG).show();
                }

            }
        });
        registrarseLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), Registrarse.class);
                startActivity(intent);

            }
        });

    }
    public void hayConexion() throws ServidorPHPException, android.database.SQLException {
        //AQUI COMPROBAMOS SI EL USUARIO NO TIENE CONECTIVIDAD A INTERNET
        System.out.println("Que nos llega de conectividad "+ conectividadInfo);
        if(conectividadInfo != null && conectividadInfo.isConnected()){
            editor.putBoolean("conectividad",true);
            System.out.println("Tenemos conectividad");
        try{
            //CREANDO ANUNCIOS INSERTADOS SIN CONEXION
            ArrayList<Anuncio> anuncios = (ArrayList<Anuncio>) ObjectSerializer.deserialize(prefs.getString("anunciosInsertar",ObjectSerializer.serialize(new ArrayList<Anuncio>())));
            if(anuncios.size()!=0){

                for(Anuncio a : anuncios){
                    System.out.println("INSERTANDO ANUNCIO DE LA BD LOCAL "+ a.getNombre());
                    Calendar actual = new GregorianCalendar();
                    Calendar fecha1 = Calendar.getInstance();
                    Calendar fecha2 = Calendar.getInstance();
                    fecha1.setTime(a.getFecha1());
                    fecha2.setTime(a.getFecha2());
                    java.sql.Timestamp sqlFechaActual = new java.sql.Timestamp(actual.getTimeInMillis());
                    java.sql.Timestamp sqlf1 = new java.sql.Timestamp(fecha1.getTimeInMillis());
                    java.sql.Timestamp sqlf2 = new java.sql.Timestamp(fecha2.getTimeInMillis());


                    controlador.insertarAnuncio(a.getTokenTurista(),a.getCiudad(),a.getAcompanantes(), a.getTipo(),a.getNombre(),sqlFechaActual,sqlf1,sqlf2,a.getMensaje(), String.valueOf(a.getEstado().toString()));

                }
                editor.putString("anunciosInsertar",ObjectSerializer.serialize(new ArrayList<Anuncio>())).commit();
            }

        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }/*
*/

            //System.out.println(controlador.obtenerGuias());

            if(prefs.getBoolean("primeraVez",false)){
                //CREANDO BD LOCAL*/
                System.out.println("BORRANDO E INSERTANDO");
                controladorBDLocal.borrarTabla("guias");
                controladorBDLocal.borrarTabla("turistas");
                controladorBDLocal.borrarTabla("ciudades");
                controladorBDLocal.borrarTabla("tipos");
                controladorBDLocal.borrarTabla("anuncios");
                controladorBDLocal.borrarTabla("propuestas");
                controladorBDLocal.borrarTabla("comunidades");
                controladorBDLocal.borrarTabla("provincias");
                controladorBDLocal.insertarGuias(controlador.obtenerGuias());
                controladorBDLocal.insertarTuristas(controlador.obtenerTuristas());

                controladorBDLocal.insertarPropuestas(controlador.obtenerPropuestas());
                controladorBDLocal.insertarAnuncios(controlador.obtenerTodosAnuncios());
                controladorBDLocal.insertarComunidades(controlador.obtenerComunidades());
                controladorBDLocal.insertarIntereses(controlador.obtenerIntereses());
                controladorBDLocal.insertarProvincias(controlador.obtenerTodasProvincias());

                controladorBDLocal.insertarCiudades(controlador.obtenerTodasCiudades());
            }else {

                Toasty.success(contexto,"Bienvenido por primera vez a la aplicación", Toast.LENGTH_LONG).show();
                controladorBDLocal.insertarGuias(controlador.obtenerGuias());
                controladorBDLocal.insertarTuristas(controlador.obtenerTuristas());

                controladorBDLocal.insertarPropuestas(controlador.obtenerPropuestas());
                controladorBDLocal.insertarAnuncios(controlador.obtenerTodosAnuncios());
                controladorBDLocal.insertarComunidades(controlador.obtenerComunidades());
                controladorBDLocal.insertarIntereses(controlador.obtenerIntereses());
                controladorBDLocal.insertarProvincias(controlador.obtenerTodasProvincias());

                controladorBDLocal.insertarCiudades(controlador.obtenerTodasCiudades());
                editor.putBoolean("primeraVez",true).commit();
            }



                //controladorBDLocal.relacionarTablas();



            //controladorBDLocal.insertarGuia("200","PruebaInsertar","apellidoInsertar","tokenInsertar","505","insertar@gmail.com","5.0","505","foto");
            //controladorBDLocal.insertarGuia("214","PruebaInsertar","apellidoInsertar","tokenInsertar","505","insertar@gmail.com","5.0","505","foto");

            //controladorBDLocal.insertarTurista("100","tokenTuristaInsertar","nombreTuristaInsertar","apellTurInser","turiInser@gmail.com","0");
            //controladorBDLocal.insertarTurista("100","tokenTuristaInsertar","nombreTuristaInsertar","apellTurInser","turiInser@gmail.com","0");
            //controladorBDLocal.insertarTurista("102","tokenTuristaInsertar","nombreTuristaInsertar","apellTurInser","turiInser@gmail.com","0");
        }else{

            editor.putBoolean("conectividad", false).commit();
            controladorBDLocal.obtenerGuias();
            controladorBDLocal.obtenerTuristas();
            controladorBDLocal.obtenerProvincias();
            controladorBDLocal.obtenerPropuestas();
            controladorBDLocal.obtenerAnuncios();
            controladorBDLocal.obtenerCiudades();
            controladorBDLocal.obtenerComunidades();
            controladorBDLocal.obtenerIntereses();
            //editor.putBoolean("primeraVez",false).commit();
            editor.putBoolean("primeraVez",true).commit();
            //PRUEBAS//

            //System.out.println("NOMBRE GUIA CON TOKEN ecP5IIm4BKaG5VKLX41Tn6l4qNm2 = "+ controladorBDLocal.obtenerGuia("ecP5IIm4BKaG5VKLX41Tn6l4qNm2").getNombre());
            //System.out.println("NOMBRE GUIA CON ID 10 = "+ controladorBDLocal.obtenerGuiaID("10").getNombre());
            //System.out.println("NOMBRE TURISTA CON TOKEN asdfadsf = "+ controladorBDLocal.obtenerTurista("asdfadsf").getNombre());
            //controladorBDLocal.obtenerAnunciosToken("ha7QmfeocLRwiZaLji57t6dgYRw2");
            //System.out.println("IDPROPUESTA CON IDGUIA 9 "+ controladorBDLocal.obtenerPropuestaIDAnuncio("9").get(0).getId());
            //System.out.println("IDPROPUESTA CON IDAnuncio 7 e IDGuia 9 = "+ controladorBDLocal.obtenerPropuestaIDAnuncioIDGuia("7","9").get(0).getId());
            //System.out.println("IDPROPUESTA CON IDGuia 9 = "+ controladorBDLocal.obtenerPropuestaIDGuia("9").get(0).getId());
            //System.out.println("IDPROPUESTA CON ID 7 = "+ controladorBDLocal.obtenerPropuestaID("6").get(0).getEstado().toString());
            try{
                //controladorBDLocal.obtenerAnunciosToken("nKf1vuVsIvhAWrAfiZwOIlERoG92");
                //controladorBDLocal.obtenerAnunciosProvincia("Araba/Álava");
                //controladorBDLocal.obtenerAnunciosID(9);
                //controladorBDLocal.obtenerAnunciosIDGuia("10");

            }catch(Exception e){
                e.printStackTrace();
            }
            System.out.println("No tenemos conectividad");
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.swEresGuia:
                guia = isChecked;
                break;
        }
    }

    /**
     * Comprueba que ya haya iniciado sesion
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
}
