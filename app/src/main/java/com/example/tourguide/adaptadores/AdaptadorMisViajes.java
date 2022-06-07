package com.example.tourguide.adaptadores;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.GlideApp;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AdaptadorMisViajes extends RecyclerView.Adapter<AdaptadorMisViajes.HolderMisViajes>{


    public class HolderMisViajes extends RecyclerView.ViewHolder
    {
        ImageView iProvincia;
        TextView tvTitulo;
        ConstraintLayout anunciosLayout;

        HolderMisViajes(View itemView)
        {
         /* En el constructor obtendremos los recursos del fichero
         de recursos xml que tengamos asociado a la clase, en
         este caso el fichero listitem_titular.xml
         */
            super(itemView);
            iProvincia = itemView.findViewById(R.id.iProvinciaViaje);
            anunciosLayout = itemView.findViewById(R.id.anuncioslayoutViaje);
            tvTitulo = itemView.findViewById(R.id.tvTituloanuncioViaje);
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
    private String activador;
    protected View.OnClickListener onClickListener;

    public AdaptadorMisViajes(Context contexto, ArrayList<Anuncio> arrayentrante, String activador)
    {
        this.contexto = contexto;
        this.arrayentrante = arrayentrante;
        this.activador = activador;
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
    public HolderMisViajes onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.misviajes, parent, false);
        v.setOnClickListener(onClickListener);
        HolderMisViajes pvh = new HolderMisViajes(v);
        return pvh;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final HolderMisViajes titulardeturno, final int position) {

        if (activador.equals("guia")) {
            titulardeturno.anunciosLayout.setBackground(contexto.getResources().getDrawable(R.drawable.bordebotonesguia));
        } else if (activador.equals("turista")) {
            titulardeturno.anunciosLayout.setBackground(contexto.getResources().getDrawable(R.drawable.bordebotonesturista));
        }
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(25));
        String foto = null;
        titulardeturno.tvTitulo.setText(arrayentrante.get(position).getNombre());
        try {
            foto = controlador.obtenerFotoProvincia(arrayentrante.get(position).getCiudad());
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }
        GlideApp.with(contexto)
                .load(foto + ".jpg")
                .error(R.drawable.ciudad1)
                .apply(requestOptions)
                .into(titulardeturno.iProvincia);
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