package com.example.tourguide.adaptadores;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.tourguide.R;
import com.example.tourguide.controladores.ControladorBDLocal;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.GlideApp;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Propuesta;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AdaptadorMisAnuncios extends RecyclerView.Adapter<AdaptadorMisAnuncios.HolderMisAnuncios>{


    public class HolderMisAnuncios extends RecyclerView.ViewHolder
    {
        ImageView iProvincia;
        TextView tvFecha,tvCiudad,tvAcompanantes,tvTitulo,tvPropuestas;
        ConstraintLayout anunciosLayout;

        HolderMisAnuncios(View itemView)
        {
         /* En el constructor obtendremos los recursos del fichero
         de recursos xml que tengamos asociado a la clase, en
         este caso el fichero listitem_titular.xml
         */
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvfecha);
            tvCiudad = itemView.findViewById(R.id.tvCiudad);
            tvPropuestas = itemView.findViewById(R.id.tvpropuestas);
            tvAcompanantes = itemView.findViewById(R.id.tvAcompanantes);
            tvTitulo = itemView.findViewById(R.id.tvTituloanuncio);
            iProvincia = itemView.findViewById(R.id.iProvincia);
            anunciosLayout = itemView.findViewById(R.id.anunciosLayout);
            // Si hubiera que sobrecargar eventos se haria aqui

        }

        public void removeItem(int position)
        {
            try {
                controlador.eliminarAnuncio(arrayentrante.get(position).getId());
                arrayentrante.remove(position);
                notifyItemRemoved(position);
            } catch (ServidorPHPException e) {
                refrescar();
                Toasty.error(contexto, "Ups, ¿Qué pasó?", Toast.LENGTH_SHORT, true).show();
                e.printStackTrace();
            }
        }
    };

    private ArrayList<Anuncio> arrayentrante;
    private Context contexto;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ControladorBaseDatos controlador;
    private ControladorBDLocal controladorBDLocal;
    private String activador;
    protected View.OnClickListener onClickListener;

    public AdaptadorMisAnuncios(Context contexto, ArrayList<Anuncio> arrayentrante,String activador)
    {
        this.contexto = contexto;
        this.arrayentrante = arrayentrante;
        this.activador = activador;
        prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
        editor = prefs.edit();
        controlador = new ControladorBaseDatos();
        controladorBDLocal = new ControladorBDLocal(contexto);
    }

    /**
     * Cuando clicamos en el hijo de un recview
     * @param onClickListener
     */
    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * Agrega los datos que queramos mostrar
     * @param arrayentrante Datos a mostrar, en este caso, titulares
     */
    public void add(ArrayList<String> arrayentrante)
    {
        arrayentrante.clear();
        arrayentrante.addAll(arrayentrante);
    }
    /**
     * Actualiza los datos del ReciclerView
     */
    public void refrescar()
    {
        notifyDataSetChanged();
    }

    @Override
    public HolderMisAnuncios onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mis_anuncios, parent, false);
        v.setOnClickListener(onClickListener);
        HolderMisAnuncios pvh = new HolderMisAnuncios(v);
        return pvh;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final HolderMisAnuncios titulardeturno, final int position)
    {
        String[] acompanantesArray = arrayentrante.get(position).getAcompanantes().split("-");
        String [] fecha1 = arrayentrante.get(position).getFecha1().toString().split(" ");
        String[] fecha2 = arrayentrante.get(position).getFecha2().toString().split(" ");

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

        if (activador.equals("guia")) {
            titulardeturno.anunciosLayout.setBackground(contexto.getResources().getDrawable(R.drawable.bordebotonesguia));
            titulardeturno.tvPropuestas.setVisibility(View.GONE);
            try {
                if(prefs.getBoolean("conectividad",false)){

                    if (controlador.obtenerPropuestaIDAIDGuia(arrayentrante.get(position).getId(),controlador.obtenerGuia(prefs.getString("token","")).getId()).size()==1){
                        titulardeturno.anunciosLayout.setBackground(contexto.getResources().getDrawable(R.drawable.bordebotonespropuestarellena));
                    }
                }else{

                    if (controladorBDLocal.obtenerPropuestaIDAnuncioIDGuia(arrayentrante.get(position).getId(),controladorBDLocal.obtenerGuia(prefs.getString("token","")).getId()).size()==1){
                        titulardeturno.anunciosLayout.setBackground(contexto.getResources().getDrawable(R.drawable.bordebotonespropuestarellena));
                    }
                }
            } catch (ServidorPHPException e) {
                e.printStackTrace();
            }
        } else if(activador.equals("turista")){
            titulardeturno.anunciosLayout.setBackground(contexto.getResources().getDrawable(R.drawable.bordebotonesturista));
        }else if (activador.equals("guiah")){
            titulardeturno.anunciosLayout.setBackground(contexto.getResources().getDrawable(R.drawable.bordebotonesguia));
            titulardeturno.tvPropuestas.setVisibility(View.GONE);
        }
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(25));
        String foto = null;
        try {
            foto = controlador.obtenerFotoProvincia(arrayentrante.get(position).getCiudad());
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }
        GlideApp.with(contexto)
                .load(foto+".jpg")
                .error(R.drawable.ciudad1)
                .apply(requestOptions)
                .into(titulardeturno.iProvincia);
        try {
            if(prefs.getBoolean("conectividad",false)){
                titulardeturno.tvTitulo.setText(arrayentrante.get(position).getNombre() + controlador.obtenerPropuestaIDA(arrayentrante.get(position).getId()).size());
            }else{
                titulardeturno.tvTitulo.setText(arrayentrante.get(position).getNombre() + controladorBDLocal.obtenerPropuestaIDAnuncio(arrayentrante.get(position).getId()).size());
            }
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }
        try {
            if(prefs.getBoolean("conectividad",false)){
                titulardeturno.tvPropuestas.setText(contexto.getResources().getString(R.string.tienes)+ " " +
                        controlador.obtenerPropuestaIDA(arrayentrante.get(position).getId()).size() + " " +
                        (controlador.obtenerPropuestaIDA(arrayentrante.get(position).getId()).size()!=1?contexto.getResources().getString(R.string.npropuestas):contexto.getResources().getString(R.string.npropuesta)));

            }else{
                titulardeturno.tvPropuestas.setText(contexto.getResources().getString(R.string.tienes)+ " " +
                        controladorBDLocal.obtenerPropuestaIDAnuncio(arrayentrante.get(position).getId()).size() + " " +
                        (controladorBDLocal.obtenerPropuestaIDAnuncio(arrayentrante.get(position).getId()).size()!=1?contexto.getResources().getString(R.string.npropuestas):contexto.getResources().getString(R.string.npropuesta)));

            }
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }
        titulardeturno.tvFecha.setText(fecha1[2] + ". "  + f1_mes +  ". " + fecha1[5] + " - " + fecha2[2] + ". " + f2_mes + ". " + fecha2[5]);
        titulardeturno.tvCiudad.setText(arrayentrante.get(position).getCiudad());
        titulardeturno.tvAcompanantes.setText(acompanantesArray.length + " acompañantes");

        titulardeturno.tvAcompanantes.setText((acompanantesArray.length==1)?acompanantesArray.length + " acompañante":acompanantesArray.length + " acompañantes");
    }

    @Override
    public int getItemCount()
    {
        return arrayentrante.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

