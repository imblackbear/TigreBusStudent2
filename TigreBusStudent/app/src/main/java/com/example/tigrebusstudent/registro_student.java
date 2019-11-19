package com.example.tigrebusstudent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class registro_student extends AppCompatActivity {
    //referencias de cada campo del XML
    private EditText  txt_telefonoeme, txt_matricula,txt_nombre,txt_apellidos,txt_escuela,txt_telefono,txt_correo,txt_password;
    private Button bt_registrar, bt_iniciarse;
    private ProgressDialog progressDialog;      //declarando variable PD

    //variables de los datos a registrar del alumno
    private String matricula = "";
    private String nombre = "";
    private String apellidos = "";
    private String escuela = "";
    private String telefono = "";
    private String correo = "";
    private String password = "";
    private String telefonoeme= "";


    //Objeto de FireBase
    FirebaseAuth Auth;
    //Objeto para utilizar la base de datos Realtime de Database
    DatabaseReference Database;


    //AlertDialog
    private ImageButton alertDialog;
    private ProgressDialog ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_student);

        //Cambiar de color la barra de estado
        int myColor = Color.parseColor("#00629F");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(myColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(myColor);
        }

        //instanciar ProgressDialog
        this.progressDialog = new ProgressDialog(this);
        this.ad = new ProgressDialog(this);



        //instanciar AlertDialog
        alertDialog = (ImageButton)findViewById(R.id.ad_info);

        //Creacion del AlrteDialog
        alertDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mostrarAD();

            }
        });


        //instanciar el objeto de FireBase
        Auth = FirebaseAuth.getInstance();

        //instanciar objeto Database y hacer referencia al nodo principal de nuestra base de datos
        Database = FirebaseDatabase.getInstance().getReference();


        //instanciar cada EditText
        txt_matricula = (EditText)findViewById(R.id.et_matricula);
        txt_nombre = (EditText)findViewById(R.id.et_nombre2);
        txt_apellidos = (EditText)findViewById(R.id.et_apellidos2);
        txt_escuela = (EditText)findViewById(R.id.et_escuela2);
        txt_telefono = (EditText)findViewById(R.id.et_telefono);
        txt_correo = (EditText)findViewById(R.id.et_correo2);
        txt_password = (EditText)findViewById(R.id.et_password2);
        txt_telefonoeme = (EditText)findViewById(R.id.et_telefonoeme);

        bt_registrar = (Button)findViewById(R.id.bt_registrarse2);
        bt_iniciarse = (Button)findViewById(R.id.bt_iniciarse);

        //metodo del boton para que se realice el registro
        bt_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //obtener datos
                matricula = txt_matricula.getText().toString();
                nombre = txt_nombre.getText().toString();
                apellidos = txt_apellidos.getText().toString();
                escuela = txt_escuela.getText().toString();
                telefono = txt_telefono.getText().toString();
                correo = txt_correo.getText().toString();
                password = txt_password.getText().toString();
                telefonoeme = txt_telefonoeme.getText().toString();


                //validacion para saber si el usuario ingreso valores a los EditText
                if(!matricula.isEmpty() && !nombre.isEmpty() && !apellidos.isEmpty() && !escuela.isEmpty() && !telefono.isEmpty() && !correo.isEmpty() &&!password.isEmpty() && !telefonoeme.isEmpty()){

                    //Firebase requiere de almenos 6 caracteres en la contraseña
                    if(password.length() >= 6 ){
                        //Ejecutara el metodo registrarUsuario
                        registrarDriver();
                    }else {
                        Toasty.warning(registro_student.this, "La contraseña debe de tener almenos 6 caracteres", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toasty.warning(registro_student.this, "Debe completar todos los campos", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Metodo para ir a la activity de iniciar sesion
        bt_iniciarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registro_student.this, ingresar_student.class));
            }
        });




    }

    //metodo para registrar usuario
    private void registrarDriver()
    {
        mostrarPD();
        //metodo crear usuario con correo y contraseña y validacion con metodo
        Auth.createUserWithEmailAndPassword(correo,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //validacion para saber si la tarea fue exitosa y el conductor se registro correctamente
                if(task.isSuccessful()){


                    Map<String, Object> map = new HashMap<>();
                    map.put("Matricula",matricula);
                    map.put("Nombre",nombre);
                    map.put("Apellidos",apellidos);
                    map.put("Escuela",escuela);
                    map.put("Telefono",telefono);
                    map.put("Correo",correo);
                    map.put("Contraseña",password);
                    map.put("Telefono de advertencia",telefonoeme);


                    //Obtener Id proporcionado de cada usuario de Database
                    String id = Auth.getCurrentUser().getUid();


                    Database.child("Usuarios").child("Alumnos").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                //verificacion de correo
                                FirebaseUser usuario = Auth.getCurrentUser();
                                usuario.sendEmailVerification();
                                //quitar if
                                if(!usuario.isEmailVerified()){
                                    Toasty.info(registro_student.this, "Verifica tu cuenta en el correo electrónico ingresado", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(registro_student.this, ingresar_student.class));
                                }


                            }else {
                                Toasty.warning(registro_student.this, "No se pudieron registrat los datos correctamente", Toast.LENGTH_LONG).show();
                            }
                            //finalizarlo
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    Toasty.warning(registro_student.this, "No se pudo registrar este usuario, verifica los datos", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    //creacion del ProgressDialog
    private void mostrarPD(){
       progressDialog.setCancelable(false);
       progressDialog.show();
       progressDialog.setContentView(R.layout.pdcontenido);
    }

    //creacion del ProgressDialog
    private void mostrarAD(){
        ad.setCancelable(true);
        ad.show();
        ad.setContentView(R.layout.ad_info);
        Button ad_btn = ad.findViewById(R.id.bt_ad);
        ad_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });


    }

}
