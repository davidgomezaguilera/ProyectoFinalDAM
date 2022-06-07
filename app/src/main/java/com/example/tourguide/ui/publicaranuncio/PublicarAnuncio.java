package com.example.tourguide.ui.publicaranuncio;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.barisatalay.filterdialog.FilterDialog;
import com.barisatalay.filterdialog.model.DialogListener;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.example.tourguide.R;
import com.example.tourguide.controladores.ControladorBDLocal;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.ObjectSerializer;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Estados;
import com.example.tourguide.modelo.Intereses;
import com.github.florent37.materialtextfield.MaterialTextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class PublicarAnuncio extends Fragment implements SlyCalendarDialog.Callback {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private BootstrapButton bAnadir,bEliminar;
    private Button bFechas, bTitulo,bPublicar, bCiudad,bInteres;
    private int contadorClicks = 0;
    private EditText acompanante1,acompanante2,acompanante3;
    private MaterialTextField mt1,mt2,mt3,mt4;
    private List<String> provincias,ciudades;
    private FlatDialog flatDialog;
    private ControladorBaseDatos controlador;
    private ControladorBDLocal controladorBDLocal;
    private Date fecha1,fecha2;
    private Activity activity;
    public static PublicarAnuncio newInstance() {
        return new PublicarAnuncio();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.publicar_anuncio_fragment, container, false);
        activity = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();
        flatDialog = new FlatDialog(getContext());
        controlador = new ControladorBaseDatos();
        controladorBDLocal = new ControladorBDLocal(getContext());
        bAnadir = root.findViewById(R.id.banadir);
        acompanante1 = root.findViewById(R.id.etacompanante1);
        acompanante2 = root.findViewById(R.id.etacompanante2);
        acompanante3 = root.findViewById(R.id.etacompanante3);
        bEliminar = root.findViewById(R.id.beliminar);
        bFechas = root.findViewById(R.id.bFechaPublicar);
        bInteres = root.findViewById(R.id.bInteres);
        bTitulo = root.findViewById(R.id.bTitulo);
        bPublicar = root.findViewById(R.id.bPublicar);
        bCiudad = root.findViewById(R.id.bCiudad);
        mt1 = root.findViewById(R.id.mt1);
        mt2 = root.findViewById(R.id.mt2);
        mt3 = root.findViewById(R.id.mt3);
        mt1.setVisibility(View.GONE);
        mt2.setVisibility(View.GONE);
        mt3.setVisibility(View.GONE);
        bPublicar.setEnabled(false);

        bTitulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flatDialog.setTitle(getResources().getString(R.string.titulotitulo))
                        .setBackgroundColor(getResources().getColor(R.color.background_light))
                        .setTitleColor(getResources().getColor(R.color.negro))
                        .setFirstTextFieldHint(getResources().getString(R.string.titulo))
                        .setFirstTextFieldBorderColor(getResources().getColor(R.color.semi_black))
                        .setFirstTextFieldHintColor(getResources().getColor(R.color.semi_black))
                        .setFirstTextFieldTextColor(getResources().getColor(R.color.negro))
                        .setFirstButtonText(getResources().getString(R.string.guardar))
                        .setSecondButtonText(getResources().getString(R.string.cancelar))
                        .setFirstButtonColor(getResources().getColor(R.color.colorAccentTurista))
                        .setSecondButtonColor(getResources().getColor(R.color.background_deletion_swipe))
                        .withFirstButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (flatDialog.getFirstTextField().isEmpty()){
                                    bTitulo.setText(getResources().getString(R.string.titulopublicacion));
                                    if (!bTitulo.getText().equals(getResources().getString(R.string.titulopublicacion)) &&
                                            !bInteres.getText().equals(getResources().getString(R.string.intereses)) &&
                                            !bCiudad.getText().equals(getResources().getString(R.string.visitarciudad)) &&
                                            !bFechas.getText().equals(getResources().getString(R.string.selecionafechas))){
                                        bPublicar.setEnabled(true);
                                    }
                                }else{
                                    bTitulo.setText(flatDialog.getFirstTextField());
                                }
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
            }
        });

        bAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contadorClicks++;
                switch (contadorClicks){
                    case 1:
                        bEliminar.setEnabled(true);
                        mt1.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mt2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        mt3.setVisibility(View.VISIBLE);
                        bAnadir.setEnabled(false);
                        break;
                }
            }
        });

        bEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (contadorClicks){
                    case 1:
                        bEliminar.setEnabled(false);
                        mt1.setVisibility(View.GONE);
                        acompanante1.setText("");
                        break;
                    case 2:
                        mt2.setVisibility(View.GONE);
                        bAnadir.setEnabled(true);
                        acompanante2.setText("");
                        break;
                    case 3:
                        mt3.setVisibility(View.GONE);
                        acompanante3.setText("");
                        break;
                }
                contadorClicks--;
            }
        });

        bFechas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlyCalendarDialog()
                        .setSingle(false)
                        .setCallback(PublicarAnuncio.this)
                        //.setBackgroundColor(Color.parseColor("#ff0000"))
                       // .setSelectedTextColor(Color.parseColor("#ffff00"))
                        //.setSelectedColor(Color.parseColor("#0000ff"))
                        .show(getParentFragmentManager(), "TAG_SLYCALENDAR");
            }
        });

        bCiudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(prefs.getBoolean("conectividad",false)){
                        provincias = controlador.obtenerProvincias();
                    }else{
                        provincias = controladorBDLocal.obtenerProvincias();
                    }

                } catch (ServidorPHPException e) {
                    e.printStackTrace();
                }

                final FilterDialog filterDialog = new FilterDialog(activity);
                filterDialog.setToolbarTitle(getResources().getString(R.string.eligetuprovincia));
                filterDialog.setSearchBoxHint(getResources().getString(R.string.buscatuprovincia));
                filterDialog.setList(provincias);

                filterDialog.backPressedEnabled(true);
                /*
                 * When you have List<String,Integer,Boolean,Double,Float> should be use this method
                 */
                filterDialog.show((DialogListener.Single) selectedItem -> {
                    try {
                        if(prefs.getBoolean("conectividad",false)){
                            ciudades = controlador.obtenerCiudades(selectedItem.getName());
                        }else{
                            ciudades = controladorBDLocal.obtenerCiudadesNombreID(selectedItem.getName());
                        }

                        final FilterDialog filterDialog1 = new FilterDialog(activity);
                        filterDialog1.setToolbarTitle(getResources().getString(R.string.eligetuciudad));
                        filterDialog1.setSearchBoxHint(getResources().getString(R.string.buscatuciudad));
                        filterDialog1.setList(ciudades);

                        filterDialog1.backPressedEnabled(true);
                        /*
                         * When you have List<String,Integer,Boolean,Double,Float> should be use this method
                         */
                        filterDialog1.show((DialogListener.Single) selectedItem1 -> {
                            bCiudad.setText(selectedItem1.getName());
                            if (!bTitulo.getText().equals(getResources().getString(R.string.titulopublicacion)) &&
                                    !bInteres.getText().equals(getResources().getString(R.string.intereses)) &&
                                    !bCiudad.getText().equals(getResources().getString(R.string.visitarciudad)) &&
                                    !bFechas.getText().equals(getResources().getString(R.string.selecionafechas))){
                                bPublicar.setEnabled(true);
                            }
                            filterDialog1.dispose();
                        });
                    } catch (ServidorPHPException e) {
                        e.printStackTrace();
                    }
                    filterDialog.dispose();
                });
            }
        });

        bInteres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> interes = new ArrayList<>();
                for (int i = 0; i!= Intereses.values().length;i++){
                    interes.add(Intereses.values()[i].toString());
                }
                final FilterDialog filterDialog1 = new FilterDialog(activity);
                filterDialog1.setToolbarTitle(getResources().getString(R.string.tintereses));
                filterDialog1.setSearchBoxHint(getResources().getString(R.string.bintereses));
                filterDialog1.setList(interes);
                filterDialog1.backPressedEnabled(true);
                /*
                 * When you have List<String,Integer,Boolean,Double,Float> should be use this method
                 */
                filterDialog1.show((DialogListener.Single) selectedItem1 -> {
                    bInteres.setText(selectedItem1.getName());
                    if (!bTitulo.getText().equals(getResources().getString(R.string.titulopublicacion)) &&
                            !bInteres.getText().equals(getResources().getString(R.string.intereses)) &&
                            !bCiudad.getText().equals(getResources().getString(R.string.visitarciudad)) &&
                            !bFechas.getText().equals(getResources().getString(R.string.selecionafechas))){
                        bPublicar.setEnabled(true);
                    }
                    filterDialog1.dispose();
                });
            }
        });

        bPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefs.getBoolean("conectividad",false)){
                Calendar actual = new GregorianCalendar();
                if (!bTitulo.getText().equals(getResources().getString(R.string.titulopublicacion)) &&
                        !bInteres.getText().equals(getResources().getString(R.string.intereses)) &&
                        !bCiudad.getText().equals(getResources().getString(R.string.visitarciudad)) &&
                        !bFechas.getText().equals(getResources().getString(R.string.selecionafechas))) {

                    flatDialog = new FlatDialog(getContext());
                    flatDialog.setTitle("¿Algo mas que añadir?")
                            .setTitleColor(getResources().getColor(R.color.negro))
                            .setBackgroundColor(getResources().getColor(R.color.background_light))
                            .setLargeTextFieldHint("Detalla aquí")
                            .setLargeTextFieldHintColor(getResources().getColor(R.color.negro))
                            .setLargeTextFieldBorderColor(getResources().getColor(R.color.negro))
                            .setLargeTextFieldTextColor(getResources().getColor(R.color.negro))
                            .setFirstButtonColor(getResources().getColor(R.color.colorAccentTurista))
                            .setFirstButtonTextColor(getResources().getColor(R.color.negro))
                            .setFirstButtonText("Publicar anuncio")
                            .setSecondButtonColor(getResources().getColor(R.color.background_deletion_swipe))
                            .setSecondButtonText("Cancelar")
                            .setSecondButtonTextColor(getResources().getColor(R.color.negro))
                            .withFirstButtonListner(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    java.sql.Timestamp sqlFechaActual = new java.sql.Timestamp(actual.getTimeInMillis());
                                    Anuncio a = new Anuncio(null, prefs.getString("token", ""),
                                            bTitulo.getText().toString(),
                                            bCiudad.getText().toString(),
                                            null,
                                            Intereses.valueOf(bInteres.getText().toString()),
                                            acompanante1.getText() + "-" + acompanante2.getText() + "-" + acompanante3.getText(),
                                            sqlFechaActual,
                                            fecha1,
                                            fecha2,
                                            flatDialog.getLargeTextField(),
                                            null,
                                            Estados.Entregado
                                    );
                                    acompanante1.setText("");
                                    acompanante2.setText("");
                                    acompanante3.setText("");
                                    try {
                                        if (controlador.insertarAnuncio(a.getTokenTurista(), a.getCiudad(), a.getAcompanantes(), a.getTipo(), a.getNombre(), a.getFechaCreacion(), a.getFecha1(), a.getFecha2(), a.getMensaje(), String.valueOf(Estados.Entregado))) {
                                            Toasty.success(getContext(), "Anuncio insertado correctamente", Toast.LENGTH_SHORT, true).show();

                                            Navigation.findNavController(root).navigate(R.id.nav_misanuncios);

                                        } else {
                                            Toasty.error(getContext(), "Pos no", Toast.LENGTH_SHORT, true).show();
                                        }
                                    } catch (ServidorPHPException e) {
                                        e.printStackTrace();
                                    }
                                    flatDialog.dismiss();
                                }
                            })
                            .withSecondButtonListner(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    flatDialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    Toasty.error(getContext(), "Deber rellenar todos los campos", Toast.LENGTH_SHORT, true).show();
                }

            }else{
                    System.out.println("preInsertado");
                    //aqui voy a llamar al controlador
                    bPublicar.setEnabled(true);
                    Calendar actual = new GregorianCalendar();
                    java.sql.Timestamp sqlFechaActual = new java.sql.Timestamp(actual.getTimeInMillis());
                    System.out.println("ES FECHA UNA INSTANCIA DE DATE??");
                    System.out.println(fecha1.getClass().getName());

                    Anuncio anuncio = new Anuncio("500",prefs.getString("token", ""),
                            bTitulo.getText().toString(),
                            bCiudad.getText().toString(),
                            null,
                            Intereses.valueOf(bInteres.getText().toString()),
                            acompanante1.getText() + "-" + acompanante2.getText() + "-" + acompanante3.getText(),
                            new Date(sqlFechaActual.getTime()),
                            new Date(fecha1.getTime()),
                            new Date(fecha2.getTime()),
                            flatDialog.getLargeTextField(),
                            controladorBDLocal.obtenerTurista(prefs.getString("token", "")),
                            Estados.Entregado);


                            controladorBDLocal.insertarAnuncio(controladorBDLocal.obtenerTurista(prefs.getString("token", "")).getId(),
                            controladorBDLocal.obtenerCiudadesPorNombre(bCiudad.getText().toString()),
                            acompanante1.getText() + "-" + acompanante2.getText() + "-" + acompanante3.getText(),
                            Intereses.valueOf(bInteres.getText().toString()),
                            bTitulo.getText().toString(),
                            new Date(sqlFechaActual.getTime()),
                            new Date(fecha1.getTime()),
                            new Date(fecha2.getTime()),
                            flatDialog.getLargeTextField(),
                            Estados.Entregado.toString());
                    System.out.println("postInsertado");
                    try{
                        ArrayList<Anuncio> anuncios = (ArrayList<Anuncio>) ObjectSerializer.deserialize(prefs.getString("anunciosInsertar",ObjectSerializer.serialize(new ArrayList<Anuncio>())));
                        if(anuncios.size()==0){
                            System.out.println("esta el por defecto");
                            anuncios.clear();
                            anuncios.add(anuncio);
                            editor.putString("anunciosInsertar", ObjectSerializer.serialize(anuncios)).commit();
                        }else{
                            anuncios.add(anuncio);
                            editor.putString("anunciosInsertar", ObjectSerializer.serialize(anuncios)).commit();
                        }

                       }catch(IOException e){
                        e.printStackTrace();
                    }catch(ClassNotFoundException e){
                        e.printStackTrace();
                    }
                    Toasty.success(getContext(), "INSERTADO.", Toast.LENGTH_SHORT, true).show();

                    Navigation.findNavController(root).navigate(R.id.nav_misanuncios);
                }
        }
        });


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes){
        java.sql.Timestamp sqlf1 = new java.sql.Timestamp(firstDate.getTimeInMillis());
        java.sql.Timestamp sqlf2 = new java.sql.Timestamp(secondDate.getTimeInMillis());
        fecha1 = sqlf1;
        fecha2 = sqlf2;
        String fecha1[] = firstDate.getTime().toString().split(" ");
        String fecha2[] = secondDate.getTime().toString().split(" ");

        String f1_mes = "";
        switch (fecha1[1]){
            case "Jan":
                f1_mes = "Ene";
                break;
            case "Apr":
                f1_mes = "Abr";
                break;
            case "Aug":
                f1_mes = "Ago";
                break;
            case "Dec":
                f1_mes = "Dic";
                break;
            default:
                f1_mes = fecha1[1];
                break;
        }

        String f2_mes = "";
        switch (fecha2[1]){
            case "Jan":
                f2_mes = "Ene";
                break;
            case "Aug":
                f2_mes = "Ago";
                break;
            case "Apr":
                f2_mes = "Abr";
                break;
            case "Dec":
                f2_mes = "Dic";
                break;
            default:
                f2_mes = fecha2[1];
                break;
        }
        bFechas.setText(fecha1[2] + "/" + f1_mes + "/" + fecha1[5] + " - " + fecha2[2] + "/" + f2_mes + "/" + fecha2[5]);
        if (!bTitulo.getText().equals(getResources().getString(R.string.titulopublicacion)) &&
                !bInteres.getText().equals(getResources().getString(R.string.intereses)) &&
                !bCiudad.getText().equals(getResources().getString(R.string.visitarciudad)) &&
                !bFechas.getText().equals(getResources().getString(R.string.selecionafechas))){
            bPublicar.setEnabled(true);
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
