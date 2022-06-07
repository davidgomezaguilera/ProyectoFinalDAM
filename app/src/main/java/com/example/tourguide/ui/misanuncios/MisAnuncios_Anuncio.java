package com.example.tourguide.ui.misanuncios;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.example.tourguide.R;
import com.example.tourguide.adaptadores.AdaptadorMisAnuncios;
import com.example.tourguide.adaptadores.AdaptadorPropuestasTurista;
import com.example.tourguide.adaptadores.DeletionSwipeHelperInfo;
import com.example.tourguide.adaptadores.SpaceItemDecoration;
import com.example.tourguide.controladores.ControladorBDLocal;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Estados;
import com.example.tourguide.modelo.Propuesta;
import com.example.tourguide.ui.propuestasenviadas.PropuestasEnviadas;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MisAnuncios_Anuncio extends Fragment implements DeletionSwipeHelperInfo.OnSwipeListener{

    private Context contexto;
    private ControladorBaseDatos controlador;
    private ControladorBDLocal controladorBDLocal;
    private RecyclerView recViewPropuestas;
    private SharedPreferences prefs;
    public static View vista;
    private SharedPreferences.Editor editor;
    private AdaptadorPropuestasTurista adaptador;
    private FlatDialog flatDialog;
    private ArrayList<Propuesta> propuestas;
    private LinearLayout lNada;

    public static MisAnuncios_Anuncio newInstance() {
        return new MisAnuncios_Anuncio();
    }

    public void llenarRecicler(RecyclerView recView , ArrayList<Propuesta> propuestas){
        adaptador = new AdaptadorPropuestasTurista(contexto, propuestas);
        recViewPropuestas.setAdapter(adaptador);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recViewPropuestas.getChildAdapterPosition(v);
                try {
                    if(propuestas.get(pos).getEstado().equals(Estados.Entregado)){
                        editor.putString("propuesta",propuestas.get(pos).getId());
                        controlador.updateEstadoPropuesta(Estados.Leido,propuestas.get(pos).getId());
                        editor.commit();
                        Navigation.findNavController(vista).navigate(R.id.nav_misanuncios_propuesta);
                    }else if(propuestas.get(pos).getEstado().equals(Estados.Aceptado)){
                        final FlatDialog flatDialog = new FlatDialog(contexto);
                        flatDialog.setTitle("¿Chatear o ver propuesta")
                                .setTitleColor(getResources().getColor(R.color.negro))
                                .setBackgroundColor(getResources().getColor(R.color.background_light))
                                .setFirstButtonColor(getResources().getColor(R.color.colorAccentTurista))
                                .setFirstButtonTextColor(Color.parseColor("#000000"))
                                .setFirstButtonText("Chatear")
                                .setSecondButtonColor(getResources().getColor(R.color.colorAccentTurista))
                                .setSecondButtonTextColor(Color.parseColor("#000000"))
                                .setSecondButtonText("Ver propuesta")
                                .withFirstButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        editor.putString("idanuncio",propuestas.get(pos).getIdAnuncio()).commit();
                                        editor.putString("propuesta",propuestas.get(pos).getId()).commit();
                                        Navigation.findNavController(vista).navigate(R.id.nav_chat);
                                        flatDialog.dismiss();
                                    }
                                }).withSecondButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editor.putString("idanuncio",propuestas.get(pos).getIdAnuncio()).commit();
                                editor.putString("propuesta",propuestas.get(pos).getId()).commit();
                                Navigation.findNavController(vista).navigate(R.id.nav_misanuncios_propuesta);
                                flatDialog.dismiss();
                            }
                        })
                                .show();
                    }else{
                        editor.putString("idanuncio",propuestas.get(pos).getIdAnuncio()).commit();
                        editor.putString("propuesta",propuestas.get(pos).getId()).commit();
                        System.out.println("POSICION ADAPTADOR "+ pos);
                        Navigation.findNavController(vista).navigate(R.id.nav_misanuncios_propuesta);
                    }
                } catch (ServidorPHPException e) {
                    e.printStackTrace();
                }
            }
        });
        adaptador.refrescar();

    }

    public void montarDialogotresopciones(RecyclerView.ViewHolder viewHolder, int position){
        flatDialog.setBackgroundColor(Color.parseColor("#f9fce1"))
                .setFirstButtonColor(Color.parseColor("#d3f6f3"))
                .setFirstButtonTextColor(Color.parseColor("#000000"))
                .setFirstButtonText("Aceptar")
                .setSecondButtonColor(Color.parseColor("#fee9b2"))
                .setSecondButtonTextColor(Color.parseColor("#000000"))
                .setSecondButtonText("Contratar")
                .setThirdButtonColor(Color.parseColor("#fbd1b7"))
                .setThirdButtonTextColor(Color.parseColor("#000000"))
                .setThirdButtonText("Rechazar")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /**
                         * Aqui lo acepta
                         */
                        try {
                            if(!prefs.getBoolean("conectividad",false)){
                                Toasty.error(contexto,"Estás en modo sin conexión", Toast.LENGTH_LONG).show();
                                flatDialog.dismiss();
                            }else if(controlador.updateEstadoPropuesta(Estados.Aceptado,propuestas.get(position).getId())){
                                /**
                                 * Aqui iria el chatemisor si soy capaz de ponerlo
                                 */
                                propuestas = controlador.obtenerPropuestaIDA(prefs.getString("idanuncio",""));
                                editor.putString("propuesta",propuestas.get(position).getId());
                                editor.commit();
                                flatDialog.dismiss();
                                Navigation.findNavController(MisAnuncios_Anuncio.vista).navigate(R.id.nav_chat);
                            }else{
                                    Toasty.error(contexto,"ha ocurrido un error", Toast.LENGTH_LONG).show();
                                    flatDialog.dismiss();

                            }
                        } catch (ServidorPHPException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /**
                         * Aqui lo contrata
                         */if(!prefs.getBoolean("conectividad",false)){
                            Toasty.error(contexto,"Estás en modo sin conexión", Toast.LENGTH_LONG).show();
                            flatDialog.dismiss();
                        }else if(!propuestas.get(position).getEstado().equals(Estados.Aceptado)){
                            Toasty.success(contexto,"Se recomienda primero aceptar el anuncio y luego contratarlo. Una vez contratado no podras volver atras",Toast.LENGTH_LONG).show();
                        }else{
                            try {
                                if(controlador.updateEstadoAnuncio(Estados.Contratado,propuestas.get(position).getIdAnuncio()) &&
                                    controlador.updateEstadoPropuesta(Estados.Contratado,propuestas.get(position).getId()) &&
                                    controlador.updateIDGuiaAnuncio(propuestas.get(position).getIdGuia(),propuestas.get(position).getIdAnuncio())
                                ){

                                    propuestas = controlador.obtenerPropuestaIDA(prefs.getString("idanuncio",""));
                                    editor.putString("propuesta",propuestas.get(position).getId());
                                    editor.commit();
                                    flatDialog.dismiss();
                                    Navigation.findNavController(MisAnuncios_Anuncio.vista).navigate(R.id.nav_home);
                                    Navigation.findNavController(MisAnuncios_Anuncio.vista).navigate(R.id.nav_misviajes);
                                }else{
                                    flatDialog.dismiss();
                                    Toasty.error(contexto,"ha ocurrido un error", Toast.LENGTH_LONG).show();
                                }
                            } catch (ServidorPHPException e) {
                                e.printStackTrace();
                            }

                        }
                        flatDialog.dismiss();
                    }
                })
                .withThirdButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /**
                         * Aqui lo rechaza
                         */
                        try {
                            if(!prefs.getBoolean("conectividad",false)){
                                Toasty.error(contexto,"Estás en modo sin conexión", Toast.LENGTH_LONG).show();
                                flatDialog.dismiss();
                            }else{
                                controlador.updateEstadoPropuesta(Estados.Rechazado, propuestas.get(position).getId());
                                propuestas = controlador.obtenerPropuestaIDA(prefs.getString("idanuncio", ""));
                                llenarRecicler(recViewPropuestas, propuestas);
                                adaptador.refrescar();
                            }
                        } catch (ServidorPHPException e) {
                            e.printStackTrace();
                        }
                        flatDialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.mis_anuncios_anuncio_fragment, container, false);
        vista = root;
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        controlador = new ControladorBaseDatos();
        contexto = getContext();
        controladorBDLocal = new ControladorBDLocal(contexto);
        editor = prefs.edit();
        recViewPropuestas = root.findViewById(R.id.recViewPropuestas);
        flatDialog = new FlatDialog(contexto);
        lNada = root.findViewById(R.id.lNadita);

        lNada.setVisibility(View.GONE);
        recViewPropuestas.setVisibility(View.GONE);
        try {
            if(prefs.getBoolean("conectividad",false)){

                propuestas = controlador.obtenerPropuestaIDA(prefs.getString("idanuncio",""));

            }else{
                propuestas = controladorBDLocal.obtenerPropuestaIDAnuncio(prefs.getString("idanuncio",""));
            }
            System.out.println("algo hare");
            if(propuestas.size()> 0){
                System.out.println("Hay cosas");
                lNada.setVisibility(View.GONE);
                recViewPropuestas.setVisibility(View.VISIBLE);
            }else{
                System.out.println("entro");
                lNada.setVisibility(View.VISIBLE);
                recViewPropuestas.setVisibility(View.GONE);
            }
            for (int i = 0 ; i!= propuestas.size(); i++){
                // Hay que crear en la carpeta values un fichero dimens.xml y crear ahí list_space
                recViewPropuestas.addItemDecoration(new SpaceItemDecoration(contexto, R.dimen.list_space, true, true));
                // Con esto el tamaño del recyclerwiew no cambiará
                recViewPropuestas.setHasFixedSize(true);
                // Creo un layoutmanager para el recyclerview
                recViewPropuestas.setLayoutManager(new LinearLayoutManager(contexto,LinearLayoutManager.VERTICAL, false));
                llenarRecicler(recViewPropuestas,propuestas);
                ItemTouchHelper.Callback callback = new DeletionSwipeHelperInfo(0, ItemTouchHelper.START, getContext(), this);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
                itemTouchHelper.attachToRecyclerView(recViewPropuestas);
            }

        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel




    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int position) {

        montarDialogotresopciones(viewHolder,position);
        adaptador.refrescar();
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
