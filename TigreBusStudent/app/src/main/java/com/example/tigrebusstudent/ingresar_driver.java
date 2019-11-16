package com.example.tigrebusstudent;

import android.content.Intent;
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

public class ingresar_driver extends AppCompatActivity {

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
        setContentView(R.layout.ingresar_driver);

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
                    Toast.makeText(ingresar_driver.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
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

                                    Intent inicio = new Intent(getApplicationContext(), MapaDriver.class);
                                    startActivity(inicio);

                                }else {
                                    Toast.makeText(ingresar_driver.this, "Correo no verificado", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ingresar_driver.this,MainActivity.class));
                                }

                            } else {
                                Toast.makeText(ingresar_driver.this, "No se pudo iniciar sesion, complete los datos", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });




    }



//



}
