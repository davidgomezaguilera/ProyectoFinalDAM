package com.example.tourguide.ui.huella;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.example.tourguide.R;
import com.example.tourguide.controladores.BaseDemoActivity;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.MultiDrawable;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Persona;
import com.example.tourguide.ui.home.HomeFragment;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class Huellas extends BaseDemoActivity implements ClusterManager.OnClusterClickListener<Persona>, ClusterManager.OnClusterInfoWindowClickListener<Persona>, ClusterManager.OnClusterItemClickListener<Persona>, ClusterManager.OnClusterItemInfoWindowClickListener<Persona> {

    private ClusterManager<Persona> mClusterManager;
    private Random mRandom = new Random(1984);
    private ControladorBaseDatos controlador;
    private FlatDialog flatDialog;

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    public class PersonRenderer extends DefaultClusterRenderer<Persona> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private int mDimension;

        public PersonRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);
            View multiProfile = getLayoutInflater().inflate(R.layout.perfiles, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(Persona person, MarkerOptions markerOptions) {
            // Draw a single person - show their profile photo and set the info window to show their name
            markerOptions
                    .icon(getItemIcon(person))
                    .title(person.name);
        }

        @Override
        protected void onClusterItemUpdated(Persona person, Marker marker) {
            // Same implementation as onBeforeClusterItemRendered() (to update cached markers)
            marker.setIcon(getItemIcon(person));
            marker.setTitle(person.name);
        }

        /**
         * Get a descriptor for a single person (i.e., a marker outside a cluster) from their
         * profile photo to be used for a marker icon
         *
         * @param person person to return an BitmapDescriptor for
         * @return the person's profile photo as a BitmapDescriptor
         */
        private BitmapDescriptor getItemIcon(Persona person) {
            mImageView.setImageBitmap(person.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            return BitmapDescriptorFactory.fromBitmap(icon);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Persona> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            markerOptions.icon(getClusterIcon(cluster));
        }

        @Override
        protected void onClusterUpdated(Cluster<Persona> cluster, Marker marker) {
            // Same implementation as onBeforeClusterRendered() (to update cached markers)
            marker.setIcon(getClusterIcon(cluster));
        }

        /**
         * Get a descriptor for multiple people (a cluster) to be used for a marker icon. Note: this
         * method runs on the UI thread. Don't spend too much time in here (like in this example).
         *
         * @param cluster cluster to draw a BitmapDescriptor for
         * @return a BitmapDescriptor representing a cluster
         */
        private BitmapDescriptor getClusterIcon(Cluster<Persona> cluster) {
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Persona p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = new BitmapDrawable(getResources(), p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            return BitmapDescriptorFactory.fromBitmap(icon);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<Persona> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Persona> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    /**
     * Muesta la foto seleccionada
     * @param photoUri
     */
    private void showPhoto(Uri photoUri){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(photoUri, "image/*");
        startActivity(intent);
    }

    /**
     * Añade un reporte
     * @param mensaje
     */
    public void guardarArchivo(String mensaje) {
        try {
            controlador = new ControladorBaseDatos();
            controlador.crearIncidencia(mensaje);
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }
    }

    /**
     * Este es para cuando clicas en las foto
     * @param item
     * @return
     */
    @Override
    public boolean onClusterItemClick(Persona item) {
        // Does nothing, but you could go into the user's profile page, for example.
        System.out.println("click");
        System.out.println(item.name);
        flatDialog = new FlatDialog(this);
        flatDialog.setBackgroundColor(getResources().getColor(R.color.background_light))
                .setFirstButtonColor(getResources().getColor(R.color.colorAccentTurista))
                .setFirstButtonTextColor(getResources().getColor(R.color.negro))
                .setFirstButtonText("Ver foto")
                .setSecondButtonText("Reportar foto")
                .setSecondButtonTextColor(getResources().getColor(R.color.negro))
                .setSecondButtonColor(getResources().getColor(R.color.rojo))
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPhoto(Uri.parse("http://apptourguide.ddns.net/TourGuide/fotos/mapa/"+item.name+".png"));
                        flatDialog.dismiss();
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        guardarArchivo(item.name);
                        Toasty.success(getBaseContext(),"Reporte enviado correctamente, en breve su caso será revisado",Toast.LENGTH_LONG).show();
                        flatDialog.dismiss();
                    }
                })
                .show();

        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Persona item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }

    @Override
    protected void startDemo(boolean isRestore) {
        LocationManager locManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        Location localizacion = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        getMap().setMyLocationEnabled(true);
        if (!isRestore) {
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(localizacion.getLatitude(), localizacion.getLongitude()), 9.5f));
        }

        mClusterManager = new ClusterManager<Persona>(this, getMap());
        mClusterManager.setRenderer(new PersonRenderer());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        mClusterManager.cluster();
    }
    private void cargarfoto() {
        URL imageUrl = null;
        try {
            controlador = new ControladorBaseDatos();
            try {
                ArrayList<String>resultado = controlador.leer();
                for(int i=0 ; i!=resultado.size(); i++){
                    List<String>datitos = Arrays.asList(resultado.get(i).split("/"));
                    if(datitos.size()==3){;
                        imageUrl = new URL(ControladorBaseDatos.urlservidor+"/fotos/mapa/"+datitos.get(0)+".png");
                        HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                        conn.connect();
                        mClusterManager.addItem(new Persona(position(datitos.get(1),datitos.get(2)), datitos.get(0),BitmapFactory.decodeStream(conn.getInputStream())));
                        obtenerImagenServidor(datitos.get(0));
                    }
                }
            } catch (ServidorPHPException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error cargando la imagen: "+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void obtenerImagenServidor(String foto){
        ImageRequest imgreq = new ImageRequest(ControladorBaseDatos.urlservidor+"/fotos/mapa/"+foto+".png", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getBaseContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItems() {
        cargarfoto();
    }

    private LatLng position(String longitud, String latitud) {
        return new LatLng(Double.valueOf(latitud), Double.valueOf(longitud));
    }
}