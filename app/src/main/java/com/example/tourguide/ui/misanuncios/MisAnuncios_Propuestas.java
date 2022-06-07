package com.example.tourguide.ui.misanuncios;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.example.tourguide.R;
import com.example.tourguide.controladores.ControladorBDLocal;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Guia;
import com.example.tourguide.modelo.Propuesta;
import com.example.tourguide.ui.buscaranuncio.BuscarAnuncio_Propuesta;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.DAYS;

public class MisAnuncios_Propuestas extends Fragment {

    private Context contexto;
    private ControladorBaseDatos controlador;
    private ControladorBDLocal controladorBDLocal;
    private SharedPreferences prefs;
    public static View vista;
    private SharedPreferences.Editor editor;
    private TextView tvTituloPAnuncio,tvNombreGuia,tvAcompanante,tvFecha,tvMensajes,tvCiudadVivir;
    private LinearLayout lInfo;

    public static MisAnuncios_Propuestas newInstance() {
        return new MisAnuncios_Propuestas();
    }

    private ArrayList<String> sacarFecha(String[] fecha1){
        ArrayList<String> fecha = new ArrayList<>(Arrays.asList(fecha1));
        String f1_mes = "";
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
        View root = inflater.inflate(R.layout.mis_anuncios_propuestas_fragment, container, false);
        vista = root;
        contexto = getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
        editor = prefs.edit();
        controlador = new ControladorBaseDatos();
        controladorBDLocal = new ControladorBDLocal(contexto);
        tvTituloPAnuncio = root.findViewById(R.id.tvTituloPAnuncio);
        tvNombreGuia = root.findViewById(R.id.tvNombreGuia);
        tvAcompanante = root.findViewById(R.id.tvAcompanante);
        tvFecha = root.findViewById(R.id.tvFecha);
        tvMensajes = root.findViewById(R.id.tvMensajes);
        tvCiudadVivir = root.findViewById(R.id.tvCiudadVivir);
        lInfo = root.findViewById(R.id.lInfo);
        Anuncio a = null;
        Guia g = null;
        try {
            if(prefs.getBoolean("conectividad",false)){
                a = controlador.obtenerAnunciosID(prefs.getString("idanuncio",""));
                g = controlador.obtenerGuia(prefs.getString("token",""));
            }else{
                a = controladorBDLocal.obtenerAnunciosID(Integer.parseInt(prefs.getString("idanuncio","")));
                g = controladorBDLocal.obtenerGuia(prefs.getString("token",""));
                System.out.println("QUE GE ME TRAES "+ g.toString());
            }
            String[] acompanantesArray = a.getAcompanantes().split("-");
            tvTituloPAnuncio.setText(a.getNombre());
            tvNombreGuia.setText(prefs.getString("propuesta","-1"));
            tvCiudadVivir.setText(g.getCiudad());
            String acompanantes = "";
            for (int i = 0; i!=acompanantesArray.length;i++){
                acompanantes += acompanantesArray[i];
            }
            tvAcompanante.setText(acompanantes);
            String [] fecha1 = a.getFecha1().toString().split(" ");
            String[] fecha2 = a.getFecha2().toString().split(" ");

            tvFecha.setText(sacarFecha(fecha1).get(2) + ". "  + sacarFecha(fecha1).get(1) +  ". " + sacarFecha(fecha1).get(5) + "\n" +
                    sacarFecha(fecha2).get(2) + ". " + sacarFecha(fecha2).get(1) + ". " + sacarFecha(fecha2).get(5)
            );
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
            long dias = DAYS.between(f1,f2);

            System.out.println(a.getId() + " a. get ide");
            Propuesta p = null;
            if(prefs.getBoolean("conectividad",false)){
                System.out.println("HOLA?");
                System.out.println("ID ANUNCIO "+ a.getId());
                System.out.println("ID GUIA "+ a.getIdGuia());
                p = controlador.obtenerPropuestaIDAIDGuia2(a.getId(),controlador.obtenerGuia(prefs.getString("token","")).getId()).get(0);

            }else{
                System.out.println("ID ANUNCIO "+ a.getId());
                System.out.println("ID GUIA "+ a.getIdGuia());
                p = controladorBDLocal.obtenerPropuestaIDAnuncioIDGuia(a.getId(),a.getIdGuia()).get(0);
            }
            //System.out.println(controlador.obtenerPropuestaIDAIDGuia(a.getId(),controlador.obtenerGuia(prefs.getString("token","")).getId()));
            ArrayList<String> Dias = new ArrayList<>(Arrays.asList(p.getMensaje().split("~")));
            ArrayList<String>infodias = new ArrayList<>();
            String [] dia;
            for (int i = 0; i < Dias.size();i++){
                dia = Dias.get(i).split("=");
                for (int j = 0; j< dia.length;j++){
                    infodias.add(dia[j]);
                }
            }

            String detalle;
            if (infodias.get(0).equals("vacio")){
                detalle = "";
            }else{
                detalle = infodias.get(0);
            }
            tvMensajes.setText(detalle);
            Hashtable<Integer, String> dicDias = new Hashtable<Integer, String>();
            for(int i=1;i!=infodias.size();i++){
                if(infodias.get(i).matches("[0-9]*")){
                    if(infodias.get(i+1).equals("vacio")){
                        dicDias.put(Integer.valueOf(infodias.get(i))," ");
                    }else{
                        dicDias.put(Integer.valueOf(infodias.get(i)),infodias.get(i+1));
                    }
                }
            }

            LinearLayout contenedorInfo = new LinearLayout(contexto);
            contenedorInfo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contenedorInfo.setOrientation(LinearLayout.VERTICAL);
            for (int i = 1; i <= dias; i++ ){

                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.setMargins(5,10,5,10);
                TextView tv = new TextView(contexto);
                tv.setLayoutParams(layoutParams1);
                tv.setTextAppearance(getResources().getIdentifier("textos","style",contexto.getPackageName()));
                tv.setText(getResources().getString(R.string.dia)+ " "+ i);
                tv.setId(i);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                //contenedorEstatico.addView(tv);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.setMargins(5,10,5,10);
                TextView tv2 = new TextView(contexto);
                tv2.setLayoutParams(layoutParams2);
                tv2.setTextAppearance(getResources().getIdentifier("textonormal","style",contexto.getPackageName()));
                tv2.setText(dicDias.get(i));
                tv2.setGravity(Gravity.CENTER_HORIZONTAL);
                contenedorInfo.addView(tv);
                contenedorInfo.addView(tv2);
            }
            lInfo.addView(contenedorInfo);

        } catch (ServidorPHPException e) {
            e.printStackTrace();
        } catch( ParseException e){
            e.printStackTrace();
        }

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

}
