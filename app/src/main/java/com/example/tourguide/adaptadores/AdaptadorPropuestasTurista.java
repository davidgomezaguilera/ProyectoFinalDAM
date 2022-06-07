package com.example.tourguide.adaptadores;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.library.SmartRatingBar;
import com.bumptech.glide.request.RequestOptions;
import com.example.tourguide.R;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.GlideApp;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Propuesta;

import java.util.ArrayList;

public class AdaptadorPropuestasTurista extends RecyclerView.Adapter<AdaptadorPropuestasTurista.HolderPropuestas>{


    public class HolderPropuestas extends RecyclerView.ViewHolder
    {
        TextView tvNombreGuia;
        ImageView iGuia,iEstado;
        SmartRatingBar smartRatingBar;

        HolderPropuestas(View itemView)
        {
         /* En el constructor obtendremos los recursos del fichero
         de recursos xml que tengamos asociado a la clase, en
         este caso el fichero listitem_titular.xml
         */
            super(itemView);
            tvNombreGuia = itemView.findViewById(R.id.tvNombreGuia);
            smartRatingBar = itemView.findViewById(R.id.smart_rating_bar);
            iGuia = itemView.findViewById(R.id.iGuia);
            iEstado = itemView.findViewById(R.id.iEstado);
            // Si hubiera que sobrecargar eventos se haria aqui

        }

        public void removeItem(int position)
        {
            arrayentrante.remove(position);
            notifyItemRemoved(position);
        }
    };



    private ArrayList<Propuesta> arrayentrante;
    private Context contexto;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ControladorBaseDatos controlador;
    protected View.OnClickListener onClickListener;

    public AdaptadorPropuestasTurista(Context contexto, ArrayList<Propuesta> arrayentrante)
    {
        this.contexto = contexto;
        this.arrayentrante = arrayentrante;
        prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
        editor = prefs.edit();
        controlador = new ControladorBaseDatos();
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
    public HolderPropuestas onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mispropuestasturista, parent, false);
        v.setOnClickListener(onClickListener);
        HolderPropuestas pvh = new HolderPropuestas(v);
        return pvh;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final HolderPropuestas titulardeturno, final int position)
    {
        try {
            GlideApp.with(contexto)
                    .load("http://192.168.18.25/TourGuide/fotos/"+controlador.obtenerGuiaID(arrayentrante.get(position).getIdGuia()).getFoto()+".jpg")
                    .error(R.drawable.iconoguiaestandar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(titulardeturno.iGuia);
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }
        titulardeturno.smartRatingBar.setRatingNum(arrayentrante.get(position).getGuia().getPuntuacion().floatValue());
        switch (arrayentrante.get(position).getEstado()){
            case Entregado:
                titulardeturno.iEstado.setImageDrawable(contexto.getResources().getDrawable(R.drawable.entregado));
                break;
            case Leido:
                titulardeturno.iEstado.setImageDrawable(contexto.getResources().getDrawable(R.drawable.abierto));
                break;
            case Rechazado:
                titulardeturno.iEstado.setImageDrawable(contexto.getResources().getDrawable(R.drawable.rechazar));
                break;
            case Aceptado:
                titulardeturno.iEstado.setImageDrawable(contexto.getResources().getDrawable(R.drawable.aceptado));
                break;
        }
        titulardeturno.tvNombreGuia.setText(arrayentrante.get(position).getGuia().getNombre());


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

