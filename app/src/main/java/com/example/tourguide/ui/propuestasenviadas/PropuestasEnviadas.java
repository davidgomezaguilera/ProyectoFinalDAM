package com.example.tourguide.ui.propuestasenviadas;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.example.tourguide.R;
import com.example.tourguide.adaptadores.AdaptadorMisViajes;
import com.example.tourguide.adaptadores.AdaptadorPropuestasGuia;
import com.example.tourguide.adaptadores.AdaptadorPropuestasTurista;
import com.example.tourguide.adaptadores.SpaceItemDecoration;
import com.example.tourguide.controladores.ControladorBDLocal;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Estados;
import com.example.tourguide.modelo.Propuesta;
import com.example.tourguide.ui.buscaranuncio.BuscarAnuncios;

import java.util.ArrayList;

public class PropuestasEnviadas extends Fragment {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    public static View vista;
    private Context contexto;
    private Activity activity;
    private ControladorBaseDatos controlador;
    private ControladorBDLocal controladorBDLocal;
    private RecyclerView recViewMisPropuestas;
    private ArrayList<Propuesta> datos;
    private FlatDialog flatDialog;
    private LinearLayout lrecicler;
    private ConstraintLayout lnada;

    public static PropuestasEnviadas newInstance() {
        return new PropuestasEnviadas();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.propuestas_enviadas_fragment, container, false);
        contexto = getContext();
        activity = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
        editor = prefs.edit();
        controlador = new ControladorBaseDatos();
        controladorBDLocal = new ControladorBDLocal(contexto);
        vista = root;
        flatDialog = new FlatDialog(contexto);

        lnada = root.findViewById(R.id.layoutNada);
        lrecicler = root.findViewById(R.id.layoutRecicler);
        recViewMisPropuestas = root.findViewById(R.id.recViewMisPropuestas);


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        lrecicler.setVisibility(View.GONE);
        lnada.setVisibility(View.GONE);
        try {
            if(prefs.getBoolean("conectividad", false)){
                String idGuia = (controlador.obtenerGuia(prefs.getString("token","")).getId());
                datos = controlador.obtenerPropuestaIDGuia(idGuia);

            }else{
                String idGuia =(controladorBDLocal.obtenerGuia(prefs.getString("token","")).getId());
                datos = controladorBDLocal.obtenerPropuestaIDGuia(idGuia);
            }
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }
        ArrayList<Propuesta> propuestas =  new ArrayList<>();
        for (int i=0;i!=datos.size();i++){
            if(!datos.get(i).getEstado().equals(Estados.Contratado)){
                propuestas.add(datos.get(i));
            }
        }
        if(propuestas.size()>0){
            lrecicler.setVisibility(View.VISIBLE);
            lnada.setVisibility(View.GONE);
            // Hay que crear en la carpeta values un fichero dimens.xml y crear ahí list_space
            recViewMisPropuestas.addItemDecoration(new SpaceItemDecoration(contexto, R.dimen.list_space, true, true));
            // Con esto el tamaño del recyclerwiew no cambiará
            recViewMisPropuestas.setHasFixedSize(true);
            // Creo un layoutmanager para el recyclerview
            recViewMisPropuestas.setLayoutManager(new LinearLayoutManager(contexto,LinearLayoutManager.VERTICAL, false));
            AdaptadorPropuestasGuia adaptador = new AdaptadorPropuestasGuia(contexto, propuestas);
            recViewMisPropuestas.setAdapter(adaptador);
            adaptador.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = recViewMisPropuestas.getChildAdapterPosition(v);
                    if(!propuestas.get(pos).getEstado().equals(Estados.Aceptado)){
                        flatDialog.setIcon(R.drawable.facepalm)
                                .setTitle("Esperando aceptación")
                                .setTitleColor(getResources().getColor(R.color.negro))
                                .setSubtitle("Solo es posible chatear cuando el turista acepte tu propuesta")
                                .setSubtitleColor(getResources().getColor(R.color.negro))
                                .setBackgroundColor(getResources().getColor(R.color.background_light))
                                .setFirstButtonColor(getResources().getColor(R.color.colorAccentTurista))
                                .setFirstButtonTextColor(Color.parseColor("#000000"))
                                .setFirstButtonText("Cerrrar")
                                .withFirstButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        flatDialog.dismiss();
                                    }
                                })
                                .show();
                    }else{
                        /**
                         * Aqui iria el chatemisor si soy capaz de ponerlo xD
                         */
                        editor.putString("propuesta",propuestas.get(pos).getId()).commit();
                        editor.putString("idanuncio",propuestas.get(pos).getIdAnuncio()).commit();
                        Navigation.findNavController(PropuestasEnviadas.vista).navigate(R.id.nav_chat);
                    }
                }
            });
            adaptador.refrescar();
        }else{
            lnada.setVisibility(View.VISIBLE);
            lrecicler.setVisibility(View.GONE);
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
