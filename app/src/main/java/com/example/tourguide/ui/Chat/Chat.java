package com.example.tourguide.ui.Chat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourguide.R;
import com.example.tourguide.adaptadores.AdaptadorChat;
import com.example.tourguide.adaptadores.SpaceItemDecoration;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.ServidorPHPException;
import com.example.tourguide.modelo.Anuncio;
import com.example.tourguide.modelo.Mensaje;
import com.example.tourguide.modelo.Propuesta;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class Chat extends Fragment {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context contexto;
    private Activity activity;
    private ControladorBaseDatos controlador;
    private RecyclerView recViewChat;
    private ArrayList<Mensaje> datos;
    private ImageButton bEnviar,badjuntar;
    private EditText etmensaje;
    private AdaptadorChat adaptador;
    private ProgressBar pbar;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    private void scrolearrecview(){
        recViewChat.scrollToPosition(adaptador.getItemCount()-1);
    }

    public static Chat newInstance() {
        return new Chat();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.chat_fragment, container, false);
        contexto = getContext();
        activity = getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
        editor = prefs.edit();
        controlador = new ControladorBaseDatos();
        try {
            Propuesta propuesta = controlador.obtenerPropuestaID(prefs.getString("propuesta",""));
            Anuncio anuncio = controlador.obtenerAnunciosID(propuesta.getIdAnuncio());
            database = FirebaseDatabase.getInstance();
            //Se genera una nueva rama en firebase con el idGuia + idTurista + idAnuncio + idPropuesta
            databaseReference = database.getReference(propuesta.getGuia().getToken()+anuncio.getTurista().getToken()+anuncio.getId()+propuesta.getId());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Mensaje post = dataSnapshot.getValue(Mensaje.class);
                    //si el post es nulo, quiere decir que es la primera entrada al chat por lo que colocamos mensaje de bienvenida
                    if(post == null){
                        databaseReference.push().setValue(new Mensaje(prefs.getString("nombre",""),"sistema","Bienvenido a la sala",null,"texto"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toasty.error(contexto,databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });


        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }

        pbar = root.findViewById(R.id.pbLoading);
        recViewChat = root.findViewById(R.id.recViewChat);
        badjuntar = root.findViewById(R.id.ibUpload);
        bEnviar = root.findViewById(R.id.ibSend);
        etmensaje = root.findViewById(R.id.etMensaje);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel

        datos = new ArrayList<>();
        // Hay que crear en la carpeta values un fichero dimens.xml y crear ahí list_space
        recViewChat.addItemDecoration(new SpaceItemDecoration(contexto, R.dimen.list_space, true, true));
        // Con esto el tamaño del recyclerwiew no cambiará
        recViewChat.setHasFixedSize(true);
        // Creo un layoutmanager para el recyclerview
        recViewChat.setLayoutManager(new LinearLayoutManager(contexto,LinearLayoutManager.VERTICAL, false));
        adaptador = new AdaptadorChat(contexto,datos);
        recViewChat.setAdapter(adaptador);
        adaptador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                scrolearrecview();
            }
        });
        adaptador.refrescar();

        bEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = etmensaje.getText().toString();
                etmensaje.setText("");
                databaseReference.push().setValue(new Mensaje(prefs.getString("nombre",""),prefs.getString("modo",""),mensaje,null,"texto"));

            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Mensaje m = dataSnapshot.getValue(Mensaje.class);
                adaptador.add(m);
                scrolearrecview();
                pbar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
