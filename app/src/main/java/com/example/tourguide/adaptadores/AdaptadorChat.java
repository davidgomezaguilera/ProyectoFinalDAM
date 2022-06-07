package com.example.tourguide.adaptadores;

import android.content.SharedPreferences;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourguide.R;
import com.example.tourguide.modelo.Mensaje;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.List;


import kotlin.jvm.internal.SpreadBuilder;

public class AdaptadorChat extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private SharedPreferences prefs;
    private final int ENVIAR = 1;
    private final int RECIBIR = 2;
    private final int SISTEMA = 3;
    private Context contexto;

    private List<Mensaje> mensajes = new ArrayList<>();

    public AdaptadorChat(Context contexto, List<Mensaje> mensajes) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
        this.contexto = contexto;
        this.mensajes = mensajes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        switch (viewType){
            case ENVIAR: viewHolder = new EnviarHolder(inflater.inflate(R.layout.chatemisor,parent,false));
                break;
            case RECIBIR: viewHolder = new RecibirHolder(inflater.inflate(R.layout.chatreceptor,parent,false));
                break;
            case SISTEMA: viewHolder = new SistemaHolder(inflater.inflate(R.layout.chatsistema,parent,false));
                break;
            default: viewHolder = new EnviarHolder(inflater.inflate(R.layout.chatemisor,parent));
        }
        return viewHolder;
    }

    /**
     * Agrega los datos que queramos mostrar
     * @param mensaje Datos a mostrar, en este caso, titulares
     */
    public void add(Mensaje mensaje)
    {
        mensajes.add(mensaje);
        refrescar();
    }

    public void refrescar()
    {
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case ENVIAR:
                Mensaje emisor = mensajes.get(position);
                EnviarHolder enviarHolder = (EnviarHolder)holder;
                enviarHolder.tvtextoemisor.setText(emisor.getMensaje());
                if(emisor.getTipo().equals("guia")){
                    enviarHolder.tvtextoemisor.setBackground(contexto.getResources().getDrawable(R.drawable.bordebocadilloguia));
                }else{
                    enviarHolder.tvtextoemisor.setBackground(contexto.getResources().getDrawable(R.drawable.bordebocadilloturista));
                }
                break;
            case RECIBIR:
                Mensaje receptor = mensajes.get(position);
                RecibirHolder recibirHolder = (RecibirHolder)holder;
                recibirHolder.tvtextoreceptor.setText(receptor.getMensaje());
                if(receptor.getTipo().equals("guia")){
                    recibirHolder.tvtextoreceptor.setBackground(contexto.getResources().getDrawable(R.drawable.bordebocadilloguia));
                }else{
                    recibirHolder.tvtextoreceptor.setBackground(contexto.getResources().getDrawable(R.drawable.bordebocadilloturista));
                }
                break;
            case SISTEMA:
                Mensaje sistema = mensajes.get(position);
                SistemaHolder sistemaholder = (SistemaHolder)holder;
                sistemaholder.tvSistema.setText(sistema.getMensaje());
        }


    }

    @Override
    public int getItemViewType(int position) {
        Mensaje mr = mensajes.get(position);
        int resultado;
        if(mr.getTipo().equals(prefs.getString("modo",""))){
            resultado = 1;
        }else if(mr.getTipo().equals("sistema")){
            resultado = 3;
        }else{
            resultado = 2;
        }
        return resultado;
        //return mensajes.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    class EnviarHolder extends RecyclerView.ViewHolder{
        TextView tvtextoemisor;
        LinearLayout lemisor;
        public EnviarHolder(View itemView) {
            super(itemView);
            tvtextoemisor = itemView.findViewById(R.id.tvtextoemisor);
            lemisor = itemView.findViewById(R.id.lcontenedoremisor);
        }
    }

    class RecibirHolder extends RecyclerView.ViewHolder{
        TextView tvtextoreceptor;
        LinearLayout lreceptor;
        public RecibirHolder(View itemView) {
            super(itemView);
            lreceptor = itemView.findViewById(R.id.lcontenedorreceptor);
            tvtextoreceptor = itemView.findViewById(R.id.tvtextoreceptor);
        }
    }

    class SistemaHolder extends RecyclerView.ViewHolder{
        TextView tvSistema;
        LinearLayout lSistema;
        public SistemaHolder(View itemView) {
            super(itemView);
            lSistema = itemView.findViewById(R.id.lLayoutSistema);
            tvSistema = itemView.findViewById(R.id.tvSistema);
        }
    }

}