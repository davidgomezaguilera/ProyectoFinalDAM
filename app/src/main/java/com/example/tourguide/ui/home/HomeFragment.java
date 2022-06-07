package com.example.tourguide.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.example.tourguide.R;
import com.example.tourguide.adaptadores.AdaptadorMisViajes;
import com.example.tourguide.adaptadores.SpaceItemDecoration;
import com.example.tourguide.controladores.ControladorBDLocal;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.GifSizeFilter;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Estados;
import com.example.tourguide.modelo.Guia;
import com.example.tourguide.ui.Login;
import com.example.tourguide.ui.huella.Huellas;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements LocationListener {

    public static LinearLayout turistaLayout, guiaLayout;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private View root;
    private Context contexto;
    private ControladorBaseDatos controlador;
    private ControladorBDLocal controladorBDLocal;
    private RecyclerView recViewMisViajes, recViajesContratados;
    private AdaptadorMisViajes adaptador;
    private FlatDialog flatDialog;
    public static View vista;
    private Activity activity;
    private Button bHuella, bExploraHuella;
    private final int PERMISO_DE_FINE_LOCALIZACION = 1;
    private final int PERMISO_DE_WRITE = 2;
    private int PICK_IMAGE_REQUEST = 1;
    private String URL = ControladorBaseDatos.urlservidor + "/upload.php";
    private String KEY_IMAGEN = "foto";
    private String KEY_NOMBRE = "nombre";
    private Bitmap bitmap;
    private Location localizacion = null;
    private LinearLayout lnadaguia, lnadaturista, lrecguia, lrecturista;
    private Boolean permiso = false;


    public void guardarArchivo(String mensaje) {
        try {
            controlador.escribir(mensaje);
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }
    }


    private void permisolocalizacion() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISO_DE_FINE_LOCALIZACION);
        } else {
            LocationServices.getFusedLocationProviderClient(contexto).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    permisodatos();
                    permiso = true;
                }
            });
        }
    }

    private void permisodatos() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISO_DE_WRITE);
        }
    }

    /**
     * obtengo la imagen mejor
     */
    private void matissechooser() {
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .capture(false)
                .captureStrategy(
                        new CaptureStrategy(true, "com.example.tourguide.ui.home", "test"))
                .maxSelectable(9)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .setOnSelectedListener((uriList, pathList) -> {
                    Log.e("onSelected", "onSelected: pathList=" + pathList);
                })
                .showSingleMediaType(true)
                .originalEnable(true)
                .maxOriginalSize(10)
                .autoHideToolbarOnSingleTap(true)
                .setOnCheckedListener(isChecked -> {
                    Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                })
                .forResult(PICK_IMAGE_REQUEST);
    }

    /**
     * Entra una foto y la combierto en String
     * @param bmp
     * @return
     */
    public String getImagenString(Bitmap bmp) {
        ByteArrayOutputStream streamsalida = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, streamsalida);
        byte[] imagenbytes = streamsalida.toByteArray();
        String imagencodificada = Base64.encodeToString(imagenbytes, Base64.DEFAULT);
        return imagencodificada;
    }

    /**
     * Sube una imagen al servidor
     */
    public void subirImagen() {
        final ProgressDialog cargando = ProgressDialog.show(contexto, "subiendo...", "subiendo ");
        LocationManager locManager = (LocationManager) contexto.getSystemService(Context.LOCATION_SERVICE);
        if (permiso) {
            if (ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 2, this);
            if (locManager != null) {
                localizacion = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        StringRequest srequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                cargando.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cargando.dismiss();
                Toast.makeText(contexto, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                int random = (int)Math.floor(Math.random()*200+1);
                String imagen = getImagenString(bitmap);
                String nombre = String.valueOf(random + prefs.getString("nombre","")) ;
                Map<String,String> params = new Hashtable<>();
                params.put(KEY_IMAGEN,imagen);
                params.put(KEY_NOMBRE,nombre.replace(" ",""));
                guardarArchivo(random + prefs.getString("nombre","").replace(" ","") + "/" +localizacion.getLongitude() + "/" + localizacion.getLatitude());
                return params;
            }
        };

        RequestQueue resquestqueue = Volley.newRequestQueue(contexto);
        resquestqueue.add(srequest);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        vista = root;
        activity = getActivity();
        contexto = getContext();
        turistaLayout = root.findViewById(R.id.layoutTurista);
        guiaLayout = root.findViewById(R.id.layoutGuia);
        lnadaguia = root.findViewById(R.id.lnadaguia);
        lnadaturista = root.findViewById(R.id.lnadaturista);
        lrecguia = root.findViewById(R.id.lrecguia);
        lrecturista = root.findViewById(R.id.lrecturista);
        prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
        editor = prefs.edit();
        controlador = new ControladorBaseDatos();
        controladorBDLocal = new ControladorBDLocal(contexto);
        permisolocalizacion();

        /*
                    APP PARA MODO TURISTA
         */
        if (prefs.getString("modo","").equals("turista")){
            turistaLayout.setVisibility(View.VISIBLE);
            guiaLayout.setVisibility(View.GONE);
            recViewMisViajes = root.findViewById(R.id.recViewMisViajes);
            bHuella = root.findViewById(R.id.bHuella);
            bExploraHuella = root.findViewById(R.id.bExplorar);
        }
        /*
                    APP PARA MODO GUIA
         */
        else{
            guiaLayout.setVisibility(View.VISIBLE);
            turistaLayout.setVisibility(View.GONE);
            recViajesContratados = root.findViewById(R.id.recViewViajesContratados);
        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        flatDialog = new FlatDialog(contexto);
        lnadaturista.setVisibility(View.GONE);
        lnadaguia.setVisibility(View.GONE);
        lrecturista.setVisibility(View.GONE);
        lrecguia.setVisibility(View.GONE);
        /*
                    APP PARA MODO TURISTA
         */
        if (prefs.getString("modo","").equals("turista")){
            turistaLayout.setVisibility(View.VISIBLE);
            guiaLayout.setVisibility(View.GONE);

            ArrayList<Anuncio> datos = null;
            Guia g;
            try {
                if(prefs.getBoolean("conectividad",false)){
                    System.out.println("IF TENEMOS CONECTIVIDAD HOMEFRAGMENT TURISTA");
                    g =controlador.obtenerGuia(prefs.getString("token",""));
                    datos = controlador.obtenerAnuncios(prefs.getString("token",""));
                }else{
                    System.out.println("ELSE NO TENEMOS CONECTIVIDAD HOMEFRAGMENT TURISTA");
                    g = controladorBDLocal.obtenerGuia(prefs.getString("token",""));
                    datos = controladorBDLocal.obtenerAnunciosToken(prefs.getString("token",""));
                }

                ArrayList<Anuncio> datosVerdaderos = new ArrayList<>();
                for (int i = 0; i!= datos.size();i++){
                    if(datos.get(i).getEstado().equals(Estados.Contratado)){
                        datosVerdaderos.add(datos.get(i));
                    }
                }
                // Hay que crear en la carpeta values un fichero dimens.xml y crear ahí list_space
                recViewMisViajes.addItemDecoration(new SpaceItemDecoration(contexto, R.dimen.list_space, true, true));
                // Con esto el tamaño del recyclerwiew no cambiará
                recViewMisViajes.setHasFixedSize(true);
                // Creo un layoutmanager para el recyclerview
                recViewMisViajes.setLayoutManager(new LinearLayoutManager(contexto,LinearLayoutManager.HORIZONTAL, false));
                adaptador = new AdaptadorMisViajes(contexto, datosVerdaderos,"turista");
                recViewMisViajes.setAdapter(adaptador);
                adaptador.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = recViewMisViajes.getChildAdapterPosition(v);
                        editor.putString("idanuncio",datosVerdaderos.get(pos).getId()).commit();
                        try {
                            editor.putString("propuesta",controlador.obtenerPropuestaIDAIDGuia2(datosVerdaderos.get(pos).getId(),g.getId()).get(0).getId()).commit();
                        } catch (ServidorPHPException e) {
                            e.printStackTrace();
                        }
                        flatDialog.setBackgroundColor(getResources().getColor(R.color.background_light))
                                .setFirstButtonColor(getResources().getColor(R.color.colorAccentTurista))
                                .setFirstButtonTextColor(getResources().getColor(R.color.negro))
                                .setFirstButtonText("Chat")
                                .setSecondButtonTextColor(getResources().getColor(R.color.colorAccentTurista))
                                .setSecondButtonTextColor(getResources().getColor(R.color.negro))
                                .setSecondButtonText("Propuesta")
                                .withFirstButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(prefs.getBoolean("conectividad",false)){
                                            Navigation.findNavController(HomeFragment.vista).navigate(R.id.nav_chat);
                                            flatDialog.dismiss();
                                        }else{
                                            Toasty.error(contexto, "Estás en modo sin conexión.", Toast.LENGTH_SHORT, true).show();
                                        }

                                    }
                                })
                                .withSecondButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Navigation.findNavController(HomeFragment.vista).navigate(R.id.nav_misanuncios_propuesta);
                                        flatDialog.dismiss();
                                    }
                                })
                                .show();

                    }
                });
                if(datosVerdaderos.size() > 0){
                    lrecturista.setVisibility(View.VISIBLE);
                    lnadaturista.setVisibility(View.GONE);
                }else{
                    lrecturista.setVisibility(View.GONE);
                    lnadaturista.setVisibility(View.VISIBLE);
                }
                adaptador.refrescar();
                bHuella.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(prefs.getBoolean("conectividad",false)){

                            matissechooser();
                            flatDialog.dismiss();
                        }else{
                            Toasty.error(contexto, "Estás en modo sin conexión.", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
                bExploraHuella.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Navigation.findNavController(HomeFragment.vista).navigate(R.id.nav_huella);
                        if(prefs.getBoolean("conectividad",false)){
                            Intent intent = new Intent(contexto, Huellas.class);
                            startActivity(intent);
                        }else{
                            Toasty.error(contexto, "Estás en modo sin conexión.", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
            } catch (ServidorPHPException e) {
                e.printStackTrace();
            }catch(ParseException e){
                e.printStackTrace();
            }


        }else{
            /* APP PARA EL GUIA */
            guiaLayout.setVisibility(View.VISIBLE);
            turistaLayout.setVisibility(View.GONE);

            ArrayList<Anuncio> datos = null;
            Guia g;
            try {
                if(prefs.getBoolean("conectividad",false)){
                    System.out.println("IF TENEMOS CONECTIVIDAD HOMEFRAGMENT GUIA");
                    g =controlador.obtenerGuia(prefs.getString("token",""));
                    datos = controlador.obtenerAnunciosIDGuia(g.getId());
                }else{
                    System.out.println("ELSE NO TENEMOS CONECTIVIDAD HOMEFRAGMENT GUIA");
                    g = controladorBDLocal.obtenerGuia(prefs.getString("token",""));
                    datos = controladorBDLocal.obtenerAnunciosIDGuia(g.getId());
                }
                ArrayList<Anuncio> datosVerdaderos = new ArrayList<>();
                for (int i = 0; i!= datos.size();i++){
                    if(datos.get(i).getEstado().equals(Estados.Contratado)){
                        datosVerdaderos.add(datos.get(i));
                    }
                }
                // Hay que crear en la carpeta values un fichero dimens.xml y crear ahí list_space
                recViajesContratados.addItemDecoration(new SpaceItemDecoration(contexto, R.dimen.list_space, true, true));
                // Con esto el tamaño del recyclerwiew no cambiará
                recViajesContratados.setHasFixedSize(true);
                // Creo un layoutmanager para el recyclerview
                recViajesContratados.setLayoutManager(new LinearLayoutManager(contexto,LinearLayoutManager.HORIZONTAL, false));
                adaptador = new AdaptadorMisViajes(contexto, datosVerdaderos,"guia");
                recViajesContratados.setAdapter(adaptador);
                adaptador.setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = recViajesContratados.getChildAdapterPosition(v);
                        editor.putString("idanuncio",datosVerdaderos.get(pos).getId()).commit();
                        try {
                            editor.putString("propuesta",controlador.obtenerPropuestaIDAIDGuia2(datosVerdaderos.get(pos).getId(),g.getId()).get(0).getId()).commit();
                        } catch (ServidorPHPException e) {
                            e.printStackTrace();
                        }
                        flatDialog.setBackgroundColor(getResources().getColor(R.color.background_light))
                                .setFirstButtonColor(getResources().getColor(R.color.colorAccentTurista))
                                .setFirstButtonTextColor(getResources().getColor(R.color.negro))
                                .setFirstButtonText("Chat")
                                .setSecondButtonColor(getResources().getColor(R.color.colorAccentTurista))
                                .setSecondButtonTextColor(getResources().getColor(R.color.negro))
                                .setSecondButtonText("Propuesta")
                                .withFirstButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(prefs.getBoolean("conectividad",false)){
                                            Navigation.findNavController(HomeFragment.vista).navigate(R.id.nav_chat);
                                            flatDialog.dismiss();
                                        }else{
                                            Toasty.error(contexto, "Estás en modo sin conexión.", Toast.LENGTH_SHORT, true).show();
                                        }

                                    }
                                })
                                .withSecondButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Navigation.findNavController(HomeFragment.vista).navigate(R.id.nav_misanuncios_propuesta);
                                        flatDialog.dismiss();
                                    }
                                })
                                .show();

                    }
                });
                if(datosVerdaderos.size()>0){
                    lnadaguia.setVisibility(View.GONE);
                    lrecguia.setVisibility(View.VISIBLE);
                }else{
                    lrecguia.setVisibility(View.GONE);
                    lnadaguia.setVisibility(View.VISIBLE);
                }
                adaptador.refrescar();
            } catch (ServidorPHPException e) {
                e.printStackTrace();
            }catch(ParseException e){
                e.printStackTrace();
            }


        }


    }

    @Override
    public void onResume() {
        super.onResume();

        flatDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog,
                                 int keyCode, android.view.KeyEvent event) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    // To dismiss the fragment when the back-button is pressed.
                    flatDialog.dismiss();
                    return true;
                }
                // Otherwise, do nothing else
                else return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri filePath = data.getData();
            //Cómo obtener el mapa de bits de la Galería
            int total = Matisse.obtainResult(data).size();
            for (int i = 0; i!= total;i++){
                try {
                    Bitmap bitmapmal = MediaStore.Images.Media.getBitmap(contexto.getContentResolver(), Matisse.obtainResult(data).get(i));
                    bitmap = Bitmap.createScaledBitmap(bitmapmal,(bitmapmal.getWidth()/10),(bitmapmal.getHeight()/10),true);
                    subirImagen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Configuración del mapa de bits en ImageView
            //Aqui poner lo que tiene que hacer cuando esoja la imagen

            //iv.setImageBitmap(bitmap);
        }else if(requestCode == PICK_IMAGE_REQUEST && resultCode != RESULT_OK){

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISO_DE_FINE_LOCALIZACION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // aqui ya tengo permisos
                } else {

                }
                return;
            }
            case PERMISO_DE_WRITE:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // aqui ya tengo permisos
                } else {
                    // aqui no tengo permisos
                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        localizacion = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
