package com.example.tigrebusstudent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import es.dmoral.toasty.Toasty;

public class ingresar_student extends AppCompatActivity {

    private EditText et_correo,et_password;
    private Button bt_ingresar;

    //datos necesarios para iniciar sesion
    private String correo  = "";
    private String password = "";

    //variable Firebase Auth
    FirebaseAuth Auth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingresar_student);

        //Cambiar de color la barra de estado
        int myColor = Color.parseColor("#00629F");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(myColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(myColor);
        }

        //instanciar
        et_correo =(EditText)findViewById(R.id.txt_correo2);
        et_password = (EditText)findViewById(R.id.txt_password2);
        bt_ingresar = (Button)findViewById(R.id.bt_ingresar2);


        //instanciar variable firebase
        Auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //metodo de iniciar sesion para el boton
       bt_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //obtener valores ingresados por el usuario
                correo = et_correo.getText().toString();
                password = et_password.getText().toString();

                //validacion para ver si los campos estan vacios
                if(!correo.isEmpty() && !password.isEmpty()){
                    ingresarDriver();
                    
                }else {
                    Toasty.warning(getApplicationContext(),"Complete todos los campos",Toast.LENGTH_LONG).show();
                }

            }
        });


    }


    private void ingresarDriver(){

                    Auth.signInWithEmailAndPassword(correo, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser usuario = Auth.getCurrentUser();

                                //comprobacion para comprobar si el correo ya fue verificado
                                    if (usuario.isEmailVerified()){

                                        Intent inicio = new Intent(getApplicationContext(), MapaStudent.class);
                                        startActivity(inicio);

                                    }else {
                                        Toasty.warning(ingresar_student.this, "Correo no verificado", Toast.LENGTH_LONG).show();
                                    }

                            } else {
                                Toasty.error(ingresar_student.this, "No se pudo iniciar sesion, complete los datos", Toast.LENGTH_LONG).show();
                            }

                        }
                    });


    }


}
