package com.example.tourguide.ui.buscaranuncio;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.barisatalay.filterdialog.FilterDialog;
import com.barisatalay.filterdialog.model.DialogListener;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.example.tourguide.R;
import com.example.tourguide.adaptadores.AdaptadorMisAnuncios;
import com.example.tourguide.adaptadores.AdaptadorMisViajes;
import com.example.tourguide.adaptadores.SpaceItemDecoration;
import com.example.tourguide.controladores.ControladorBDLocal;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Estados;
import com.example.tourguide.modelo.Propuesta;
import com.example.tourguide.ui.misanuncios.MisAnuncios;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import es.dmoral.toasty.Toasty;

public class BuscarAnuncios extends Fragment {

    private RecyclerView recViewBuscarAnuncios;
    private Context contexto;
    private ArrayList<Anuncio> datos;
    private SharedPreferences.Editor editor;
    private static View vista;
    private ControladorBaseDatos controlador;
    private ControladorBDLocal controladorBDLocal;
    private SharedPreferences prefs;
    private Button bCiudadFiltro,bFiltroProvincia;
    private TextView tvBusqueda;
    private FlatDialog flatDialog;
    private LinearLayout lrecicler;
    private ConstraintLayout lnada;

    public static BuscarAnuncios newInstance() {
        return new BuscarAnuncios();
    }

