package com.example.tourguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.bumptech.glide.request.RequestOptions;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.GlideApp;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.ui.Login;
import com.example.tourguide.ui.home.HomeFragment;
import com.example.tourguide.ui.misanuncios.MisAnuncios_Anuncio;
import com.google.android.material.navigation.NavigationView;

//import java.awt.Button;

//import javax.swing.text.View;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private Button bmodo,bcerrarSesion;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View hView;
    private TextView tvNombreUsuario;
    private ImageButton iUsuario;
    private ConstraintLayout lDrawer;
    private ControladorBaseDatos controlador;
    private static View vista;

    //actualizamos los temas segun si el usuario es guia o turista
    public void updateUI(){
        if(prefs.getString("modo","").equals("guia")){
            /*       Parte de guia           */
            bmodo.setText(getResources().getString(R.string.modoturista));
            bmodo.setBackground(getResources().getDrawable(R.drawable.bordebotonesguia));
            bcerrarSesion.setBackground(getResources().getDrawable(R.drawable.bordebotonesguia));
            lDrawer.setBackground(getResources().getDrawable(R.drawable.side_nav_bar_guia));
            navigationView.getMenu().setGroupVisible(R.id.grupoguia, true);
            navigationView.getMenu().setGroupVisible(R.id.grupoturista, false);
            //hView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark)); Cambia el color
        }else if (prefs.getString("modo","").equals("turista")){
            /*      Parte de turista        */
            bmodo.setText(getResources().getString(R.string.modoguia));
            bmodo.setBackground(getResources().getDrawable(R.drawable.bordebotonesturista));
            bcerrarSesion.setBackground(getResources().getDrawable(R.drawable.bordebotonesturista));
            lDrawer.setBackground(getResources().getDrawable(R.drawable.side_nav_bar_turista));
            navigationView.getMenu().setGroupVisible(R.id.grupoturista, true);
            navigationView.getMenu().setGroupVisible(R.id.grupoguia, false);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        vista = getCurrentFocus();
        controlador = new ControladorBaseDatos();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        switch (prefs.getString("modo","")){
            case "turista":
                setTheme(R.style.EstiloTuristaApp);
                break;
            case "guia":
                setTheme(R.style.EstiloGuiaApp);
                break;
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        editor = prefs.edit();
        navigationView = findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);
        lDrawer = hView.findViewById(R.id.lDrawer);
        tvNombreUsuario = hView.findViewById(R.id.tvNombreUsuario);
        iUsuario = hView.findViewById(R.id.iUsuario);
        try {
            GlideApp.with(this)
                    .load("http://192.168.56.1/TourGuide/fotos/"+controlador.obtenerGuia(prefs.getString("token","")).getFoto()+".jpg")
                    .error(R.drawable.iconoguiaestandar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(iUsuario);
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }
        tvNombreUsuario.setText(prefs.getString("nombre","") + " " +prefs.getString("apellidos",""));
        //get the menu from the navigation view
        Menu menu=navigationView.getMenu();
        //get switch view
        bcerrarSesion = MenuItemCompat.getActionView(menu.findItem(R.id.sw)).findViewById(R.id.bcerrarSesion);
        bmodo = MenuItemCompat.getActionView(menu.findItem(R.id.sw)).findViewById(R.id.swmodo);
        updateUI();
        bcerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //limpiamos las Prefs y mandamos al login
                //editor.clear().commit();
                //editor.putBoolean("primeraVez",true).commit();
                Intent intent = new Intent(getBaseContext(), Login.class);
                startActivity(intent);
                finish();
                finish();
            }
        });
        bmodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefs.getString("modo","").equals("turista")){
                    editor.putString("modo","guia").commit();
                    updateUI();
                    HomeFragment.turistaLayout.setVisibility(View.GONE);
                    HomeFragment.guiaLayout.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(intent);
                }else if (prefs.getString("modo","").equals("guia")){
                    editor.putString("modo","turista").commit();
                    updateUI();
                    HomeFragment.guiaLayout.setVisibility(View.GONE);
                    HomeFragment.turistaLayout.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        iUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(MainActivity.vista).navigate(R.id.nav_home);
            }
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Cambia el comportamiento de ir atras
     */
    /*@Override public void onBackPressed() {
        System.out.println("Quieres volver amigo?");
    }*/
}
