package com.example.tourguide.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alexfu.formvalidator.ValidationResult;
import com.alexfu.formvalidator.Validator;
import com.alexfu.formvalidator.rules.EmailRule;
import com.alexfu.formvalidator.rules.MinLengthRule;
import com.alexfu.formvalidator.rules.SonIguales;
import com.example.tourguide.MainActivity;
import com.example.tourguide.R;
import com.example.tourguide.controladores.ControladorBaseDatos;
import com.example.tourguide.controladores.ServidorPHPException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class Registrarse extends AppCompatActivity implements  Validator.Callback {

    private EditText etRNombre,etRApellido1,etREmail,etRPswd1,etRPswd2;
    private Button bVolver,registrarse;
    private Spinner spProvincia, spCiudad;
    private FirebaseAuth mAuth;
    private ControladorBaseDatos controlador;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Validator validator = new Validator();
    private Boolean validacionRegistro = false;

    /**
     * Rellena un spiner pasandole como parametro el spinner y el recurso
     * @param spin
     * @param recurso
     */
    public void rellenarSpinner(Spinner spin, ArrayList<String> recurso){
        Resources res = getResources();
        spin.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,recurso));
    }


    public boolean insertarGuia(FirebaseUser user, String pass) throws ServidorPHPException {
        return controlador.insertarGuia(user.getUid(),spCiudad.getSelectedItem().toString(),spProvincia.getSelectedItem().toString(),etRNombre.getText().toString(),etRApellido1.getText().toString(),etREmail.getText().toString(), pass);
    }

    public boolean insertarTurista(FirebaseUser user, String pass) throws ServidorPHPException {
        return controlador.insertarTurista(user.getUid(),etRNombre.getText().toString(),etRApellido1.getText().toString(),etREmail.getText().toString(),spCiudad.getSelectedItem().toString(), pass);
    }

    public void agregarUsuarios(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            try {
                                if(insertarTurista(user,etRPswd1.getText().toString())) {
                                    Intent intent = new Intent(getBaseContext(), Login.class);
                                    startActivity(intent);
                                    Toasty.success(getBaseContext(), "Usuario creado", Toast.LENGTH_SHORT, true).show();
                                }
                                else {
                                    Toasty.success(getBaseContext(), "Ha ocurrido un error, vuelva a intentarlo nuevamente", Toast.LENGTH_SHORT, true).show();
                                }
                                if(insertarGuia(user,etRPswd1.getText().toString())) {
                                    Intent intent = new Intent(getBaseContext(), Login.class);
                                    startActivity(intent);
                                    Toasty.success(getBaseContext(), "Usuario creado", Toast.LENGTH_SHORT, true).show();
                                }
                                else {
                                    Toasty.success(getBaseContext(), "Ha ocurrido un error, vuelva a intentarlo nuevamente", Toast.LENGTH_SHORT, true).show();
                                }

                            } catch (ServidorPHPException e) {
                                e.printStackTrace();
                            }

                        }else{
                            Toasty.warning(getBaseContext(), "Este usuario ya existe", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);


        mAuth = FirebaseAuth.getInstance();
        registrarse = findViewById(R.id.bRegistrarse);
        etRApellido1 = findViewById(R.id.etRApellido1);
        etREmail = findViewById(R.id.etREmail);
        etRNombre = findViewById(R.id.etRNombre);
        etRPswd1 = findViewById(R.id.etRPswd1);
        etRPswd2 = findViewById(R.id.etRPswd2);
        spCiudad = findViewById(R.id.spCiudad);
        spProvincia = findViewById(R.id.spProvincia);
        controlador = new ControladorBaseDatos();

        try {
            rellenarSpinner(spProvincia,controlador.obtenerProvincias());
        } catch (ServidorPHPException e) {
            e.printStackTrace();
        }

        spProvincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    rellenarSpinner(spCiudad,controlador.obtenerCiudades(spProvincia.getSelectedItem().toString()));
                } catch (ServidorPHPException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setUpValidator();


    }



    /**
     * Pone las reglas para validar
     */
    private void setUpValidator() {
        validator.setCallback(this);
        validator.addRule(etRNombre, new MinLengthRule(1, "Este campo es obligatorio"));
        validator.addRule(etRApellido1, new MinLengthRule(1, "Este campo es obligatorio"));
        validator.addRule(etREmail, new EmailRule("Introduzca un email valido"));
        validator.addRule(etRPswd1,new MinLengthRule(8,"Tiene que tener un minimo de 8 caracteres"));
        validator.addRule(etRPswd2,new MinLengthRule(8,"Tiene que tener un minimo de 8 caracteres"));

        // Form validation

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
                if (!etRPswd1.getText().toString().equals(etRPswd2.getText().toString())){
                    validator.addRule(etRPswd2,new SonIguales("Las dos contraseñas deben de ser iguales"));
                }else{
                    agregarUsuarios(etREmail.getText().toString(),etRPswd1.getText().toString());
                }
            }
        });
    }

    /**
     * Cada vez que escribes comprueba... ya haces lo que quieras
     */
    @Override public void onBeginFormValidation() {

    }

    /**
     * Comprueba el campo
     * @param result
     */
    @Override public void onFieldValidated(@NonNull ValidationResult result) { }

    /**
     * Cuando la validacion esta bien
     */
    @Override
    public void onSuccessValidation() {
        validacionRegistro = true;
    }

    /**
     * Cuando la validacion esta mal
     * @param errors
     */
    @Override
    public void onFailedValidation(@NonNull List<ValidationResult> errors) {
        validacionRegistro = false;
    }

}