    private void mensajePropuestaEnviada(String idanuncio,String idguia) throws ServidorPHPException {
        if(!prefs.getBoolean("conectividad",false)){

            Toasty.error(contexto,"Estás en modo sin conexión", Toast.LENGTH_LONG).show();
        }else if (controlador.obtenerPropuestaIDAIDGuia(idanuncio,idguia).size()==1){
            flatDialog.setIcon(R.drawable.warning)
                    .setTitle("Ya has enviado una propuesta a este anuncio")
                    .setTitleColor(getResources().getColor(R.color.negro))
                    .setSubtitle("¿Quieres mandar otra? ¡¡Esta sobre-escribira la anterior!!")
                    .setSubtitleColor(getResources().getColor(R.color.negro))
                    .setBackgroundColor(getResources().getColor(R.color.background_light))
                    .setFirstButtonColor(getResources().getColor(R.color.colorAccentTurista))
                    .setFirstButtonTextColor(Color.parseColor("#000000"))
                    .setFirstButtonText("Continuar")
                    .setSecondButtonColor(getResources().getColor(R.color.background_deletion_swipe))
                    .setSecondButtonTextColor(Color.parseColor("#000000"))
                    .setSecondButtonText("Cancelar")
                    .withFirstButtonListner(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Navigation.findNavController(BuscarAnuncios.vista).navigate(R.id.nav_buscaranuncio_popuesta);
                            flatDialog.dismiss();
                        }
                    })
                    .withSecondButtonListner(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            flatDialog.dismiss();
                        }
                    })
                    .show();
        }else{
            Navigation.findNavController(BuscarAnuncios.vista).navigate(R.id.nav_buscaranuncio_popuesta);
        }
    }


    public void llenarRecicler(RecyclerView recView , ArrayList<Anuncio> anuncios){

        AdaptadorMisAnuncios adaptador =  new AdaptadorMisAnuncios(contexto, anuncios,"guia");
        recView.setAdapter(adaptador);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recView.getChildAdapterPosition(v);
                try {
                    mensajePropuestaEnviada(anuncios.get(pos).getId(),controlador.obtenerGuia(prefs.getString("token","")).getId());
                } catch (ServidorPHPException e) {
                    e.printStackTrace();
                }
                editor.putString("idanuncio",anuncios.get(pos).getId());
                editor.commit();
            }
        });
        adaptador.refrescar();
        if(anuncios.size() > 0){
            lrecicler.setVisibility(View.VISIBLE);
            lnada.setVisibility(View.GONE);

        }else{
            lnada.setVisibility(View.VISIBLE);
            lrecicler.setVisibility(View.GONE);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.buscar_anuncios_fragment, container, false);
        contexto = getContext();
        vista = root;
        controlador = new ControladorBaseDatos();
        controladorBDLocal = new ControladorBDLocal(contexto);
        flatDialog = new FlatDialog(contexto);
        prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
        editor = prefs.edit();
        recViewBuscarAnuncios = root.findViewById(R.id.recViewBuscarAnuncios);
        tvBusqueda = root.findViewById(R.id.tvBusqueda);
        bCiudadFiltro = root.findViewById(R.id.bCiudadFiltro);
        bFiltroProvincia = root.findViewById(R.id.bFiltroProvincia);
        lrecicler = root.findViewById(R.id.lrecicler);
        lnada = root.findViewById(R.id.lnada);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        /* Datossinfiltrar contiene todos los anuncios de una provincia */
        ArrayList<Anuncio> datossinfiltrar = null;
        lnada.setVisibility(View.GONE);
        lrecicler.setVisibility(View.GONE);
        try {
            if(prefs.getBoolean("conectividad",false)){
                datossinfiltrar = controlador.obtenerAnunciosProvincia(prefs.getString("provincia",""));
            }else{
                datossinfiltrar = controladorBDLocal.obtenerAnunciosProvincia(prefs.getString("provincia",""));

            }
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }catch(ParseException e){
            e.printStackTrace();
        }

        datos = new ArrayList<>();
        for (int i = 0; i!= datossinfiltrar.size();i++) {
            /* Datos contiene los anuncios de tu provicia pero sin salir los tuyos */
            if(!datossinfiltrar.get(i).getTurista().getToken().equals(prefs.getString("token","")) && !datossinfiltrar.get(i).getEstado().equals(Estados.Contratado)){
                datos.add(datossinfiltrar.get(i));
            }
        }
        // Creo un layoutmanager para el recyclerview
        // Hay que crear en la carpeta values un fichero dimens.xml y crear ahí list_space
        recViewBuscarAnuncios.addItemDecoration(new SpaceItemDecoration(contexto, R.dimen.list_space, true, true));
        // Con esto el tamaño del recyclerwiew no cambiará
        recViewBuscarAnuncios.setHasFixedSize(true);
        recViewBuscarAnuncios.setLayoutManager(new LinearLayoutManager(contexto,LinearLayoutManager.VERTICAL, false));
        llenarRecicler(recViewBuscarAnuncios,datos);

        tvBusqueda.setText(getResources().getString(R.string.mostrandoProv) + " " + prefs.getString("provincia",""));

        bCiudadFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> ciudades = null;
                try {
                    if(prefs.getBoolean("conectividad",false)){
                        ciudades = controlador.obtenerCiudades(prefs.getString("provincia",""));
                    }else{
                        ciudades = controladorBDLocal.obtenerCiudadesNombre(prefs.getString("provincia",""));
                    }

                } catch (ServidorPHPException e) {
                    e.printStackTrace();
                }
                final FilterDialog filterDialog1 = new FilterDialog(getActivity());
                filterDialog1.setToolbarTitle(getResources().getString(R.string.eligetuciudad));
                filterDialog1.setSearchBoxHint(getResources().getString(R.string.buscatuciudad));
                filterDialog1.setList(ciudades);

                filterDialog1.backPressedEnabled(true);

                filterDialog1.show((DialogListener.Single) selectedItem1 -> {

                    tvBusqueda.setText(getResources().getString(R.string.mostrandoCiu) + " " + selectedItem1.getName());
                    ArrayList<Anuncio> datosFiltrados = new ArrayList<>();
                    for (int i = 0; i!= datos.size();i++){
                        if(datos.get(i).getCiudad().equals(selectedItem1.getName())){
                            datosFiltrados.add(datos.get(i));
                        }
                    }
                    llenarRecicler(recViewBuscarAnuncios,datosFiltrados);
                    filterDialog1.dispose();
                });
            }
        });

        bFiltroProvincia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llenarRecicler(recViewBuscarAnuncios,datos);

                tvBusqueda.setText(getResources().getString(R.string.mostrandoProv) + " " + prefs.getString("provincia",""));
            }
        });

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
