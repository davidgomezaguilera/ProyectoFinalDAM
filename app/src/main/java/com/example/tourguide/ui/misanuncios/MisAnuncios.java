package com.example.tourguide.ui.misanuncios;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.example.tourguide.R;
import com.example.tourguide.adaptadores.AdaptadorMisAnuncios;
import com.example.tourguide.adaptadores.DeletionSwipeHelperBorrar;
import com.example.tourguide.adaptadores.DeletionSwipeHelperInfo;
import com.example.tourguide.adaptadores.SpaceItemDecoration;
import com.example.tourguide.controladores.ControladorBDLocal;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Estados;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MisAnuncios extends Fragment implements DeletionSwipeHelperBorrar.OnSwipeListener{

    private RecyclerView recViewMisAnuncios;
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

    public static MisAnuncios newInstance() {
        return new MisAnuncios();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.mis_anuncios_fragment, container, false);
        vista = root;
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();
        controlador = new ControladorBaseDatos();
        contexto = getContext();
        controladorBDLocal = new ControladorBDLocal(contexto);
        recViewMisAnuncios = root.findViewById(R.id.recViewMisAnuncios);
        flatDialog = new FlatDialog(contexto);
        lRecicler = root.findViewById(R.id.layoutRecicler);
        lNada = root.findViewById(R.id.layoutNada);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel


        try {
            ArrayList<Anuncio> datos = new ArrayList<>();
            ArrayList<Anuncio> datosVerdaderos = new ArrayList<>();
            if(prefs.getBoolean("conectividad",false)){
                System.out.println("CONECTIVIDAD IF MISANUNCIOS");
                datos = controlador.obtenerAnuncios(prefs.getString("token",""));

            }else{

                System.out.println("ELSE CONECTIVIDAD MIS NUNCIOS");
                datos = controladorBDLocal.obtenerAnunciosToken(prefs.getString("token",""));



            }
            //Arrays.sort(datos.toArray());
            for (int i = 0; i!= datos.size();i++){
                System.out.println("DATOS DENTRO DEL FOR "+ datos.get(i).toString());
                if(!datos.get(i).getEstado().equals(Estados.Contratado)){

                    datosVerdaderos.add(datos.get(i));


                }


            }


            lNada.setVisibility(View.GONE);
            lRecicler.setVisibility(View.GONE);
           if(datosVerdaderos.size() > 0){
               lNada.setVisibility(View.GONE);
               lRecicler.setVisibility(View.VISIBLE);
               // Hay que crear en la carpeta values un fichero dimens.xml y crear ahí list_space
               recViewMisAnuncios.addItemDecoration(new SpaceItemDecoration(contexto, R.dimen.list_space, true, true));
               // Con esto el tamaño del recyclerwiew no cambiará
               recViewMisAnuncios.setHasFixedSize(true);
               // Creo un layoutmanager para el recyclerview
               recViewMisAnuncios.setLayoutManager(new LinearLayoutManager(contexto,LinearLayoutManager.VERTICAL, false));
               adaptador = new AdaptadorMisAnuncios(contexto, datosVerdaderos,"turista");
               recViewMisAnuncios.setAdapter(adaptador);
               adaptador.setOnItemClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       int pos = recViewMisAnuncios.getChildAdapterPosition(v);
                       Navigation.findNavController(MisAnuncios.vista).navigate(R.id.nav_misanuncios_anuncio);
                       editor.putString("idanuncio",datosVerdaderos.get(pos).getId());
                       editor.commit();
                   }
               });
               adaptador.refrescar();
               ItemTouchHelper.Callback callback = new DeletionSwipeHelperInfo(0, ItemTouchHelper.START, getContext(), this::onSwiped);
               ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
               itemTouchHelper.attachToRecyclerView(recViewMisAnuncios);
           }else{
               lRecicler.setVisibility(View.GONE);
               lNada.setVisibility(View.VISIBLE);
           }

        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }catch(ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int position) {
        adaptador.refrescar();
        flatDialog.setBackgroundColor(Color.parseColor("#f9fce1"))
                .setFirstButtonColor(Color.parseColor("#d3f6f3"))
                .setFirstButtonTextColor(Color.parseColor("#000000"))
                .setFirstButtonText("Eliminar")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((AdaptadorMisAnuncios.HolderMisAnuncios)viewHolder).removeItem(position);
                        flatDialog.dismiss();
                    }
                })
                .show();
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
