package com.example.tourguide.ui.buscaranuncio;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.example.tourguide.R;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Estados;
import com.example.tourguide.modelo.Guia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static java.time.temporal.ChronoUnit.DAYS;

public class BuscarAnuncio_Propuesta extends Fragment {

    private ControladorBaseDatos controlador;
    private Context contexto;
    private Activity activity;
    private SharedPreferences.Editor editor;
    private static View vista;
    private SharedPreferences prefs;
    private LinearLayout nDias;
    private FlatDialog flatDialog;
    private Hashtable<Integer, String> mensajes = new Hashtable<Integer, String>();
    private Button bEnviar;
    private boolean duplicado= false,algoescrito=false;
    private TextView tvNombreCliente,tvAcompanantes,tvFechas,tvMensaje,etComentarios,tvTitulo;
    private long dias;



    public static BuscarAnuncio_Propuesta newInstance() {
        return new BuscarAnuncio_Propuesta();
    }

    private ArrayList<String> sacarFecha(String[] fecha1){
        ArrayList<String> fecha = new ArrayList<>(Arrays.asList(fecha1));
        switch (fecha.get(1)){
            case "Jan":
                fecha.set(1,"Ene");
                break;
            case "Apr":
                fecha.set(1,"Abr");
                break;
            case "Aug":
                fecha.set(1,"Ago");
                break;
            case "Dec":
                fecha.set(1,"Dic");
                break;
            default:
                fecha.set(1,fecha1[1]);
                break;
        }
        return fecha;
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.buscar_anuncio_propuesta_fragment, container, false);
        flatDialog = new FlatDialog(getContext());
        vista = root;
        contexto = getContext();
        activity = getActivity();
        controlador = new ControladorBaseDatos();
        nDias = root.findViewById(R.id.lDias);
        bEnviar = root.findViewById(R.id.bEnviar);
        prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
        editor = prefs.edit();
        tvNombreCliente = root.findViewById(R.id.tvNombreCliente);
        tvAcompanantes = root.findViewById(R.id.tvAcompanantes);
        tvFechas = root.findViewById(R.id.tvFechas);
        tvMensaje = root.findViewById(R.id.tvMensaje);
        etComentarios = root.findViewById(R.id.etComentarios);
        tvTitulo = root.findViewById(R.id.tvTituloanuncio);
        try {
            Anuncio a = controlador.obtenerAnunciosID(prefs.getString("idanuncio",""));
            if (controlador.obtenerPropuestaIDAIDGuia(a.getId(),controlador.obtenerGuia(prefs.getString("token","")).getId()).size()==1){
                duplicado = true;
            }
            String[] acompanantesArray = a.getAcompanantes().split("-");
            tvTitulo.setText(a.getNombre());
            tvMensaje.setText(a.getMensaje());
            tvNombreCliente.setText(a.getTurista().getNombre());
            //comprobamos los acompañantes que hay en el acompanatesArray y lo introducimos en la TextView
            tvAcompanantes.setText(acompanantesArray.length + " " + (acompanantesArray.length!=1?"compañeros":"compañero"));

            String [] fecha1 = a.getFecha1().toString().split(" ");
            String[] fecha2 = a.getFecha2().toString().split(" ");

            tvFechas.setText(sacarFecha(fecha1).get(2) + ". "  + sacarFecha(fecha1).get(1) +  ". " + sacarFecha(fecha1).get(5) + "\n" +
                                sacarFecha(fecha2).get(2) + ". " + sacarFecha(fecha2).get(1) + ". " + sacarFecha(fecha2).get(5)
                            );
            LinearLayout contenedor = new LinearLayout(contexto);
            contenedor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contenedor.setOrientation(LinearLayout.VERTICAL);

            Calendar calendar1 = new GregorianCalendar();
            calendar1.setTime(a.getFecha1());
            int year1 = calendar1.get(Calendar.YEAR);
            int month1 = calendar1.get(Calendar.MONTH) + 1;
            int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
            Calendar calendar2 = new GregorianCalendar();
            calendar2.setTime(a.getFecha2());
            int year2 = calendar2.get(Calendar.YEAR);
            int month2 = calendar2.get(Calendar.MONTH) + 1;
            int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
            LocalDate f1 = LocalDate.of(year1, month1, day1);
            LocalDate f2 = LocalDate.of(year2, month2, day2);
            dias = DAYS.between(f1,f2);

            for (int j = 0; j < dias; j++ ){

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,10,0,10);
                Button boton = new Button(contexto);
                boton.setLayoutParams(layoutParams);
                boton.setTextAppearance(getResources().getIdentifier("textonormal","style",contexto.getPackageName()));
                boton.setText(getResources().getString(R.string.dia)+ " "+ (j+1));
                boton.setBackground(getResources().getDrawable(R.drawable.bordebotonesguia));
                boton.setId(j);
                boton.setOnClickListener(new ButtonsOnClickListener(contexto,j));
                contenedor.addView(boton);
                mensajes.put(j,"vacio");
            }
            nDias.addView(contenedor);


            bEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mensaje= "";
                    if(etComentarios.getText().toString().isEmpty()){
                        mensaje += "vacio";
                    }
                    mensaje+=etComentarios.getText().toString()+ "~";
                    for (Map.Entry<Integer, String> entry : mensajes.entrySet()) {
                        /*
                        *El mensaje sera el diccionario entero para luego separarlo en el for, detalles sera el ultimo elemento
                        */

                        mensaje+= (entry.getKey()+1)+ "~" + entry.getValue() + "~";
                    }
                    try {
                        Guia g = controlador.obtenerGuia(prefs.getString("token",""));
                        Boolean eliminadook= true;
                        //comprueba que haya algo escrito en la propuesta
                        if(algoescrito){
                            //comprueba si la propuesta es duplicada
                            if(duplicado){
                                if(!controlador.eliminarPropuesta(controlador.obtenerPropuestaIDAIDGuia(a.getId(),controlador.obtenerGuia(prefs.getString("token","")).getId()).get(0).getId())){
                                    eliminadook = false;
                                }
                            }
                            if(eliminadook){
                                if(controlador.insertarPropuesta(g.getId(),a.getId(),mensaje, Estados.Entregado)){
                                    Navigation.findNavController(BuscarAnuncio_Propuesta.vista).navigate(R.id.nav_home);
                                    Navigation.findNavController(BuscarAnuncio_Propuesta.vista).navigate(R.id.nav_propuestasenviadas);
                                }
                            }
                        }else{
                            Toasty.error(contexto,"No se pueden mandar propuestas vacías", Toast.LENGTH_LONG).show();
                        }

                    } catch (ServidorPHPException e) {
                        e.printStackTrace();
                    }

                }
            });


        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }
//devolvemos la View
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }


    class ButtonsOnClickListener implements View.OnClickListener
    {
        Context context;
        int numButton;

        public ButtonsOnClickListener(Context context, int numButton) {
            this.context = context;
            this.numButton = numButton;
        }

        public void montarDialogo(String texto){
            if(texto.equals("vacio")){
                flatDialog.setTitle("Desarrolla el planing")
                        .setTitleColor(getResources().getColor(R.color.negro))
                        .setBackgroundColor(getResources().getColor(R.color.background_light))
                        .setLargeTextFieldHint("Desarrolla aquí")
                        .setLargeTextFieldHintColor(getResources().getColor(R.color.negro))
                        .setLargeTextFieldBorderColor(getResources().getColor(R.color.negro))
                        .setLargeTextFieldTextColor(getResources().getColor(R.color.negro))
                        .setFirstButtonColor(getResources().getColor(R.color.colorAccentTurista))
                        .setFirstButtonTextColor(getResources().getColor(R.color.negro))
                        .setFirstButtonText("Aceptar")
                        .setSecondButtonColor(getResources().getColor(R.color.background_deletion_swipe))
                        .setSecondButtonText("Cancelar")
                        .setSecondButtonTextColor(getResources().getColor(R.color.negro))
                        .withFirstButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mensajes.put(numButton, flatDialog.getLargeTextField());
                                algoescrito = !mensajes.get(numButton).isEmpty();
                                flatDialog.setLargeTextField("");
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
            }else{
                flatDialog.setTitle("Desarrolla el planing")
                        .setTitleColor(getResources().getColor(R.color.negro))
                        .setBackgroundColor(getResources().getColor(R.color.background_light))
                        .setLargeTextFieldHint("Desarrolla aquí")
                        .setLargeTextField(texto)
                        .setLargeTextFieldHintColor(getResources().getColor(R.color.negro))
                        .setLargeTextFieldBorderColor(getResources().getColor(R.color.negro))
                        .setLargeTextFieldTextColor(getResources().getColor(R.color.negro))
                        .setFirstButtonColor(getResources().getColor(R.color.colorAccentTurista))
                        .setFirstButtonTextColor(getResources().getColor(R.color.negro))
                        .setFirstButtonText("Aceptar")
                        .setSecondButtonColor(getResources().getColor(R.color.background_deletion_swipe))
                        .setSecondButtonText("Cancelar")
                        .setSecondButtonTextColor(getResources().getColor(R.color.negro))
                        .withFirstButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mensajes.put(numButton, flatDialog.getLargeTextField());
                                flatDialog.setLargeTextField("");
                                algoescrito = !mensajes.get(numButton).isEmpty();
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

            }
        }

        @Override
        public void onClick(View v)
        {
            montarDialogo(mensajes.get(numButton));
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
