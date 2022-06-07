package com.example.tourguide.ui.misviajes;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.example.tourguide.R;
import com.example.tourguide.adaptadores.AdaptadorMisAnuncios;
import com.example.tourguide.adaptadores.SpaceItemDecoration;
import com.example.tourguide.controladores.ControladorBDLocal;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Estados;
import com.example.tourguide.modelo.Guia;
import com.example.tourguide.modelo.Propuesta;

import java.text.ParseException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MisViajes extends Fragment {

    private RecyclerView recViewMisViajes;
    private Context contexto;
    private ControladorBaseDatos controlador;
    private ControladorBDLocal controladorBDLocal;
    private SharedPreferences prefs;
    public static View vista;
    private SharedPreferences.Editor editor;
    private AdaptadorMisAnuncios adaptador;
    private FlatDialog flatDialog;
    private LinearLayout lRecicler;
    private ConstraintLayout lNada;
    private TextView tvNada;


    public static MisViajes newInstance() {
        return new MisViajes();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.mis_viajes_fragment, container, false);
        vista = root;
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();
        controlador = new ControladorBaseDatos();
        contexto = getContext();
        controladorBDLocal = new ControladorBDLocal(contexto);
        lNada = root.findViewById(R.id.layoutnada);
        lRecicler = root.findViewById(R.id.layoutRecicler);
        tvNada = root.findViewById(R.id.tvNadaViajes);
        recViewMisViajes = root.findViewById(R.id.recViewMisViajes);
        flatDialog = new FlatDialog(contexto);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        ArrayList<Anuncio> datos = null;
        ArrayList<Propuesta> propuestas= null;
        Guia g;
        try {
            if(prefs.getString("modo","").equals("turista")) {

                /*  Modo turista */
                lRecicler.setVisibility(View.GONE);
                lNada.setVisibility(View.GONE);

                if(prefs.getBoolean("conectividad",false)){
                    g = controlador.obtenerGuia(prefs.getString("token", ""));
                    datos = controlador.obtenerAnuncios(prefs.getString("token", ""));

                }else{
                    g = controladorBDLocal.obtenerGuia(prefs.getString("token",""));
                    datos = controladorBDLocal.obtenerAnunciosToken(prefs.getString("token",""));
                }

                ArrayList<Anuncio> datosVerdaderos = new ArrayList<>();
                for (int i = 0; i != datos.size(); i++) {
                    if (datos.get(i).getEstado().equals(Estados.Contratado)) {
                        datosVerdaderos.add(datos.get(i));
                    }
                }
                if(datosVerdaderos.size()>0){
                    lRecicler.setVisibility(View.VISIBLE);
                    lNada.setVisibility(View.GONE);
                    // Hay que crear en la carpeta values un fichero dimens.xml y crear ahí list_space
                    recViewMisViajes.addItemDecoration(new SpaceItemDecoration(contexto, R.dimen.list_space, true, true));
                    // Con esto el tamaño del recyclerwiew no cambiará
                    recViewMisViajes.setHasFixedSize(true);
                    // Creo un layoutmanager para el recyclerview
                    recViewMisViajes.setLayoutManager(new LinearLayoutManager(contexto, LinearLayoutManager.VERTICAL, false));
                    adaptador = new AdaptadorMisAnuncios(contexto, datosVerdaderos, "turista");
                    recViewMisViajes.setAdapter(adaptador);
                    adaptador.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = recViewMisViajes.getChildAdapterPosition(v);
                            editor.putString("idanuncio", datosVerdaderos.get(pos).getId()).commit();
                            try {
                                editor.putString("propuesta", controlador.obtenerPropuestaIDAIDGuia2(datosVerdaderos.get(pos).getId(), g.getId()).get(0).getId()).commit();
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
                                            Navigation.findNavController(MisViajes.vista).navigate(R.id.nav_chat);
                                            flatDialog.dismiss();
                                        }
                                    })
                                    .withSecondButtonListner(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Navigation.findNavController(MisViajes.vista).navigate(R.id.nav_misanuncios_propuesta);
                                            flatDialog.dismiss();
                                        }
                                    })
                                    .show();

                        }
                    });
                    adaptador.refrescar();
                }else{
                    lNada.setVisibility(View.VISIBLE);
                    lRecicler.setVisibility(View.GONE);
                    tvNada.setText(R.string.nadaquemostrarviajesturista);
                }
            }else{
                /*  Modo Guia */
                if(prefs.getBoolean("conectividad",false)){
                    g = controlador.obtenerGuia(prefs.getString("token", ""));
                    datos = controlador.obtenerAnunciosIDGuia(g.getId());
                }else{
                    g = controladorBDLocal.obtenerGuia(prefs.getString("token",""));
                    datos = controladorBDLocal.obtenerAnunciosIDGuia(g.getId());
                }

                ArrayList<Anuncio> datosVerdaderos = new ArrayList<>();
                for (int i = 0; i != datos.size(); i++) {
                    if (datos.get(i).getEstado().equals(Estados.Contratado)) {
                        datosVerdaderos.add(datos.get(i));
                    }
                }
                if(datosVerdaderos.size()>0){
                    // Hay que crear en la carpeta values un fichero dimens.xml y crear ahí list_space
                    recViewMisViajes.addItemDecoration(new SpaceItemDecoration(contexto, R.dimen.list_space, true, true));
                    // Con esto el tamaño del recyclerwiew no cambiará
                    recViewMisViajes.setHasFixedSize(true);
                    // Creo un layoutmanager para el recyclerview
                    recViewMisViajes.setLayoutManager(new LinearLayoutManager(contexto, LinearLayoutManager.VERTICAL, false));
                    adaptador = new AdaptadorMisAnuncios(contexto, datosVerdaderos, "guiah");
                    recViewMisViajes.setAdapter(adaptador);
                    adaptador.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                int pos = recViewMisViajes.getChildAdapterPosition(v);
                                editor.putString("idanuncio", datosVerdaderos.get(pos).getId()).commit();
                                try {
                                    if (!prefs.getBoolean("conectividad", false)) {
                                        editor.putString("propuesta",controladorBDLocal.obtenerPropuestaIDAnuncioIDGuia(datosVerdaderos.get(pos).getId(), g.getId()).get(0).getId()).commit();
                                    } else {
                                        editor.putString("propuesta", controlador.obtenerPropuestaIDAIDGuia(datosVerdaderos.get(pos).getId(), g.getId()).get(0).getId()).commit();
                                    }
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
                                                Navigation.findNavController(MisViajes.vista).navigate(R.id.nav_chat);
                                                flatDialog.dismiss();
                                            }
                                        })
                                        .withSecondButtonListner(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Navigation.findNavController(MisViajes.vista).navigate(R.id.nav_misanuncios_propuesta);
                                                flatDialog.dismiss();
                                            }
                                        })
                                        .show();

                            }

                    });
                    adaptador.refrescar();
                    lNada.setVisibility(View.GONE);
                    lRecicler.setVisibility(View.VISIBLE);
                }else{
                    lNada.setVisibility(View.VISIBLE);
                    lRecicler.setVisibility(View.GONE);
                    tvNada.setText(R.string.nadaquemostrarviajesguia);
                }
            }
            } catch (ServidorPHPException e) {
                e.printStackTrace();
            } catch( ParseException e){
                e.printStackTrace();
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

}
